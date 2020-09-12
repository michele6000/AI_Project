package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.ProfessorDTO;
import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.UserDTO;
import it.polito.ai.project.security.jwt.JwtTokenProvider;
import it.polito.ai.project.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Qualifier("customUserDetailsService")
    @Autowired
    org.springframework.security.core.userdetails.UserDetailsService UserDetailsService;

    @Autowired
    TeamService service;

    @PostMapping("/signin")
    public ResponseEntity<Map<Object, Object>> signin(
            @Valid @RequestBody UserDTO data
    ) {
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, data.getPassword())
            );
            String token = jwtTokenProvider.createToken(
                    username,
                    UserDetailsService
                            .loadUserByUsername(username)
                            .getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList())
            );

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied!");
        }
    }


    @PostMapping("/addProfessor")
    public ProfessorDTO addProfessor(@RequestBody ProfessorDTO professor) {
        if (!professor.getEmail().endsWith("@polito.it"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not allowed");

        if (!service.addProfessor(professor)) throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                professor.getId()
        );
        return professor;
    }

    @PostMapping("/addStudent")
    public StudentDTO addProfessor(@RequestBody StudentDTO student) {
        if (!student.getEmail().endsWith("@studenti.polito.it"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not allowed");

        if (!service.addStudent(student)) throw new ResponseStatusException(HttpStatus.CONFLICT, student.getId());
        return student;
    }
}
