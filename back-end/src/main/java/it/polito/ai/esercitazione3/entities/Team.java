package it.polito.ai.esercitazione3.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class Team {
  //    @JsonIgnore
  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(
    name = "team_members",
    joinColumns = @JoinColumn(name = "team_id"),
    inverseJoinColumns = @JoinColumn(name = "student_id")
  )
  List<Student> members = new ArrayList<>();

  @Id
  @GeneratedValue
  private Long id;

  private String name;
  private int status;

  @ManyToOne
  @JoinColumn(name = "course_id")
  private Course course;

  public void setCourse(Course course) {
    if (course != null) {
      this.course = course;
      this.course.getTeams().add(this);
    } else {
      this.course.getTeams().remove(this);
      this.course = null;
    }
  }

  public void addMember(Student student) {
    this.members.add(student);
    student.getTeams().add(this);
  }

  public void rmMember(Student student) {
    this.members.remove(student);
    student.getTeams().remove(this);
  }
}
