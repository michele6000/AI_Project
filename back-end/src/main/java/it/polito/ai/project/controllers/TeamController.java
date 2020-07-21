package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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


}
