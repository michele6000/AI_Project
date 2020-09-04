package it.polito.ai.project.dtos;

import lombok.Data;

@Data
public class TeamDTO {
  private Long id;
  private String name;
  private int status;
  private String courseName;

  // Limitazioni per la VM
  private Integer limit_hdd;
  private Integer limit_cpu;
  private Integer limit_ram;
  private Integer limit_instance;
  private Integer limit_active_instance;

}
