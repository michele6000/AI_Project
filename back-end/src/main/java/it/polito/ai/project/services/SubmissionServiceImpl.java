package it.polito.ai.project.services;

import it.polito.ai.project.dtos.CourseDTO;
import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.SubmissionDTO;
import it.polito.ai.project.entities.Course;
import it.polito.ai.project.entities.Student;
import it.polito.ai.project.entities.Submission;
import it.polito.ai.project.exceptions.CourseNotFoundException;
import it.polito.ai.project.exceptions.StudentNotFoundException;
import it.polito.ai.project.exceptions.SubmissionNotFoundException;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.repositories.CourseRepository;
import it.polito.ai.project.repositories.SubmissionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    

}
