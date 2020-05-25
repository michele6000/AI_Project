package it.polito.ai.esercitazione3.controllers;

import it.polito.ai.esercitazione3.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notification")
public class NotificationController {
  String home = "notification.html";

  @Autowired
  NotificationService notificationService;

  @GetMapping("/confirm/{token}")
  public String confirm(@PathVariable String token, Model model) {
    if (notificationService.confirm(token)) {
      model.addAttribute(
        "message",
        "Confirmation of group membership occurred!\nYour group is now Active"
      );
    } else {
      if (!notificationService.hasErrors()) model.addAttribute(
        "message",
        "Confirmation of group membership occurred! \nWaiting all members confirmation for activation..."
      ); else model.addAttribute("message", "An error has occurred!");
    }
    return home;
  }

  @GetMapping("/reject/{token}")
  public String reject(@PathVariable String token, Model model) {
    if (notificationService.reject(token)) model.addAttribute(
      "message",
      "The group has been eliminated!"
    ); else model.addAttribute(
      "message",
      "Something go wrong!\n\nToken: " + token
    );
    return home;
  }
}
