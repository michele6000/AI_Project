package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.CourseDTO;
import it.polito.ai.project.dtos.SolutionDTO;
import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.TeamDTO;
import it.polito.ai.project.exceptions.StudentNotFoundException;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.SubmissionService;
import it.polito.ai.project.services.TeamService;
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

  @Autowired
  SubmissionService submissionService;

  @GetMapping({ "", "/" })
  public List<StudentDTO> all() {
    List<StudentDTO> students = service.getAllStudents();
    students.forEach(ModelHelper::enrich);
    return students;
  }

//  @PostMapping({ "", "/" })
//  public StudentDTO addStudent(@RequestBody StudentDTO dto) {
//    if (!service.addStudent(dto)) throw new ResponseStatusException(
//            HttpStatus.CONFLICT,
//            dto.getId()
//    ); else return ModelHelper.enrich(dto);
//  }

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



  @GetMapping("/{id}/courses")
  public List<CourseDTO> getCourses(@PathVariable String id) {
    if (!isMe(id)) throw new ResponseStatusException(
      HttpStatus.FORBIDDEN,
      "You are not allowed to access this information!"
    );
    try {
      return service.getCourses(id);
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

//  SOLUTIONS START

  @GetMapping("/{studentId}/{submissionId}/getAllSolutions")
  public List<SolutionDTO> getSolutions(@PathVariable String studentId, @PathVariable Long submissionId) {
    if (getCurrentRoles().contains("ROLE_STUDENT") && !isMe(studentId)) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "You are not allowed to access this information!"
    );
    try {
      return submissionService.getAllSolutions(submissionId);
    } catch (TeamServiceException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{studentId}/{submissionId}/getHistorySolutions")
  public List<SolutionDTO> getSolutionsForStudent(@PathVariable String studentId, @PathVariable Long submissionId) {
    if (getCurrentRoles().contains("ROLE_STUDENT") && !isMe(studentId)) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "You are not allowed to access this information!"
    );
    try {
      return submissionService.getAllSolutionsForStudent(submissionId,studentId);
    } catch (TeamServiceException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{studentId}/{submissionId}/getLatestSolution")
  public SolutionDTO getLastSolution(@PathVariable String studentId, @PathVariable Long submissionId) {
    if (getCurrentRoles().contains("ROLE_STUDENT") && !isMe(studentId)) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "You are not allowed to access this information!"
    );
    try {
      return submissionService.getLastSolution(studentId,submissionId);
    } catch (TeamServiceException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/{studentId}/{solutionId}/getSolution")
  public SolutionDTO getSolution(@PathVariable String studentId, @PathVariable Long solutionId) {
    if (getCurrentRoles().contains("ROLE_STUDENT") && !isMe(studentId)) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "You are not allowed to access this information!"
    );
    try {
      return submissionService.getSolution(solutionId);
    } catch (TeamServiceException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/{studentId}/{submissionId}/evaluateLatestSolution")
  public boolean evaluateLastSolution(@PathVariable String studentId, @PathVariable Long submissionId, @RequestParam Long evaluation) {
//    if (!getCurrentRoles().contains("PROFESSOR"))  throw new ResponseStatusException(
//            HttpStatus.FORBIDDEN,
//            "You are not allowed to evaluate a solution!"
//    );
    try {
      return submissionService.evaluateLastSolution(studentId,submissionId, evaluation, getCurrentUsername());
    } catch (Exception e) {
      if(e instanceof TeamServiceException)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
      else throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PostMapping("/{studentId}/{solutionId}/evaluateSolution")
  public boolean evaluateSolution(@PathVariable String studentId, @PathVariable Long solutionId, @RequestParam Long evaluation) {
//    if (!getCurrentRoles().contains("PROFESSOR")) throw new ResponseStatusException(
//            HttpStatus.FORBIDDEN,
//            "You are not allowed to evaluate a solution!"
//    );
    try {
      return submissionService.evaluateSolution(solutionId, evaluation, getCurrentUsername());
    } catch (Exception e) {
      if(e instanceof TeamServiceException)
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
      else throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
    }
  }

  @PostMapping("/{studentId}/{submissionId}/addSolution")
  public String addSolution(@PathVariable String studentId, @PathVariable Long submissionId, @RequestBody SolutionDTO sol) {
    if (!isMe(studentId)) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "You are not allowed to add a solution!"
    );
    try {
      return submissionService.addSolution(submissionId, sol, studentId);
    } catch (TeamServiceException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
  @PostMapping("/{studentId}/{submissionId}/updateSolution")
  public String updateSolution(@PathVariable String studentId, @PathVariable Long submissionId, @RequestBody SolutionDTO sol) {
    if (!isMe(studentId)) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "You are not allowed to add a solution!"
    );
    try {
      return submissionService.updateSolution(submissionId, sol, studentId);
    } catch (TeamServiceException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  //  SOLUTIONS END

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
