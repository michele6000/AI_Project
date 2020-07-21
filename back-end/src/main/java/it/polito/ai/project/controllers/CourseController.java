package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.CourseDTO;
import it.polito.ai.project.dtos.ProfessorDTO;
import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.TeamDTO;
import it.polito.ai.project.exceptions.CourseNotFoundException;
import it.polito.ai.project.exceptions.StudentNotFoundException;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.NotificationService;
import it.polito.ai.project.services.TeamService;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/API/courses")
public class CourseController {
  @Autowired
  TeamService service;

  @Autowired
  NotificationService notifService;

  @GetMapping({ "", "/" })
  public List<CourseDTO> all() {
    List<CourseDTO> courses = service.getAllCourses();
    courses.forEach(ModelHelper::enrich);
    return courses;
  }

  @GetMapping({ "{/professorId}" })
  public List<CourseDTO> getProfessorCourses(@PathVariable String professorId) {
    List<CourseDTO> courses = service.getProfessorCourses(professorId);
    courses.forEach(ModelHelper::enrich);
    return courses;
  }

  @GetMapping("/{name}")
  public CourseDTO getOne(@PathVariable String name) {
    Optional<CourseDTO> course = service.getCourse(name);

    if (!course.isPresent()) throw new ResponseStatusException(
      HttpStatus.CONFLICT,
      name
    ); else {
      CourseDTO courseDTO = course.get();
      return ModelHelper.enrich(courseDTO);
    }
  }

  @GetMapping("/{name}/deleteOne")
  public Boolean deleteOne(@PathVariable String name, @RequestParam String studentId) {
    try{
      service.deleteOne(studentId,name);
      return true;
    }
    catch(TeamServiceException e){
      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              e.getMessage()
      );
    }
  }

  @GetMapping("/{name}/enrolled")
  public List<StudentDTO> enrolledStudents(@PathVariable String name) {
    try {
      List<StudentDTO> students = service.getEnrolledStudents(name);
      students.forEach(ModelHelper::enrich);
      return students;
    } catch (CourseNotFoundException e) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Course: " + name + " Error: " + e.getMessage()
      );
    }
  }

  @PostMapping({ "", "/" })
  public CourseDTO addCourse(@RequestBody CourseDTO dto) {
    if (!service.addCourse(dto)) throw new ResponseStatusException(
      HttpStatus.CONFLICT,
      dto.getName()
    ); else return ModelHelper.enrich(dto);
  }

  @PostMapping("/{name}/enable")
  public boolean enableCourse(@PathVariable String name) {
    try {
      service.enableCourse(name);
      return true;
    } catch (CourseNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/{name}/disable")
  public boolean disableCourse(@PathVariable String name) {
    try {
      service.disableCourse(name);
      return true;
    } catch (CourseNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/{name}/enrollOne")
  public boolean enrollOne(
    @PathVariable String name,
    @RequestBody StudentDTO student
  ) {
    try {
      if (
        service.addStudentToCourse(student.getId(), name)
      ) return true; else throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        name + " " + student.getId()
      );
    } catch (CourseNotFoundException | StudentNotFoundException e) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Course: " +
        name +
        " StudentID: " +
        student.getId() +
        " Error: " +
        e.getMessage()
      );
    }
  }

  @PostMapping("/{name}/enrollMany")
  public List<Boolean> enrollStudents(
    @PathVariable String name,
    @RequestParam("file") MultipartFile file
  ) {
    if (
      !Objects.equals(file.getContentType(), "text/csv")
    ) throw new ResponseStatusException(
      HttpStatus.UNSUPPORTED_MEDIA_TYPE,
      "File content type: " +
      file.getContentType() +
      " Error: CSV file required!"
    );

    try {
      Reader reader = new InputStreamReader(file.getInputStream());
      return service.addAndEnroll(reader, name);
    } catch (
      CourseNotFoundException | StudentNotFoundException | IOException e
    ) {
      if (
        e instanceof StudentNotFoundException ||
        e instanceof CourseNotFoundException
      ) throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Course: " + name + " Error: " + e.getMessage()
      ); else throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Course: " + name + " Error: " + e.getMessage()
      );
    }
  }

  @PostMapping("/{courseName}/proposeTeam")
  public boolean proposeTeam(
    @PathVariable String courseName,
    @RequestParam String name,
    @RequestBody List<String> membersIds
  ) {
    TeamDTO team;
    try {
      team = service.proposeTeam(courseName, name, membersIds);
      notifService.notifyTeam(team, membersIds);
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
  public boolean addProfessorToCourse(
          @PathVariable String courseName,
          @RequestParam String id
  ) {
    try {
      return service.addProfessorToCourse(id, courseName);
    } catch (TeamServiceException e) {
      if (
              e instanceof StudentNotFoundException ||
                      e instanceof CourseNotFoundException
      ) throw new ResponseStatusException(
              HttpStatus.NOT_FOUND,
              "Error: " + e.getMessage()
      ); else throw new ResponseStatusException(
              HttpStatus.CONFLICT,
              "Error: " + e.getMessage()
      );
    }
  }

  @GetMapping("/{courseName}/setVMlimits")
  public boolean setCourseVMlimits(@PathVariable String courseName, @RequestParam CourseDTO course) {
    try {
      //nel form solo i limiti
      course.setName(courseName);
      return service.setCourseVMlimits(course);
    } catch (CourseNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
