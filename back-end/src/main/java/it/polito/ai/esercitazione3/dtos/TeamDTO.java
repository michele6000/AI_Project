package it.polito.ai.esercitazione3.dtos;

import lombok.Data;

@Data
public class TeamDTO {
  private Long id;
  private String name;
  private int status;
  private String courseName;
}
