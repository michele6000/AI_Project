package it.polito.ai.project.dtos;

import lombok.Data;

import javax.persistence.Id;

@Data
public class SolutionDTO {
    String id;
    String image; //image of student's solution for the submission
    String version;
}
