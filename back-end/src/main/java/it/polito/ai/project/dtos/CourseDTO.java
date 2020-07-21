package it.polito.ai.project.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {
  private String name;
  private String acronymous;
  private int min;
  private int max;
  private boolean enabled;

  // Limitazioni per la VM
  private Integer limit_hdd;
  private Integer limit_cpu;
  private Integer limit_ram;
  private Integer limit_instance;
  private Integer limit_active_instance;

}
