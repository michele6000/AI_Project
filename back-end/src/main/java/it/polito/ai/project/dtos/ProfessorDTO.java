package it.polito.ai.project.dtos;

import lombok.Data;

@Data
public class ProfessorDTO {
    private String id;
    private String firstName;
    private String name;
    private String email;
    private Byte[] image;
}
