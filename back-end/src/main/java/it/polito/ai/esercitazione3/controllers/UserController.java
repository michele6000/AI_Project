package it.polito.ai.esercitazione3.controllers;

import it.polito.ai.esercitazione3.dtos.ProfessorDTO;
import it.polito.ai.esercitazione3.exceptions.TeamServiceException;
import it.polito.ai.esercitazione3.services.TeamService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {
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
    if (service.addProfessor(professor)) return professor;
    return new ProfessorDTO();
  }

  @PostMapping("/addProfessorToCourse")
  public boolean addProfessorToCourse(
    @RequestParam String profId,
    @RequestParam String courseName
  ) {
    try {
      return service.addProfessorToCourse(profId, courseName);
    } catch (TeamServiceException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  private List<String> loggedUserInfo() {
    List<String> userInfo = new ArrayList<>();
    Object principal = SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getPrincipal();
    if (principal instanceof UserDetails) {
      userInfo.add(((UserDetails) principal).getUsername());
      userInfo.addAll(
        ((UserDetails) principal).getAuthorities()
          .stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.toList())
      );
    } else {
      userInfo.add(principal.toString());
    }
    return userInfo;
  }

  //    Sulla base del funzionamento del portale della didattica del politecnico
  //    si assume che ad un determinato username sia associato solo un ruolo.

  //    Si assume, inoltre, che un Docente A possa avere accesso alle informazioni
  //    riguardanti altri docenti, come ad esempio la lista degli studenti di un corso
  //    che non è tenuto dal Docente A.

  //    admin -> Amministratore di sistema
  //    @polito.it -> Docente
  //    @studenti.polito.it -> Studente

  private String getUsername() {
    return loggedUserInfo().get(0);
  }

  private String getRole() {
    return loggedUserInfo().get(1);
  }

  public void itsMe(String id) {
    if (
      getRole().equals("ROLE_STUDENT") &&
      !id.equals(getUsername().split("@")[0])
    ) throw new ResponseStatusException(
      HttpStatus.UNAUTHORIZED,
      "You are not logged as: " + id
    );
  }
}
