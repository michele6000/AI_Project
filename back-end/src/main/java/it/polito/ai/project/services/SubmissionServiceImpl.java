package it.polito.ai.project.services;

import it.polito.ai.project.dtos.SolutionDTO;
import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.SubmissionDTO;
import it.polito.ai.project.entities.Course;
import it.polito.ai.project.entities.Solution;
import it.polito.ai.project.entities.Student;
import it.polito.ai.project.entities.Submission;
import it.polito.ai.project.exceptions.*;
import it.polito.ai.project.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private NotificationService notification;

    @Autowired
    private SubmissionRepository submissionRepo;

    @Autowired
    private SolutionRepository solutionRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private ProfessorRepository profRepo;

    @Qualifier("threadPoolTaskExecutor")
    @Autowired
    private TaskExecutor executor;

    @Override
    public SubmissionDTO addSubmission(SubmissionDTO submissionDTO, String courseName, String profId, MultipartFile submissionFile) {

        if (submissionDTO == null)
            throw new SubmissionNotFoundException("Submission not found!");
        if (courseName.length() == 0 || !courseRepo.findById(courseName).isPresent())
            throw new CourseNotFoundException("Course not found!");

        Submission submissionEntity = modelMapper.map(submissionDTO, Submission.class);
        Course course = courseRepo.getOne(courseName);

        if (!course.isEnabled())
            throw new CourseDisabledException("Course not enabled!");

        if (!course.getProfessors().contains(profRepo.getOne(profId))) {
            submissionRepo.delete(submissionEntity);
            throw new TeamServiceException("You are not a professor of this course!");
        }

        try{
            Byte[] byteObjects = new Byte[submissionFile.getBytes().length];
            int i = 0;
            for (byte b : submissionFile.getBytes())
                byteObjects[i++] = b;

            submissionEntity.setImage(byteObjects);
        } catch (IOException e) {
            throw new TeamServiceException("Error saving image: " + submissionFile.getName());
        }

        submissionEntity.setCourse(course);
        submissionEntity = submissionRepo.save(submissionEntity);
        course.addSubmission(submissionEntity);

        Submission finalSubmissionEntity = submissionEntity;
        executor.execute(() -> {
            for (Student s : course.getStudents())
                notification.sendMessage(
                        s.getEmail(),
                        "Submission",
                        "The professor created a new submission for you.\n" +
                                "Submission id:" + finalSubmissionEntity.getId() + "\n" +
                                "Expiry date: " + finalSubmissionEntity.getExpiryDate()
                );
        });


        return modelMapper.map(submissionEntity, SubmissionDTO.class);
    }

    @Override
    public List<SubmissionDTO> getAllSubmissions(String courseName, String username) {

        if (!courseRepo.findById(courseName).isPresent())
            throw new CourseNotFoundException("Course not found!");
        if (!courseRepo.getOne(courseName).isEnabled())
            throw new CourseDisabledException("Course not enabled!");

        return courseRepo
                .getOne(courseName)
                .getSubmissions().stream()
                .sorted(Comparator.comparing(Submission::getExpiryDate))
                .map(s -> modelMapper.map(s, SubmissionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public SubmissionDTO getSubmission(String courseName, Long submissionId, String studentId) {

        if (!courseRepo.findById(courseName).isPresent())
            throw new CourseNotFoundException("Course not found!");
        if (!submissionRepo.findById(submissionId).isPresent())
            throw new SubmissionNotFoundException("Submission not found!");

        if (!profRepo.findById(studentId).isPresent()) { //a student is requiring submission
            if(getLastSolVersion(submissionId,studentId).isRevisable())
                if (studentRepo.getOne(studentId).getSolutions().stream()
                        .filter(solution -> solution.getVersion() >= 0)
                        .noneMatch(sol -> sol.getSubmission().getId().equals(submissionId))) {
                    //no solution for this submission and this student-->create an empty solution with status "READ"
                    List<Solution> ReadSol = submissionRepo.getOne(submissionId).getSolutions().stream()
                            .filter(sol -> sol.getStudent().getId().equals(studentId))
                            .filter(sol -> sol.getVersion() == -1)
                            .filter(sol -> sol.getSubmission().getId().equals(submissionId))
                            .collect(Collectors.toList() );
                    if (ReadSol.size() > 0){
                        submissionRepo.getOne(submissionId).getSolutions().remove(ReadSol.get(0));
                        solutionRepo.delete(ReadSol.get(0));
                    }
                    if (!submissionRepo.getOne(submissionId).getExpiryDate().before(new Date()))
                        createNewSol(studentId, submissionId);
                }
        }

        return modelMapper.map(submissionRepo.getOne(submissionId), SubmissionDTO.class);
    }


    @Override
    public SolutionDTO addSolution(Long submissionId, SolutionDTO solutionDTO, String studentId, MultipartFile solutionFile) {

        if (solutionDTO == null)
            throw new SolutionNotFoundException("Solution not found!");

        if (!submissionRepo.findById(submissionId).isPresent())
            throw new SubmissionNotFoundException("Submission not found!");

        Submission submission = submissionRepo.getOne(submissionId);

        if (submission.getExpiryDate().before(new Date()))
            throw new SubmissionExpiredException("This submission expired! You cannot submit solutions now.");

        if (!getLastSolVersion(submissionId,studentId).isRevisable())
            throw new NotRevisableException("Professor has stopped review! You cannot submit any solutions.");

        if (studentId.length() == 0 || !studentRepo.findById(studentId).isPresent())
            throw new StudentNotFoundException("Student not found!");

        Solution solution = modelMapper.map(solutionDTO, Solution.class);
        Student student = studentRepo.getOne(studentId);

        if (!student.getCourses().contains(submission.getCourse()))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        List<Solution> ReadSol = submission.getSolutions().stream()
                .filter(sol -> sol.getStudent().getId().equals(studentId))
                .filter(sol -> sol.getVersion() == 0 || sol.getVersion() == -1)
                .filter(sol -> sol.getSubmission().getId().equals(submissionId))
                .collect(Collectors.toList() );
        if (ReadSol.size() > 0){
            submission.getSolutions().remove(ReadSol.get(0));
            solutionRepo.delete(ReadSol.get(0));
        }

        long version = student.getSolutions()
                .stream()
                .filter(sol -> sol.getSubmission().getId().equals(submissionId))
                .filter(sol -> sol.getVersion() != 0)
                .filter(sol -> sol.getVersion() != -1)
                .count();

        solution.setSubmission(submission);
        solution.setStatus("SUBMITTED");
        solution.setRevisable(true);
        solution.setStudent(student);
        solution.setVersion((int) (version + 1L));

        try{
            Byte[] byteObjects = new Byte[solutionFile.getBytes().length];
            int i = 0;
            for (byte b : solutionFile.getBytes())
                byteObjects[i++] = b;

            solution.setImage(byteObjects);
        } catch (IOException e) {
            throw new TeamServiceException("Error saving image: " + solutionFile.getName());
        }

        solution = solutionRepo.save(solution);
        student.addSolution(solution);

        return modelMapper.map(solution,SolutionDTO.class);
    }


    @Override
    public List<SolutionDTO> getAllSolutions(Long submissionId) { //all students for that submission (only last version)
        if (!submissionRepo.findById(submissionId).isPresent())
            throw new SubmissionNotFoundException("Submission not found!");

        return submissionRepo.getOne(submissionId)
                .getSolutions().stream().map(Solution::getStudent)
                .map(stud -> getLastSolVersion(submissionId, stud.getId()))
                .map(s -> modelMapper.map(s, SolutionDTO.class))
                .filter(sol -> sol.getVersion() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolutionDTO> getAllSolutionsForStudentForSubmission(Long submissionId, String studentId) {
        // history of solutions for a student for a specific submission
        if (!submissionRepo.findById(submissionId).isPresent())
            throw new SubmissionNotFoundException("Submission not found!");

        if (studentId.length() == 0 || !studentRepo.findById(studentId).isPresent())
            throw new StudentNotFoundException("Student not found!");

        Student student = studentRepo.getOne(studentId);
        Submission submission = submissionRepo.getOne(submissionId);

        if (!student.getCourses().contains(submission.getCourse()))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        return student.getSolutions().stream()
                .filter(sol -> sol.getSubmission().equals(submission))
                .filter(sol -> sol.getVersion() > 0)
                .map(sol -> modelMapper.typeMap(Solution.class, SolutionDTO.class).addMappings(mapper -> {
                    mapper.skip(SolutionDTO::setImage);
                }).map(sol))
                .collect(Collectors.toList());
    }

    public List<SolutionDTO> getAllSolutionsForStudentForCourse(String courseName, String studentId) {
        // history of all solutions (only last version) for a student for ALL submissions of the course
        if (!studentRepo.findById(studentId).isPresent())
            throw new StudentNotFoundException("Student not found!");

        if (!courseRepo.findById(courseName).isPresent())
            throw new CourseNotFoundException("Course not found!");

        Course course = courseRepo.getOne(courseName);

        if (!studentRepo.getOne(studentId).getCourses().contains(course))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        if (!course.isEnabled())
            throw new CourseDisabledException("Course not enabled!");

        return course.getSubmissions().stream()
                .map(submission -> getLastSolVersion(submission.getId(), studentId))
                .map(sol -> modelMapper.map(sol, SolutionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean evaluateSolution(Long solutionId, Long evaluation, String profId) {
        if (!solutionRepo.findById(solutionId).isPresent())
            throw new SolutionNotFoundException("Solution not found!");

        if (!isProfessorCourseSolution(solutionId, profId))
            throw new TeamServiceException("You are not a professor of this course!");

        Solution sol = solutionRepo.getOne(solutionId);
        sol.setEvaluation(evaluation);
        sol.setStatus("EVALUATED");

        executor.execute(() -> notification.sendMessage(
                sol.getStudent().getEmail(),
                "Evaluation",
                "The professor has evaluated your solution.\nSolution_id = " + sol.getId() +
                        "\n Final score: " + evaluation
        ));

        return true;


    }

    @Override
    public void stopRevisions(Long solutionId, String profId) {
        if (!solutionRepo.findById(solutionId).isPresent())
            throw new SolutionNotFoundException("Solution not found!");

        if (!isProfessorCourseSolution(solutionId, profId))
            throw new TeamServiceException("You are not a professor of this course!");

        solutionRepo.getOne(solutionId).setRevisable(false);

    }

    @Override
    public byte[] getSubmissionImage( Long submissionId) {
        if (!submissionRepo.findById(submissionId).isPresent())
            throw new SubmissionNotFoundException("Submission not found!");

        Byte[] image = submissionRepo.getOne(submissionId).getImage();
        int j=0;
        byte[] bytes = new byte[image.length];
        for(Byte b: image)
            bytes[j++] = b;

        return bytes;
    }

    @Override
    public byte[] getSolutionImage(Long solutionId) {
        if (!solutionRepo.findById(solutionId).isPresent())
            throw new SolutionNotFoundException("Solution not found!");

        Byte[] image = solutionRepo.getOne(solutionId).getImage();
        int j=0;
        byte[] bytes = new byte[image.length];
        for(Byte b: image)
            bytes[j++] = b;

        return bytes;
    }

    @Override
    public SolutionDTO getSolution(Long solutionId, String username) {
        if (!solutionRepo.findById(solutionId).isPresent())
            throw new SolutionNotFoundException("Solution not found!");

        Solution sol = solutionRepo.getOne(solutionId);

        if (sol.getSubmission().getCourse().getProfessors().contains(profRepo.getOne(username)))
            sol.setStatus("REVISED");

        return modelMapper.map(sol, SolutionDTO.class);
    }

    @Override
    public SolutionDTO getLastSolution(String studentId, Long submissionId, String username) {

        if (!studentRepo.findById(studentId).isPresent())
            throw new StudentNotFoundException("Student not found!");

        if (!submissionRepo.findById(submissionId).isPresent())
            throw new SubmissionNotFoundException("Submission not found!");

        Course course = submissionRepo.getOne(submissionId).getCourse();

        if (!studentRepo.getOne(studentId).getCourses().contains(course))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        if (!course.isEnabled())
            throw new CourseDisabledException("Course not enabled!");

        return modelMapper.map(getLastSolVersion(submissionId, studentId), SolutionDTO.class);

    }

    /* ----------- PRIVATE METHODS ------------ */


    @Scheduled(fixedRate = 24*60*60*1000) // una volta al giorno + appena avvio il programma
    public void passiveSolutionAfterSubmissionExpiryDate() {
        AtomicBoolean found = new AtomicBoolean(false);

        submissionRepo.findAll().forEach( submission -> {
            // Per ogni submission
            if(submission.getExpiryDate().before(new Date())) // se è scaduta
                submission.getCourse().getStudents().forEach(student -> {
                    // per ogni studente del corso
                    found.set(false);
                    if (student.getSolutions().stream()
                            .filter(solution -> solution.getSubmission().equals(submission)) //submission corrente
                            .anyMatch(solution -> solution.getVersion() != -1 && solution.getVersion() != 0 )) // cioè ha sottomesso qualcosa
                        found.set(true);
                    if (!found.get()){
                        // student didn't send a solution
                        List<Solution> ReadSol = student.getSolutions().stream()
                                .filter(sol -> sol.getSubmission().equals(submission))
                                .filter(sol -> sol.getVersion() != -2) // se è stata già processata in passato
                                .filter(sol -> sol.getVersion() == 0 || sol.getVersion() == -1) // se è letta oppure non letta
                                .collect(Collectors.toList() );
                        if (ReadSol.size() > 0){
                            submission.getSolutions().remove(ReadSol.get(0));
                            solutionRepo.delete(ReadSol.get(0)); // elimino la solution fittizia
                        }
                        Solution blankSolution = new Solution();
                        blankSolution.setStatus("NOT_SUBMITTED");
                        blankSolution.setVersion(-2);
                        blankSolution.setRevisable(false);
                        blankSolution.setStudent(student);
                        blankSolution.setSubmission(submission);
                        solutionRepo.save(blankSolution);
                    }
                });
        });

    }

    private boolean isProfessorCourseSolution(Long solutionId, String profId) {
        return solutionRepo.getOne(solutionId).getSubmission().getCourse().getProfessors()
                .contains(profRepo.getOne(profId));
    }

    private boolean isProfessorCourseSubmission(Long submissionId, String profId) {
        return submissionRepo.getOne(submissionId).getCourse().getProfessors().contains(profRepo.getOne(profId));
    }

    private void createNewSol(String username, Long submissionId) {
        Solution sol = new Solution();
        sol.setStatus("READ");
        sol.setRevisable(true);
        sol.setVersion(0);
        sol.setStudent(studentRepo.getOne(username));
        sol.setSubmission(submissionRepo.getOne(submissionId));
        solutionRepo.save(sol);
        studentRepo.getOne(username).addSolution(sol);
    }

    private Solution getLastSolVersion(Long submissionId, String studentId) {
        Optional<Solution> solution = submissionRepo.getOne(submissionId).getSolutions()
                .stream()
                .filter(s -> s.getStudent().equals(studentRepo.getOne(studentId)))
                .max(Comparator.comparing(Solution::getVersion));
        if (solution.isPresent())
            return solution.get();
        Solution blank = new Solution();
        blank.setStatus("NOT_READ");
        blank.setStudent(studentRepo.getOne(studentId));
        blank.setSubmission(submissionRepo.getOne(submissionId));
        blank.setVersion(-1);
        blank.setRevisable(true);
        return solutionRepo.save(blank);
    }



    /* ----------- DEPRECATED METHODS ------------ */

    @Deprecated
    @Override
    public SubmissionDTO getLastSubmission(String courseName, String username) {

        if (!courseRepo.findById(courseName).isPresent()) throw new CourseNotFoundException(
                "Course not found!"
        );

        if (!courseRepo.getOne(courseName).isEnabled()) throw new CourseDisabledException(
                "Course not enabled!"
        );

        Optional<SubmissionDTO> optSubmission = courseRepo
                .getOne(courseName)
                .getSubmissions()
                .stream()
                .map(s -> modelMapper.map(s, SubmissionDTO.class))
                .max(Comparator.comparing(SubmissionDTO::getReleaseDate));

//
        if (optSubmission.isPresent()) {
            /*if (!profRepo.findById(username).isPresent()) { //a student is requiring submission
                if (studentRepo.getOne(username).getSolutions().stream().noneMatch(sol -> sol.getSubmission().getId()
                        .equals(optSubmission.get().getId()))) {
                    //no solution for this submission and this student-->create an empty solution with status "READ"

                }
            }*/
            return optSubmission.get();
        } else throw new SubmissionNotFoundException("There are no submissions for this course!");
    }

    @Deprecated
    @Override
    public boolean evaluateLastSolution(String studentId, Long submissionId, Long evaluation, String profId) {

        try {
            SolutionDTO sol = getLastSolution(studentId, submissionId, profId);
            if (!isProfessorCourseSubmission(submissionId, profId))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a professor of this course!");
            solutionRepo.getOne(sol.getId()).setEvaluation(evaluation);
            solutionRepo.getOne(sol.getId()).setStatus("EVALUATED");
            executor.execute(() -> notification.sendMessage(
                    studentRepo.getOne(studentId).getEmail(),
                    "Evaluation",
                    "The professor has evaluated your solution.\nSolution_id = " + sol.getId() +
                            "\n Final score: " + evaluation
            ));

            return true;
        } catch (TeamServiceException e) {
            throw e;
        }

    }

    @Deprecated
    @Override
    public String updateSolution(Long submissionId, SolutionDTO solutionDTO, String studentId) {
        return "DEPRECATED";
    }

}
