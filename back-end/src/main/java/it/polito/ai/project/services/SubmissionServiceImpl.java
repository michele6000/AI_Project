package it.polito.ai.project.services;

import it.polito.ai.project.dtos.CourseDTO;
import it.polito.ai.project.dtos.SubmissionDTO;
import it.polito.ai.project.entities.Course;
import it.polito.ai.project.entities.Student;
import it.polito.ai.project.entities.Submission;
import it.polito.ai.project.exceptions.CourseNotFoundException;
import it.polito.ai.project.exceptions.StudentNotFoundException;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.repositories.CourseRepository;
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

    @Override
    public boolean addSubmission(SubmissionDTO submissionDTO) {

        if (submissionDTO == null) return false;

        Submission submissionEntity = modelMapper.map(submissionDTO, Submission.class);

        Course course=submissionEntity.getCourse();

        if (course==null) return false;

        if (!courseRepo.existsById(course.getName())) throw new CourseNotFoundException(
                "Course not found!"
        );

        if (!course.isEnabled()) throw new TeamServiceException(
                "Course not enabled!"
        );

        course.addSubmission(submissionEntity);

        for(Student s: course.getStudents())
        notification.sendMessage(
                s.getEmail(),
                "Submission",
                "The professor created a new submission for you. Expiry date: "+submissionEntity.getExpiryDate()
        );

        return true;
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

        return optSubmission.orElse(null);
    }

    

}
