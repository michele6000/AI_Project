package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.TeamDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.exceptions.TeamServiceException;
import it.polito.ai.project.services.TeamService;
import it.polito.ai.project.services.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
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
            return vmService.modifyVMConfiguration(vmId, vm);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //in caso di "cessione" dell'ownership
    @PostMapping("/{vmId}/modifyOwner")
    public Boolean modifyOwner(@PathVariable Long vmId, @RequestBody String studentID) {
        try {
            return vmService.modifyVMOwner(vmId, studentID);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/addOwner")
    public Boolean addOwner(@PathVariable Long vmId, @RequestBody String studentID) {
        try {
            return vmService.addVMOwner(vmId, studentID);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{vmId}/powerOff")
    public Boolean powerOff(@PathVariable Long vmId) {
        try {
            return vmService.powerVMOff(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @PostMapping("/{vmId}/delete")
    public Boolean delete(@PathVariable Long vmId) {
        try {
            return vmService.deleteVM(vmId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

//    @GetMapping(value = "/getImage/{vmId}", produces = MediaType.IMAGE_JPEG_VALUE)
    @GetMapping("/getImage/{vmId}")
    public String showImage(@PathVariable Long vmId,  Model model) {
//        try {
//            response.addHeader("Access-Control-Allow-Origin","*");
//            response.setContentType("image/jpeg");
//            response.getOutputStream().write(vmService.getVmImage(vmId));
//            response.getOutputStream().close();
//        }catch (Exception e){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error retrieving image!");
//        }
        VMDTO vm = vmService.getVMConfig(vmId);
        TeamDTO team = vmService.retriveTeamFromVm(vmId);
        model.addAttribute("title","VM - "+vm.getId().toString());
        model.addAttribute("vmSpec","TeamId: "+team.getId()+" TeamName: "+team.getName() + " VmId: "+vmId);
        model.addAttribute("vmConfig","VmConfig: V-CPU ("+vm.getCpu()+" core), V-Ram ("+vm.getRam()+" MB), HDD ("+vm.getHdd()+" MB)");
        if (vm.getStatus().equals("poweron"))
            return "vmTemplateOnline";
        else{
            return "vmTemplate";
        }
    }



}
