package it.polito.ai.project.services;

import it.polito.ai.project.dtos.SolutionDTO;
import it.polito.ai.project.dtos.SubmissionDTO;
import it.polito.ai.project.entities.Course;
import it.polito.ai.project.entities.Solution;
import it.polito.ai.project.entities.Student;
import it.polito.ai.project.entities.Submission;
import it.polito.ai.project.exceptions.*;
import it.polito.ai.project.repositories.CourseRepository;
import it.polito.ai.project.repositories.SolutionRepository;
import it.polito.ai.project.repositories.StudentRepository;
import it.polito.ai.project.repositories.SubmissionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Override
    public String addSubmission(SubmissionDTO submissionDTO, String courseName) {

        if (submissionDTO == null) throw new SubmissionNotFoundException("Bad request!");

        Submission submissionEntity = modelMapper.map(submissionDTO, Submission.class);


        if (courseName.length()==0 || !courseRepo.existsById(courseName)) throw new CourseNotFoundException(
                "Course not found!"
        );

        Course course =courseRepo.getOne(courseName);

        if (!course.isEnabled()) throw new TeamServiceException(
                "Course not enabled!"
        );

        course.addSubmission(submissionEntity);
        submissionRepo.save(submissionEntity);

        for(Student s: course.getStudents())
        notification.sendMessage(
                s.getEmail(),
                "Submission",
                "The professor created a new submission for you.\nSubmission id:"+submissionEntity.getId()+"\nExpiry date: "+submissionEntity.getExpiryDate()
        );

        return "Submission successfully created, id = "+submissionEntity.getId();
    }

    @Override
    public List<SubmissionDTO> getAllSubmissions(String courseName) {

        if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
                "Course not found!"
        );

        if(!courseRepo.getOne(courseName).isEnabled()) throw new TeamServiceException(
                "Course not enabled!"
        );

        return courseRepo
                .getOne(courseName)
                .getSubmissions()
                .stream()
                .map(s -> modelMapper.map(s, SubmissionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public SubmissionDTO getLastSubmission(String courseName) {

        if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException(
                "Course not found!"
        );

        if(!courseRepo.getOne(courseName).isEnabled()) throw new TeamServiceException(
                "Course not enabled!"
        );

        Optional<SubmissionDTO> optSubmission= courseRepo
                .getOne(courseName)
                .getSubmissions()
                .stream()
                .map(s -> modelMapper.map(s, SubmissionDTO.class))
                .max(Comparator.comparing(SubmissionDTO::getReleaseDate));

        if(optSubmission.isPresent()) return optSubmission.get();
        else throw new SubmissionNotFoundException("There are no submissions for this course!");
    }

    @Override
    public SubmissionDTO getSubmission(String courseName, Long id) {

        if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException("Course not found!");
        if (!submissionRepo.existsById(id)) throw new SubmissionNotFoundException("Submission not found!");

        return modelMapper.map(submissionRepo.getOne(id), SubmissionDTO.class);

    }

    @Override
    public SolutionDTO getSolution(String studentId, Long submissionId) {

        if (!studentRepo.existsById(studentId)) throw new StudentNotFoundException("Student not found!");
        if (!submissionRepo.existsById(submissionId)) throw new SubmissionNotFoundException("Submission not found!");

        Course course=submissionRepo.getOne(submissionId).getCourse();
        if(!studentRepo.getOne(studentId).getCourses().contains(course))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        if(!course.isEnabled())
            throw new TeamServiceException("Course not enabled!");

        Optional<Solution> sol=submissionRepo.getOne(submissionId).getSolutions()
                .stream()
                .filter(s->s.getStudent().equals(studentRepo.getOne(studentId))).findFirst();

        if(sol.isPresent()){
            sol.get().setStatus("READ");
            solutionRepo.save(sol.get());
            return modelMapper.map(sol, SolutionDTO.class);
        }

        else throw new SolutionNotFoundException("Solution not found!");


    }

    @Override
    public boolean evaluateSolution(String studentId, Long submissionId, Long evaluation) {

        try{
            SolutionDTO sol=getSolution(studentId,submissionId);
            modelMapper.map(sol, Solution.class);
            sol.setEvaluation(evaluation);
            sol.setVersion("EVALUATED");
            notification.sendMessage(
                    studentRepo.getOne(studentId).getEmail(),
                    "Evaluation",
                    "The professor has evaluated your solution.\n Final score: "+evaluation
            );
            return true;
        }
        catch(Exception e){
            throw e;
        }

    }

    @Override
    public String addSolution(Long submissionId, SolutionDTO solutionDTO, String studentId) {

        if (solutionDTO == null) throw new SolutionNotFoundException("Bad request!");

        if(!submissionRepo.existsById(submissionId)) throw new SubmissionNotFoundException("Submission not found!");

        Submission submission=submissionRepo.getOne(submissionId);
        if(submission.getExpiryDate().before(new Date()))
            throw new SubmissionExpiredException("This submission expired! You cannot submit solutions now.");

        Solution solution = modelMapper.map(solutionDTO, Solution.class);

        if (studentId.length()==0 || !studentRepo.existsById(studentId)) throw new StudentNotFoundException(
                "Student not found!"
        );

        Student s =studentRepo.getOne(studentId);

        if(!studentRepo.getOne(studentId).getCourses().contains(submission.getCourse()))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        solution.setSubmission(submissionRepo.getOne(submissionId));
        solution.setStatus("SUBMITTED");

        solutionRepo.save(solution);

        return "Solution successfully created, id = "+solution.getId();
    }

    @Override
    public String updateSolution(Long submissionId, SolutionDTO solutionDTO, String studentId) {
    return null;

    }

    @Override
    @Scheduled(fixedRate = 600000)
    public void passiveSolutionAfterSubmissionExpiryDate() {
        Solution sol=new Solution();
        sol.setStatus("SUBMITTED");
        AtomicBoolean found= new AtomicBoolean(false);
        submissionRepo.findAll()
                .forEach(
                        s -> {
                            if(s.getExpiryDate().before(new Date())) //expirated submission
                                s.getCourse().getStudents().forEach(stud->{
                                    found.set(false);
                                    stud.getSolutions().forEach(solution->{
                                        if(solution.getSubmission().equals(s))
                                            found.set(true);
                                    });
                                    if(!found.get())
                                        // student didn't send a solution
                                        stud.getSolutions().add(sol);
                                });
                        }
                );

    }
    

}
