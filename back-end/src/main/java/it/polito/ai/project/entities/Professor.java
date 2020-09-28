package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Professor {
    @Id
    private String id;
    private String firstName;
    private String name;
    private String email;
    @Lob
    private Byte[] image;

    @JoinTable(
            name = "professor_course",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "course_name")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Course> courses = new ArrayList<>();

    public void addCourses(Course c) {
        courses.add(c);
        c.getProfessors().add(this);
    }
}
