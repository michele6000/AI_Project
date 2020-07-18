package it.polito.ai.lab3.controllers;

import it.polito.ai.lab3.dtos.CourseDTO;
import it.polito.ai.lab3.dtos.StudentDTO;
import it.polito.ai.lab3.dtos.TeamDTO;
import it.polito.ai.lab3.exceptions.StudentNotFoundException;
import it.polito.ai.lab3.services.TeamService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

  @GetMapping("/{id}")
  public StudentDTO getOne(@PathVariable String id) {
    Optional<StudentDTO> student = service.getStudent(id);

    if (!student.isPresent()) throw new ResponseStatusException(
      HttpStatus.CONFLICT,
      id
    ); else {
      StudentDTO studentDTO = student.get();
      return ModelHelper.enrich(studentDTO);
    }
  }

  @PostMapping({ "", "/" })
  public StudentDTO addStudent(@RequestBody StudentDTO dto) {
    if (!service.addStudent(dto)) throw new ResponseStatusException(
      HttpStatus.CONFLICT,
      dto.getId()
    ); else return ModelHelper.enrich(dto);
  }

  @GetMapping("/{id}/courses")
  public List<CourseDTO> getCourses(@PathVariable String id) {
    if (!isMe(id)) throw new ResponseStatusException(
      HttpStatus.FORBIDDEN,
      "You are not allowed to access this information!"
    );
    try {
      List<CourseDTO> courses = service.getCourses(id);
      return courses;
    } catch (StudentNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{id}/teams")
  public List<TeamDTO> getTeamsForStudent(@PathVariable String id) {
    if (!isMe(id)) throw new ResponseStatusException(
      HttpStatus.FORBIDDEN,
      "You are not allowed to access this information!"
    );
    try {
      return service.getTeamsForStudent(id);
    } catch (StudentNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  private String getCurrentUsername() {
    return SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getName()
      .split("@")[0];
  }

  private List<String> getCurrentRoles() {
    return SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getAuthorities()
      .stream()
      .map(a -> ((GrantedAuthority) a).getAuthority())
      .collect(Collectors.toList());
  }

  private boolean isMe(String id) {
    return (
      id.equals(getCurrentUsername()) ||
      !getCurrentRoles().contains("ROLE_STUDENT")
    );
  }
}
