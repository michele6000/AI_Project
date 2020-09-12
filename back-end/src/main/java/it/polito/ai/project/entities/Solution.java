package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Solution {
    @Id
    @GeneratedValue
    private Long id;

    private String image; //image of student's solution for the submission
    private String status = null;
    private Long evaluation;
    private int version = 0; //0 non ancora consegnato, 1 prima consegna, incrementato per ogni nuova versione
    private boolean isRevisable = true;

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
