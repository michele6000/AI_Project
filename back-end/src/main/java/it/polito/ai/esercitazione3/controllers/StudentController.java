package it.polito.ai.esercitazione3.controllers;

import it.polito.ai.esercitazione3.dtos.CourseDTO;
import it.polito.ai.esercitazione3.dtos.StudentDTO;
import it.polito.ai.esercitazione3.dtos.TeamDTO;
import it.polito.ai.esercitazione3.services.TeamService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/API/students")
public class StudentController {
  @Autowired
  TeamService service;

  @GetMapping({ "", "/" })
  public List<StudentDTO> all() {
    List<StudentDTO> students = service.getAllStudents();
    students.forEach(ModelHelper::enrich);
    return students;
  }

  @PostMapping({ "", "/" })
  public StudentDTO addStudent(@RequestBody StudentDTO student) {
    if (service.addStudent(student)) return ModelHelper.enrich(student);
    throw new ResponseStatusException(HttpStatus.CONFLICT, student.getId());
  }

  @GetMapping("/{id}")
  public StudentDTO getOne(@PathVariable String id) {
    new UserController().itsMe(id);
    Optional<StudentDTO> result = service.getStudent(id);
    if (result.isPresent()) return ModelHelper.enrich(result.get());
    throw new ResponseStatusException(HttpStatus.CONFLICT, id);
  }

  @GetMapping("/{id}/getCourses")
  public List<CourseDTO> getStudentCourses(@PathVariable String id) {
    new UserController().itsMe(id);
    try {
      List<CourseDTO> courses = service.getCourses(id);
      courses.forEach(ModelHelper::enrich);
      return courses;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, id);
    }
  }

  @GetMapping("/{id}/getTeams")
  public List<TeamDTO> getStudentTeams(@PathVariable String id) {
    new UserController().itsMe(id);
    try {
      return service.getTeamsForStudent(id);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, id);
    }
  }
}
