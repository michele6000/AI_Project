package it.polito.ai.lab3.controllers;

import it.polito.ai.lab3.exceptions.TeamServiceException;
import it.polito.ai.lab3.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/API/notifications")
public class NotificationController {
  @Autowired
  NotificationService service;

  @GetMapping("/confirm/{token}")
  public String confirm(@PathVariable String token, Model model) {
    try {
      if (service.confirm(token)) {
        model.addAttribute("error", "display:none;");
        model.addAttribute("message", "You joined the team!\nTEAM ACTIVATED!");
      } else {
        if (service.isEverythingOk().get()) {
          model.addAttribute("error", "display:none;");
          model.addAttribute("message", "You joined the team!");
        } else {
          model.addAttribute("success", "display:none;");
          model.addAttribute("message", "Sorry, something went wrong!");
        }
      }

      return "notifications";
    } catch (TeamServiceException e) {
      model.addAttribute("success", "display:none;");
      model.addAttribute("message", "EXCEPTION!-->" + e.getMessage());
      return "notifications";
    }
  }

  @GetMapping("/reject/{token}")
  public String reject(@PathVariable String token, Model model) {
    try {
      if (service.reject(token)) {
        model.addAttribute("error", "display:none;");
        model.addAttribute(
          "message",
          "You refused the invitation to the team.\nTEAM EVICTED!"
        );
      } else {
        model.addAttribute("success", "display:none;");
        model.addAttribute("message", "Sorry, something went wrong!");
      }

      return "notifications";
    } catch (TeamServiceException e) {
      model.addAttribute("success", "display:none;");
      model.addAttribute("message", "EXCEPTION!-->" + e.getMessage());
      return "notifications";
    }
  }
}
