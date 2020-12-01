package it.polito.ai.project.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SolutionDTO {
    private Long id;
    private Byte[] image;
    private Byte[] correction=null;
    private int version;
    private Long evaluation;
    private String status;
    private boolean isRevisable; //flag
    private Timestamp creationDate;
    private String matricola;
    private String name;
    private String surname;
}
