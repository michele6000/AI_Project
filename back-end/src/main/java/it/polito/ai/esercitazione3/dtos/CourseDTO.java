package it.polito.ai.esercitazione3.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {
  String name;
  int min;
  int max;
  boolean enabled;
}
