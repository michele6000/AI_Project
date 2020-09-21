package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.ProfessorDTO;
import it.polito.ai.project.dtos.SolutionDTO;
import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.UserDTO;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.repositories.StudentRepository;
import it.polito.ai.project.security.jwt.JwtTokenProvider;
import it.polito.ai.project.services.TeamService;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
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
    @Autowired
    StudentRepository studentRepository;

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


//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.IMAGE_PNG);
//
//
//            model.put("image",new HttpEntity<>(, headers));


            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied!");
        }
    }

    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public void showImage(@RequestParam("id") String username, HttpServletResponse response, HttpServletRequest request)
            throws ServletException, IOException{
        response.addHeader("Access-Control-Allow-Origin","*");
        response.setContentType("image/jpeg");
        response.getOutputStream().write(service.getImage(username.split("@")[0]));
        response.getOutputStream().close();
    }


    @PostMapping("/addProfessor")
    public ProfessorDTO addProfessor(@RequestPart("user") ProfessorDTO professor,
                                     @RequestPart("file") MultipartFile file) {
        if (!professor.getEmail().endsWith("@polito.it"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not allowed");

        if (!service.addProfessor(professor,file))
            throw new ResponseStatusException( HttpStatus.CONFLICT, professor.getId());
        return professor;
    }

    @PostMapping("/addStudent")
    public StudentDTO addProfessor(@RequestPart("user") StudentDTO student,
                                   @RequestPart("file") MultipartFile file) {
        if (!student.getEmail().endsWith("@studenti.polito.it"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not allowed");

        if (!service.addStudent(student,file)) throw new ResponseStatusException(HttpStatus.CONFLICT, student.getId());
        return student;
    }
}
