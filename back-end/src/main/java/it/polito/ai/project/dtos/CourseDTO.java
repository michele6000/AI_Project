package it.polito.ai.project.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {
  private String name;
  private int min;
  private int max;
  private boolean enabled;
}
