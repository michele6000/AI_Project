package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Data
public class Token {
    @Id
    private String id;

    private Long teamId;
    private Timestamp expiryDate;
}
