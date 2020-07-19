package it.polito.ai.lab3.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class UserDTO {
  Long id;
  String username;
  String password;
  String role;
}
