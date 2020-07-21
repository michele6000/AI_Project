package it.polito.ai.project.dtos;

import lombok.Data;

@Data
public class TeamDTO {
  private Long id;
  private String name;
  private int status;
  private String courseName;

  // Limitazioni per la VM
  private String limit_hdd;
  private String limit_cpu;
  private String limit_ram;
  private String limit_instance;
  private String limit_active_instance;

}
