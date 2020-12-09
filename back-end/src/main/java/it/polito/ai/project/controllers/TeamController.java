package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.TeamDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.NotificationService;
import it.polito.ai.project.services.TeamService;
import it.polito.ai.project.services.VmService;
import it.polito.ai.project.wrappers.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/team")
public class TeamController {

    @Autowired
    TeamService service;

    @Autowired
    NotificationService notifyService;

    @Autowired
    VmService vmService;

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
            return vmService.getTeamVMs(teamId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/stats")
    public GenericResponse getTeamStats(@PathVariable Long teamId) {
        try {
            GenericResponse response = new GenericResponse();
            response.setGenericResponse(vmService.getTeamStat(teamId));
            return response;
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{teamId}/usage")
    public TeamDTO getTeamUsage(@PathVariable Long teamId) {
        try {
            return vmService.getTeamUsage(teamId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
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

    @PostMapping("/{teamId}/{studentId}/deleteMember")
    public void deleteMember(@PathVariable Long teamId, @PathVariable String studentId) {
        try {
            if (!getCurrentRoles().contains("ROLE_PROFESSOR") && !getCurrentUsername().equals(studentId))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this member!");
            service.deleteMember(teamId, studentId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{teamId}/{studentId}/addMember")
    public void addMember(@PathVariable Long teamId, @PathVariable String studentId) {
        List<String> students = new ArrayList<>();
        try {
            service.addMember(teamId, studentId);
            students.add(studentId);
            notifyService.notifyTeam(service.getTeam(teamId), students, new Timestamp(new Date().getTime()+60000000));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{teamId}/evictTeam")
    public void evictTeam(@PathVariable Long teamId) {
        try {
            if (!getCurrentRoles().contains("ROLE_PROFESSOR"))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to evict this team!");
            service.evictTeam(teamId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{teamId}/createVmInstance")
    public VMDTO createVmInstance(@PathVariable Long teamId, @RequestBody VMDTO vm) {
        try {
            return vmService.createVmInstance(teamId, vm, getCurrentUsername());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{teamId}/setTeamLimits")
    public TeamDTO setTeamLimit(@PathVariable Long teamId, @RequestBody TeamDTO team) {
        try {
            if (!getCurrentRoles().contains("ROLE_PROFESSOR")) throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed set limits of this team!"
            );
            team.setId(teamId);
            return vmService.setTeamLimit(team);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{teamId}/deleteAllProposals")
    public boolean deleteAllProposal(@PathVariable Long teamId) {
        try {
            service.deleteAllProposals(Collections.singletonList(teamId));
            return true;
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

    private List<String> getCurrentRoles() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

}
