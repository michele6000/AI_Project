package it.polito.ai.project.dtos;

import lombok.Data;

@Data
public class UserDTO {
  Long id;
  String username;
  String password;
  String role;
}
