package it.polito.ai.esercitazione3.controllers;

import it.polito.ai.esercitazione3.dtos.CourseDTO;
import it.polito.ai.esercitazione3.dtos.ProfessorDTO;
import it.polito.ai.esercitazione3.dtos.StudentDTO;
import it.polito.ai.esercitazione3.dtos.TeamDTO;
import it.polito.ai.esercitazione3.exceptions.CourseNotFoundException;
import it.polito.ai.esercitazione3.exceptions.StudentNotFoundException;
import it.polito.ai.esercitazione3.services.NotificationService;
import it.polito.ai.esercitazione3.services.TeamService;
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
  NotificationService nService;

  @GetMapping({ "", "/" })
  public List<CourseDTO> all() {
    List<CourseDTO> courses = service.getAllCourses();
    courses.forEach(ModelHelper::enrich);
    return courses;
  }

  @PostMapping({ "", "/" })
  public CourseDTO addCourse(@RequestBody CourseDTO course) {
    if (service.addCourse(course)) return ModelHelper.enrich(course);
    throw new ResponseStatusException(HttpStatus.CONFLICT, course.getName());
  }

  @PostMapping("/{name}/enable")
  public boolean enableCourse(@PathVariable String name) {
    try {
      service.enableCourse(name);
      return true;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @PostMapping("/{name}/disable")
  public boolean disableCourse(@PathVariable String name) {
    try {
      service.disableCourse(name);
      return true;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/{name}")
  public CourseDTO getOne(@PathVariable String name) {
    Optional<CourseDTO> result = service.getCourse(name);
    if (result.isPresent()) return ModelHelper.enrich(result.get());
    throw new ResponseStatusException(HttpStatus.CONFLICT, name);
  }

  @GetMapping("/{name}/enrolled")
  public List<StudentDTO> enrolledStudents(@PathVariable String name) {
    if (!service.getCourse(name).isPresent()) throw new ResponseStatusException(
      HttpStatus.NOT_FOUND,
      name
    );
    List<StudentDTO> students = service.getEnrolledStudents(name);
    students.forEach(ModelHelper::enrich);
    return students;
  }

  @PostMapping("/{name}/enrollOne")
  public boolean enrollOne(
    @PathVariable String name,
    @RequestBody StudentDTO stud
  ) {
    String id = stud.getId();
    try {
      if (service.addStudentToCourse(id, name)) return true;
      throw new ResponseStatusException(HttpStatus.CONFLICT, id);
    } catch (CourseNotFoundException | StudentNotFoundException e) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Enrolling: " + id + " - " + name + ". Error : " + e.getMessage()
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
      "Filename: " + file.getName() + " Type: " + file.getContentType()
    );
    try {
      Reader reader = new InputStreamReader(file.getInputStream());
      return service.addAndEnroll(reader, name);
    } catch (Exception e) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Enrolling: " +
        file.getName() +
        ".csv - " +
        name +
        ". Error : " +
        e.getMessage()
      );
    }
  }

  @PostMapping("/{courseName}/proposeTeam")
  public String proposeTeam(
    @PathVariable String courseName,
    @RequestParam String name,
    @RequestBody List<String> membersIds
  ) {
    TeamDTO tmp;
    try {
      tmp = service.proposeTeam(courseName, name, membersIds);
      nService.notifyTeam(tmp, membersIds);
      return "Team: " + tmp.getName() + " - Proposta effettuata correttamente!";
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/{courseName}/getTeams")
  public List<TeamDTO> getTeams(@PathVariable String courseName) {
    try {
      return service.getTeamForCourse(courseName);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/{courseName}/getProfessors")
  public List<ProfessorDTO> getProfessors(@PathVariable String courseName) {
    try {
      return service.getProfessors(courseName);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/{courseName}/getFreeStudent")
  public List<StudentDTO> getFreeStudents(@PathVariable String courseName) {
    try {
      List<StudentDTO> students = service.getAvailableStudents(courseName);
      students.forEach(ModelHelper::enrich);
      return students;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/{courseName}/getBusyStudent")
  public List<StudentDTO> getBusyStudents(@PathVariable String courseName) {
    try {
      List<StudentDTO> students = service.getStudentsInTeams(courseName);
      students.forEach(ModelHelper::enrich);
      return students;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping("/{courseName}/{teamId}/getMembers")
  public List<StudentDTO> getTeamMembers(
    @PathVariable String courseName,
    @PathVariable Long teamId
  ) {
    try {
      List<StudentDTO> students = service.getMembers(teamId);
      students.forEach(ModelHelper::enrich);
      return students;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
