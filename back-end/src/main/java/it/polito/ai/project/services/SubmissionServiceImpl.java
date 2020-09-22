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

    @Override
    public SubmissionDTO addSubmission(SubmissionDTO submissionDTO, String courseName, String profId, MultipartFile submissionFile) {

        if (submissionDTO == null)
            throw new SubmissionNotFoundException("Bad request!");
        if (courseName.length() == 0 || !courseRepo.existsById(courseName))

            throw new CourseNotFoundException("Course not found!");

        Submission submissionEntity = modelMapper.map(submissionDTO, Submission.class);
        Course course = courseRepo.getOne(courseName);

        if (!course.isEnabled())
            throw new CourseDisabledException("Course not enabled!");

        // l'altro non andava bene va fatto prima di fare le modifiche sul repo, e non avendo l'id quello non andava bene
        if (!courseRepo.getOne(courseName).getProfessors().contains(profRepo.getOne(profId))) {
            submissionRepo.delete(submissionEntity);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a professor of this course!");
        }

        try{
            Byte[] byteObjects = new Byte[submissionFile.getBytes().length];

            int i = 0;

            for (byte b : submissionFile.getBytes())
                byteObjects[i++] = b;

            submissionEntity.setImage(byteObjects);
        } catch (IOException e) {
            throw new TeamServiceException("Error saving image: " + e.getMessage());
        }


        submissionEntity.setCourse(course);
        submissionEntity = submissionRepo.save(submissionEntity);
        course.addSubmission(submissionEntity);


        for (Student s : course.getStudents())
            notification.sendMessage(
                    s.getEmail(),
                    "Submission",
                    "The professor created a new submission for you.\nSubmission id:" + submissionEntity.getId() + "\nExpiry date: " + submissionEntity.getExpiryDate()
            );

        return modelMapper.map(submissionEntity, SubmissionDTO.class);
    }

    @Override
    public List<SubmissionDTO> getAllSubmissions(String courseName, String username) {

        if (!courseRepo.existsById(courseName))
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
    public SubmissionDTO getSubmission(String courseName, Long id, String username) {

        if (!courseRepo.existsById(courseName))
            throw new CourseNotFoundException("Course not found!");
        if (!submissionRepo.existsById(id))
            throw new SubmissionNotFoundException("Submission not found!");

        if (!profRepo.existsById(username)) { //a student is requiring submission
            if (studentRepo.getOne(username).getSolutions().stream().noneMatch(sol -> sol.getSubmission().getId().equals(id))) {
                //no solution for this submission and this student-->create an empty solution with status "READ"
                createNewSol(username, id);
            }
        }

        return modelMapper.map(submissionRepo.getOne(id), SubmissionDTO.class);
    }


    // @Todo ->
    @Override
    public SolutionDTO addSolution(Long submissionId, SolutionDTO solutionDTO, String studentId, MultipartFile solutionFile) {

        if (solutionDTO == null)
            throw new SolutionNotFoundException("Bad request!");

        if (!submissionRepo.existsById(submissionId))
            throw new SubmissionNotFoundException("Submission not found!");

        Submission submission = submissionRepo.getOne(submissionId);
        if (submission.getExpiryDate().before(new Date()))
            throw new SubmissionExpiredException("This submission expired! You cannot submit solutions now.");

        Solution solution = modelMapper.map(solutionDTO, Solution.class);

        if (studentId.length() == 0 || !studentRepo.existsById(studentId))
            throw new StudentNotFoundException("Student not found!");

        Student s = studentRepo.getOne(studentId);

        if (!s.getCourses().contains(submission.getCourse()))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        long version = solutionRepo.findAll()
                .stream()
                .filter(sol -> sol.getStudent().getId().equals(studentId))
                .filter(sol -> sol.getSubmission().getId().equals(submissionId))
                .count();

        solution.setSubmission(submissionRepo.getOne(submissionId));
        solution.setStatus("SUBMITTED");
        solution.setStudent(studentRepo.getOne(studentId));
        solution.setVersion((int) (version + 1L));

        try{
            Byte[] byteObjects = new Byte[solutionFile.getBytes().length];

            int i = 0;

            for (byte b : solutionFile.getBytes())
                byteObjects[i++] = b;

            solution.setImage(byteObjects);
        } catch (IOException e) {
            throw new TeamServiceException("Error saving image: " + e.getMessage());
        }

        solutionRepo.save(solution);
        studentRepo.getOne(studentId).addSolution(solution);

        return modelMapper.map(solutionRepo.getOne(solution.getId()),SolutionDTO.class);
    }


    @Deprecated
    @Override
    public String updateSolution(Long submissionId, SolutionDTO solutionDTO, String studentId) {
        try {
            return "DEPRECATED";
        } catch (TeamServiceException e) {
            throw e;
        }

    }

    @Override
    public List<SolutionDTO> getAllSolutions(Long submissionId) { //all students for that submission (only last version)
        if (!submissionRepo.existsById(submissionId))
            throw new SubmissionNotFoundException("Submission not found!");

        Submission subm = submissionRepo.getOne(submissionId);

        return subm.getSolutions().stream().map(Solution::getStudent)
                .map(stud -> getLastSolVersion(subm.getId(), stud.getId()))
                .map(s -> modelMapper.map(s, SolutionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SolutionDTO> getAllSolutionsForStudentForSubmission(Long submissionId, String studentId) {
        // history of solutions for a student for a specific submission
        if (!submissionRepo.existsById(submissionId))
            throw new SubmissionNotFoundException("Submission not found!");

        if (studentId.length() == 0 || !studentRepo.existsById(studentId))
            throw new StudentNotFoundException("Student not found!");

        Student s = studentRepo.getOne(studentId);
        Submission submission = submissionRepo.getOne(submissionId);

        if (!s.getCourses().contains(submission.getCourse()))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        return s.getSolutions().stream().filter(sol -> sol.getSubmission().equals(submission))
                .map(sol -> modelMapper.typeMap(Solution.class, SolutionDTO.class).addMappings(mapper -> {
                    mapper.skip(SolutionDTO::setImage);
                }).map(sol))
                .collect(Collectors.toList());
    }

    public List<SolutionDTO> getAllSolutionsForStudentForCourse(String courseName, String studentId) {
        // history of all solutions (only last version) for a student for ALL submissions of the course
        if (!studentRepo.existsById(studentId))
            throw new StudentNotFoundException("Student not found!");

        if (!courseRepo.existsById(courseName))
            throw new CourseNotFoundException("Course not found!");

        Course course = courseRepo.getOne(courseName);

        if (!studentRepo.getOne(studentId).getCourses().contains(course))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        if (!course.isEnabled())
            throw new CourseDisabledException("Course not enabled!");

        Student s = studentRepo.getOne(studentId);

        return course.getSubmissions().stream()
                .map(subm -> getLastSolVersion(subm.getId(), studentId))
                .map(sol -> modelMapper.map(sol, SolutionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean evaluateSolution(Long solutionId, Long evaluation, String profId) {
        if (!solutionRepo.existsById(solutionId))
            throw new SubmissionNotFoundException("Solution not found!");

        if (!isProfessorCourseSolution(solutionId, profId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a professor of this course!");

        Solution sol = solutionRepo.getOne(solutionId);
        sol.setEvaluation(evaluation);
        sol.setStatus("EVALUATED");
        notification.sendMessage(
                sol.getStudent().getEmail(),
                "Evaluation",
                "The professor has evaluated your solution.\nSolution_id = " + sol.getId() +
                        "\n Final score: " + evaluation
        );
        return true;


    }

    @Override
    public void stopRevisions(Long solutionId, String profId) {
        if (!solutionRepo.existsById(solutionId))
            throw new SolutionNotFoundException("Solution not found!");

        if (!isProfessorCourseSolution(solutionId, profId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a professor of this course!");

        solutionRepo.getOne(solutionId).setRevisable(false);

    }

    @Override
    public byte[] getSubmissionImage( Long submissionId) {
        if (!submissionRepo.existsById(submissionId))
            throw new SolutionNotFoundException("Submission not found!");

        Byte[] image = submissionRepo.getOne(submissionId).getImage();
        int j=0;
        byte[] bytes = new byte[image.length];
        for(Byte b: image)
            bytes[j++] = b;

        return bytes;
    }

    @Override
    public byte[] getSolutionImage(String studentId, Long solutionId) {
        if (!solutionRepo.existsById(solutionId))
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
        if (!solutionRepo.existsById(solutionId))
            throw new SolutionNotFoundException("Solution not found!");

        Solution sol = solutionRepo.getOne(solutionId);

        if (sol.getSubmission().getCourse().getProfessors().contains(profRepo.getOne(username))) {
            sol.setStatus("REVISED");
            return modelMapper.map(sol, SolutionDTO.class);
        } else { //is a student
            if (sol.isRevisable())
                return modelMapper.map(sol, SolutionDTO.class);
            else
                throw new NotRevisableException("You cannot modify/read this solution (id = " + sol.getId() + ") anymore!");
        }
    }

    @Override
    public SolutionDTO getLastSolution(String studentId, Long submissionId, String username) {

        if (!studentRepo.existsById(studentId))
            throw new StudentNotFoundException("Student not found!");

        if (!submissionRepo.existsById(submissionId))
            throw new SubmissionNotFoundException("Submission not found!");

        Course course = submissionRepo.getOne(submissionId).getCourse();

        if (!studentRepo.getOne(studentId).getCourses().contains(course))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        if (!course.isEnabled())
            throw new CourseDisabledException("Course not enabled!");

        Solution sol = getLastSolVersion(submissionId, studentId);

        if (submissionRepo.getOne(submissionId).getCourse().getProfessors().contains(profRepo.getOne(username)))
            sol.setStatus("REVISED");
        else if (!sol.isRevisable())
            throw new NotRevisableException("You cannot modify/read this solution (id = " + sol.getId() + ") anymore!");
        solutionRepo.save(sol);
        return modelMapper.map(sol, SolutionDTO.class);

    }

    /* ----------- PRIVATE METHODS ------------ */


    @Scheduled(fixedRate = 600000)
    private void passiveSolutionAfterSubmissionExpiryDate() {
        Solution sol = new Solution();
        sol.setStatus("SUBMITTED");
        sol.setVersion(0);
        sol.setRevisable(false);
        AtomicBoolean found = new AtomicBoolean(false);
        submissionRepo.findAll()
                .forEach(
                        s -> {
                            if (s.getExpiryDate().before(new Date())) //expirated submission
                                s.getCourse().getStudents().forEach(stud -> {
                                    found.set(false);
                                    stud.getSolutions().forEach(solution -> {
                                        if (solution.getSubmission().equals(s))
                                            found.set(true);
                                    });
                                    if (!found.get())
                                        // student didn't send a solution
                                        stud.getSolutions().add(sol);
                                });
                        }
                );

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
        return submissionRepo.getOne(submissionId).getSolutions()
                .stream()
                .filter(s -> s.getStudent().equals(studentRepo.getOne(studentId)))
                .max(Comparator.comparing(Solution::getVersion))
                .get();
    }



    /* ----------- DEPRECATED METHODS ------------ */

    @Deprecated
    @Override
    public SubmissionDTO getLastSubmission(String courseName, String username) {

        if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
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
            /*if (!profRepo.existsById(username)) { //a student is requiring submission
                if (studentRepo.getOne(username).getSolutions().stream().noneMatch(sol -> sol.getSubmission().getId()
                        .equals(optSubmission.get().getId()))) {
                    //no solution for this submission and this student-->create an empty solution with status "READ"
                    createNewSol(username);
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
            notification.sendMessage(
                    studentRepo.getOne(studentId).getEmail(),
                    "Evaluation",
                    "The professor has evaluated your solution.\nSolution_id = " + sol.getId() +
                            "\n Final score: " + evaluation
            );

            return true;
        } catch (TeamServiceException e) {
            throw e;
        }

    }

}
