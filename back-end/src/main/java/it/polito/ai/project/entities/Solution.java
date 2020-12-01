package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class Solution {
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private Byte[] image;
    @Lob
    private Byte[] correction=null;
    private String status = "";
    private Long evaluation=0L;
    private int version = 0;
    private boolean isRevisable = true;
    private Timestamp creationDate;


    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    public void setStudent(Student s) {
        student = s;
        s.getSolutions().add(this);
    }

    public void setSubmission(Submission s) {
        submission = s;
        s.getSolutions().add(this);
    }


}
