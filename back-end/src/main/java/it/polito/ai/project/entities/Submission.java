package it.polito.ai.project.entities;

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
    private Course course;

    public void setCourse(Course c){
        course = c;
        c.getSubmissions().add(this);
    }
}
