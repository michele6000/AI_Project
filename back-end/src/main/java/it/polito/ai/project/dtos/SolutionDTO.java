package it.polito.ai.project.dtos;

import lombok.Data;

@Data
public class SolutionDTO {
    private Long id;
    private String image; //image of student's solution for the submission
    private String version;
    private Long evaluation;
    private String status;
    private boolean isRevisable; //flag
}
