package it.polito.ai.lab3.controllers;

import static org.springframework.http.ResponseEntity.ok;

import it.polito.ai.lab3.dtos.UserDTO;
import it.polito.ai.lab3.security.jwt.JwtTokenProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
}
