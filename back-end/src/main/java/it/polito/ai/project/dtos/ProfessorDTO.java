package it.polito.ai.project.dtos;

import lombok.Data;

@Data
public class ProfessorDTO {
  String id;
  String firstName;
  String name;
  String email;
  String image; //TODO: mettere binary
}
