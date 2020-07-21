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
    private String version;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;


}
