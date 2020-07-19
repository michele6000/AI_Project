package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.ProfessorDTO;
import it.polito.ai.project.exceptions.CourseNotFoundException;
import it.polito.ai.project.exceptions.StudentNotFoundException;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.TeamService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserinfoController {
  @Autowired
  TeamService service;

  @GetMapping("/me")
  public ResponseEntity currentUser(
    @AuthenticationPrincipal UserDetails userDetails
  ) {
    Map<Object, Object> model = new HashMap<>();
    model.put("username", userDetails.getUsername());
    model.put(
      "roles",
      userDetails
        .getAuthorities()
        .stream()
        .map(a -> ((GrantedAuthority) a).getAuthority())
        .collect(Collectors.toList())
    );
    return ResponseEntity.ok(model);
  }

  @PostMapping("/addProfessor")
  public ProfessorDTO addProfessor(@RequestBody ProfessorDTO professor) {
    if (!service.addProfessor(professor)) throw new ResponseStatusException(
      HttpStatus.CONFLICT,
      professor.getId()
    );

    return professor;
  }

  @PostMapping("/addProfessor/{courseName}")
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
}
