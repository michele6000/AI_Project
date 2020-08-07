package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/API/team")
public class TeamController {

    @Autowired
    TeamService service;

    @GetMapping("/{teamId}/members")
    public List<StudentDTO> getMembers(@PathVariable Long teamId) {
        try {
            return service.getMembers(teamId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/vms")
    public List<VMDTO> getTeamVMs(@PathVariable Long teamId) {
        try {
            return service.getTeamVMs(teamId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/stats")
    public String getTeamStats(@PathVariable Long teamId) {
        try {
            return service.getTeamStat(teamId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/confirmedStudents")
    public List<StudentDTO> getTeamConfirmedStudents(@PathVariable Long teamId) {
        try {
            return service.getConfirmedStudents(teamId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/pendentStudents")
    public List<StudentDTO> getTeamPendentStudents(@PathVariable Long teamId) {
        try {
            return service.getPendentStudents(teamId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{teamId}/createVmInstance")
    public VMDTO createVmInstance(@PathVariable Long teamId, @RequestBody VMDTO vm) {
        try {
            return service.createVmInstance(teamId,vm,getCurrentUsername());
        } catch (TeamServiceException e) {
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


}
