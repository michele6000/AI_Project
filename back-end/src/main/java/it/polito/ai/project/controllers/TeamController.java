package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.TeamService;
import it.polito.ai.project.services.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/team")
public class TeamController {

    @Autowired
    TeamService service;

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
    public String getTeamStats(@PathVariable Long teamId) {
        try {
            return vmService.getTeamStat(teamId);
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

    @PostMapping("/{teamId}/{studentId}/deleteMember")
    public void deleteMember(@PathVariable Long teamId, @PathVariable String studentId) {
        try {
            //se non sono professore (sono studente) e sto provando a cancellare un membro diverso da me stesso
            if (!getCurrentRoles().contains("PROFESSOR") && getCurrentUsername()!=studentId) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "You are not allowed to delete this member!"
    );
            service.deleteMember(teamId,studentId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{teamId}/evictTeam")
    //TODO: chi può farlo oltre al professore? Può essere usata dallo studente per annullare vecchie proposte, ma come?
    public void evictTeam(@PathVariable Long teamId) {
        try {
//            if (!getCurrentRoles().contains("PROFESSOR")) throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN,
//                    "You are not allowed to evict this team!"
//            );
            service.evictTeam(teamId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{teamId}/createVmInstance")
    public VMDTO createVmInstance(@PathVariable Long teamId, @RequestBody VMDTO vm) {
        try {
            return vmService.createVmInstance(teamId,vm,getCurrentUsername());
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
                .map(a -> ((GrantedAuthority) a).getAuthority())
                .collect(Collectors.toList());
    }

}
