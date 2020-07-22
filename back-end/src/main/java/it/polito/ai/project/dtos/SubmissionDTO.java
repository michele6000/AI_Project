package it.polito.ai.project.dtos;

import lombok.Data;

import java.awt.*;
import java.sql.Timestamp;

@Data
public class SubmissionDTO {

    Long id;
    private Timestamp expiryDate;
    private Timestamp releaseDate;
    private String content; //image

}
