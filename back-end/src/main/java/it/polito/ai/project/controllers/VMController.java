package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.entities.VM;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/API/vm")
public class VMController {

    @Autowired
    TeamService service;

    @GetMapping("/{vmId}/getCurrentConfiguration")
    public VMDTO getVMConfig(@PathVariable Long vmId) {
        try {
            return service.getVMConfig(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/modifyConfiguration")
    public Boolean modifyConfiguration(@PathVariable Long vmId, @RequestBody VMDTO vm) {
        try {
            return service.modifyVMConfiguration(vmId,vm);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //in caso di "cessione" dell'ownership
    @PostMapping("/{vmId}/modifyOwner")
    public Boolean modifyOwner(@PathVariable Long vmId, @RequestBody String studentID) {
        try {
            return service.modifyVMOwner(vmId,studentID);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/addOwner")
    public Boolean addOwner(@PathVariable Long vmId, @RequestBody String studentID) {
        try {
            return service.addVMOwner(vmId,studentID);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{vmId}/getOwners")
    public List<StudentDTO> getOwners(@PathVariable Long vmId) {
        try {
            return service.getVMOwners(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/powerOn")
    public Boolean powerOn(@PathVariable Long vmId) {
        try {
            return service.powerVMOn(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/powerOff")
    public Boolean powerOff(@PathVariable Long vmId) {
        try {
            return service.powerVMOff(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @PostMapping("{vmId}/delete")
    public Boolean delete(@PathVariable Long vmId) {
        try {
            return service.deleteVM(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }








}