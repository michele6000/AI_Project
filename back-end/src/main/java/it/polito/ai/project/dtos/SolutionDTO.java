package it.polito.ai.project.dtos;

import lombok.Data;

@Data
public class SolutionDTO {
    private Long id;
    private Byte[] image;
    private int version;
    private Long evaluation;
    private String status;
    private boolean isRevisable; //flag
}
