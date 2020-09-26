package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.TeamService;
import it.polito.ai.project.services.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/API/vm")
public class VMController {

    @Autowired
    TeamService service;

    @Autowired
    VmService vmService;

    @GetMapping("/{vmId}/getCurrentConfiguration")
    public VMDTO getVMConfig(@PathVariable Long vmId) {
        try {
            return vmService.getVMConfig(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/modifyConfiguration")
    public Boolean modifyConfiguration(@PathVariable Long vmId, @RequestBody VMDTO vm) {
        try {
            return vmService.modifyVMConfiguration(vmId, vm, getCurrentUsername());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/modifyOwner")
    public Boolean modifyOwner(@PathVariable Long vmId, @RequestBody String studentID) {
        try {
            return vmService.modifyVMOwner(vmId, studentID,getCurrentUsername());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/addOwner")
    public Boolean addOwner(@PathVariable Long vmId, @RequestBody String studentID) {
        try {
            return vmService.addVMOwner(vmId, studentID,getCurrentUsername());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{vmId}/getOwners")
    public List<StudentDTO> getOwners(@PathVariable Long vmId) {
        try {
            return vmService.getVMOwners(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/powerOn")
    public Boolean powerOn(@PathVariable Long vmId) {
        try {
            return vmService.powerVMOn(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/powerOff")
    public Boolean powerOff(@PathVariable Long vmId) {
        try {
            return vmService.powerVMOff(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @PostMapping("/{vmId}/delete")
    public Boolean delete(@PathVariable Long vmId) {
        try {
            return vmService.deleteVM(vmId, getCurrentUsername());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
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
