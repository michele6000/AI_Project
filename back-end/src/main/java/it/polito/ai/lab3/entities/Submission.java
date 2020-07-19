package it.polito.ai.lab3.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class Submission {

    @Id
    @GeneratedValue
    private Long id;

    private Timestamp expiryDate;
    private Timestamp releaseDate;
    private Byte content; //image

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
