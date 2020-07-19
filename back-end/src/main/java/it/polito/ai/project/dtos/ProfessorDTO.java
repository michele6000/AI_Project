package it.polito.ai.lab3.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class ProfessorDTO {
  String id;
  String firstName;
  String name;
}
