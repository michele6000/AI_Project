package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.ProfessorDTO;
import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.UserDTO;
import it.polito.ai.project.repositories.StudentRepository;
import it.polito.ai.project.security.jwt.JwtTokenProvider;
import it.polito.ai.project.services.CustomUserDetailsService;
import it.polito.ai.project.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.*;
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
    @Autowired
    CustomUserDetailsService userService;

    @PostMapping("/signin")
    public ResponseEntity<Map<Object, Object>> signin( @Valid @RequestBody UserDTO data) {
        try {
            String username = data.getUsername();
            if (!service.checkActiveUser(username))
                throw new BadCredentialsException("You must verify your email!");

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong username/password");
        }
    }

    @GetMapping("/verifyEmail/{codedToken}")
    public String enableUser(@PathVariable String codedToken){
        byte[] decodedBytes = Base64.getDecoder().decode(codedToken);
        String plainToken = new String(decodedBytes);
        if(service.checkToken(plainToken.split("_")[0])){
            service.enableUser(plainToken.split("_")[1]);
            return "Your account is now active!";
        }
        return "Token expired, please do registration again!";
    }

    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public void showImage(@RequestParam("id") String username, HttpServletResponse response, HttpServletRequest request)
            throws ServletException, IOException {

        try {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("image/jpeg");
            response.getOutputStream().write(service.getImage(username.split("@")[0]));
            response.getOutputStream().close();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error retrieving image!");
        }

    }


    @PostMapping("/addProfessor")
    public ProfessorDTO addProfessor(@RequestPart("user") UserDTO professor,
                                     @RequestPart("file") MultipartFile file) {
        if (!professor.getEmail().endsWith("@polito.it"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not allowed");

        try{
            return service.addProfessor(professor, file);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/addStudent")
    public StudentDTO addStudent(@RequestPart("user") UserDTO student,
                                   @RequestPart("file") MultipartFile file) {
        if (!student.getEmail().endsWith("@studenti.polito.it"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not allowed");

        try{
            return service.addStudent(student, file);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
