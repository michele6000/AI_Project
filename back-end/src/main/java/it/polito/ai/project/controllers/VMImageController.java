package it.polito.ai.project.controllers;

import it.polito.ai.project.dtos.TeamDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.services.VmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/API/vm")
public class VMImageController {
    @Autowired
    VmService vmService;

    @GetMapping("/getImage/{vmId}")
    public String showImage(@PathVariable Long vmId, Model model) {
        VMDTO vm = vmService.getVMConfig(vmId);
        TeamDTO team = vmService.retriveTeamFromVm(vmId);
        model.addAttribute("title", "VM - " + vm.getId().toString());
        model.addAttribute("vmSpec", "TeamId: " + team.getId() + " TeamName: " + team.getName() + " VmId: " + vmId);
        model.addAttribute("vmConfig", "VmConfig: V-CPU (" + vm.getCpu() + " core), V-Ram (" + vm.getRam() + " MB), HDD (" + vm.getHdd() + " MB)");
        if (vm.getStatus().equals("poweron"))
            return "vmTemplateOnline";
        else {
            return "vmTemplate";
        }
    }
}
