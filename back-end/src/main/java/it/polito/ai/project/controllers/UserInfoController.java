package it.polito.ai.project.controllers;

import it.polito.ai.project.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserInfoController {
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
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
        model.put("myId", userDetails.getUsername().split("@")[0]);
        return ResponseEntity.ok(model);
    }
}
