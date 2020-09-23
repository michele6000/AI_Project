package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Submission {

    @Id
    @GeneratedValue
    private Long id;
    private String content;
    private Timestamp expiryDate;
    private Timestamp releaseDate;
    @Lob
    private Byte[] image;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "submission")
    private List<Solution> solutions = new ArrayList<>();

    public void setCourse(Course c) {
        course = c;
        c.getSubmissions().add(this);
    }

    public void addSolution(Solution solution) {
        solutions.add(solution);
        solution.setSubmission(this);
    }
}
