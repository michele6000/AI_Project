package it.polito.ai.project.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TokenDTO {
    private String id;
    private Long teamId;
    private Timestamp expiryDate;
}
