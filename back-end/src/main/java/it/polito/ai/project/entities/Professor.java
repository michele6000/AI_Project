package it.polito.ai.lab3.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class Professor {
  @Id
  String id;

  String firstName;
  String name;

  @JoinTable(
    name = "professor_course",
    joinColumns = @JoinColumn(name = "professor_id"),
    inverseJoinColumns = @JoinColumn(name = "course_name")
  )
  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  List<Course> courses = new ArrayList<>();

  public void addCourses(Course c) {
    courses.add(c);
    c.getProfessors().add(this);
  }
}
