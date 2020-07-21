package it.polito.ai.project.dtos;

import lombok.Data;

@Data
public class VMTypeDTO {
    private String id;
    private String dockerFile; // visto come path del dockerfile
}
