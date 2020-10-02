package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.*;
import it.polito.ai.project.exceptions.*;
import it.polito.ai.project.services.NotificationService;
import it.polito.ai.project.services.SubmissionService;
import it.polito.ai.project.services.TeamService;
import it.polito.ai.project.services.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    TeamService service;

    @Autowired
    VmService vmService;

    @Autowired
    NotificationService notifyService;

    @Autowired
    SubmissionService submissionService;

    @GetMapping({"", "/"})
    public List<CourseDTO> all() {
        List<CourseDTO> courses = service.getAllCourses();
        courses.forEach(ModelHelper::enrich);
        return courses;
    }

    @GetMapping({"/{professorId}/getCourses"})
    public List<CourseDTO> getProfessorCourses(@PathVariable String professorId) {
        List<CourseDTO> courses = service.getProfessorCourses(professorId);
        courses.forEach(ModelHelper::enrich);
        return courses;
    }

    @GetMapping("/{courseName}")
    public CourseDTO getOne(@PathVariable String courseName) {
        Optional<CourseDTO> course = service.getCourse(courseName);

        if (!course.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, courseName);

        else {
            CourseDTO courseDTO = course.get();
            return ModelHelper.enrich(courseDTO);
        }
    }

    @GetMapping("/{courseName}/getVMType")
    public VMTypeDTO getVMType(@PathVariable String courseName) {
        try {
            return vmService.getVMType(courseName);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{courseName}/deleteOne")
    public Boolean deleteOne(@PathVariable String courseName, @RequestParam String studentId) {
        try {
            service.deleteOne(studentId, courseName);
            return true;
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/enrolled")
    public List<StudentDTO> enrolledStudents(@PathVariable String courseName) {
        try {
            List<StudentDTO> students = service.getEnrolledStudents(courseName);
            students.forEach(ModelHelper::enrich);
            return students;
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course: " + courseName + " Error: " + e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    public CourseDTO addCourse(@RequestBody CourseDTO dto) {
        if (!service.addCourse(dto))
            throw new ResponseStatusException(HttpStatus.CONFLICT, dto.getName());
        else return ModelHelper.enrich(dto);
    }

    @PostMapping("/{courseName}/enable")
    public boolean enableCourse(@PathVariable String courseName) {
        try {
            service.enableCourse(courseName, getCurrentUsername());
            return true;
        } catch (CourseNotFoundException | ResponseStatusException e) {
            if (e instanceof ResponseStatusException)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Error: " + e.getMessage());

            else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: " + e.getMessage());
        }
    }

    @PostMapping("/{courseName}/update")
    public boolean updateCourse(@PathVariable String courseName, @RequestBody CourseDTO ModifiedCourse) {
        try {
            service.updateCourse(courseName, ModifiedCourse, getCurrentUsername());
            return true;
        } catch (CourseNotFoundException | ResponseStatusException e) {
            if (e instanceof ResponseStatusException)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Error: " + e.getMessage());

            else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: " + e.getMessage());
        }
    }

    @PostMapping("/{courseName}/delete")
    public boolean deleteCourse(@PathVariable String courseName) {
        try {
            service.deleteCourse(courseName, getCurrentUsername());
            return true;
        } catch (CourseNotFoundException | ResponseStatusException e) {
            if (e instanceof ResponseStatusException)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Error: " + e.getMessage());

            else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: " + e.getMessage());
        }
    }

    @PostMapping("/{courseName}/disable")
    public boolean disableCourse(@PathVariable String courseName) {
        try {
            service.disableCourse(courseName, getCurrentUsername());
            return true;
        } catch (CourseNotFoundException | ResponseStatusException e) {
            if (e instanceof ResponseStatusException)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Error: " + e.getMessage());

            else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: " + e.getMessage());
        }
    }

    @PostMapping("/{courseName}/enrollOne")
    public boolean enrollOne(
            @PathVariable String courseName,
            @RequestBody StudentDTO student
    ) {
        try {
            if (service.addStudentToCourse(student.getId(), courseName))
                return true;
            else throw new ResponseStatusException(HttpStatus.CONFLICT, courseName + " " + student.getId());

        } catch (CourseNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course: " + courseName + " StudentID: " + student.getId() + " Error: " + e.getMessage());
        }
    }

    @PostMapping("/{courseName}/enrollMany")
    public List<Boolean> enrollStudents(
            @PathVariable String courseName,
            @RequestParam("file") MultipartFile file
    ) {
        if (!Objects.equals(file.getContentType(), "text/csv"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File content type: " + file.getContentType() + " Error: CSV file required!");

        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            return service.addAndEnroll(reader, courseName);
        } catch (
                CourseNotFoundException | StudentNotFoundException | IOException e
        ) {
            if (e instanceof TeamServiceException)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course: " + courseName + " Error: " + e.getMessage());
            else
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Course: " + courseName + " Error: " + e.getMessage());
        }
    }

    @PostMapping("/{courseName}/proposeTeam")
    public boolean proposeTeam(
            @PathVariable String courseName,
            @RequestParam String name,
            @RequestParam Timestamp timestamp,
            @RequestBody List<String> membersIds
    ) {
        TeamDTO team;
        try {

            team = service.proposeTeam(courseName, name, membersIds, getCurrentUsername());
            notifyService.notifyTeam(team, membersIds, timestamp);
            return true;
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/teams")
    public List<TeamDTO> getTeamsForCourse(@PathVariable String courseName) {
        try {
            return service.getTeamForCourse(courseName);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/availableStudents")
    public List<StudentDTO> getAvailableStudents(
            @PathVariable String courseName
    ) {
        try {
            return service.getAvailableStudents(courseName);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/studentsInTeams")
    public List<StudentDTO> getStudentsInTeams(@PathVariable String courseName) {
        try {
            return service.getStudentsInTeams(courseName);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/professors")
    public List<ProfessorDTO> getProfessors(@PathVariable String courseName) {
        try {
            return service.getProfessors(courseName);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{courseName}/addProfessor")
    public boolean addProfessorToCourse(@PathVariable String courseName, @RequestParam String id) {
        try {
            return service.addProfessorToCourse(id, courseName);
        } catch (TeamServiceException e) {
            if (e instanceof StudentNotFoundException || e instanceof CourseNotFoundException)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: " + e.getMessage());
            else throw new ResponseStatusException(HttpStatus.CONFLICT, "Error: " + e.getMessage());
        }
    }

    @PostMapping("/{courseName}/deleteProfessor")
    public boolean deleteProfessorFromCourse(@PathVariable String courseName, @RequestParam String id) {
        try {
            return service.deleteProfessor(id, courseName);
        } catch (TeamServiceException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: " + e.getMessage());
        }
    }

    @PostMapping("/{courseName}/createVMType")
    public Long createVMType(@RequestBody VMTypeDTO vmt, @PathVariable String courseName) {
        try {
            return vmService.createVMType(vmt, courseName);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{courseName}/setVMType")
    public Boolean setVMType(@RequestParam Long vmtId, @PathVariable String courseName) {
        try {
            vmService.setVMType(courseName, vmtId);
            return true;
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //  SUBMISSION START
    @PostMapping("/{courseName}/addSubmission")
    public SubmissionDTO addSubmission(@PathVariable String courseName,
                                       @RequestPart("submission") SubmissionDTO submission,
                                       @RequestPart("file") MultipartFile file) {
        try {
            String profId = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName()
                    .split("@")[0];
            return submissionService.addSubmission(submission, courseName, profId, file);
        } catch (TeamServiceException | ResponseStatusException e) {
            if (e instanceof ResponseStatusException)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Error: " + e.getMessage());

            else if (e instanceof CourseNotFoundException)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: " + e.getMessage());
            else throw new ResponseStatusException(HttpStatus.CONFLICT, "Error: " + e.getMessage());
        }
    }

    @GetMapping("/{courseName}/getAllSubmissions")
    public List<SubmissionDTO> getAllSubmissions(@PathVariable String courseName) {
        try {
            return submissionService.getAllSubmissions(courseName, getCurrentUsername());
        } catch (TeamServiceException e) {
            if (e instanceof CourseNotFoundException)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/submissions/{id}")
    public SubmissionDTO getSubmissionById(@PathVariable String courseName, @PathVariable Long id) {
        try {
            return submissionService.getSubmission(courseName, id, getCurrentUsername());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/submissions/getImage/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void showImage(HttpServletResponse response, @PathVariable Long id) {
        try {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("application/pdf");
            response.getOutputStream().write(submissionService.getSubmissionImage(id));
            response.getOutputStream().close();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error retrieving image!");
        }
    }

    @PostMapping("/{solutionId}/stopRevisions")
    public void stopRevisions(@PathVariable Long solutionId) {
        try {
            submissionService.stopRevisions(solutionId, getCurrentUsername());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

//  SUBMISSIONS END

    /* PRIVATE METHODS */
    private String getCurrentUsername() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName()
                .split("@")[0];
    }


    /* DEPRECATED END-POINTS */
    @Deprecated
    @GetMapping("/{courseName}/getLastSubmission")
    public SubmissionDTO getLastSubmission(@PathVariable String courseName) {
        try {
            return submissionService.getLastSubmission(courseName, getCurrentUsername());
        } catch (TeamServiceException e) {
            if (e instanceof CourseNotFoundException || e instanceof SubmissionNotFoundException)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


}
