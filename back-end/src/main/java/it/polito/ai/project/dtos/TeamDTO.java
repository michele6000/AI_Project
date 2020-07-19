package it.polito.ai.lab3.dtos;

import it.polito.ai.lab3.entities.Course;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class TeamDTO {
  private Long id;
  private String name;
  private int status;
  private String courseName;
}
