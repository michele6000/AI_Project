package it.polito.ai.project.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SubmissionDTO {

    private Long id;
    private Timestamp expiryDate;
    private Timestamp releaseDate;
    private Byte[] image;
    private String content;

}
