package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.ProfessorDTO;
import it.polito.ai.project.dtos.StudentDTO;
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
}
