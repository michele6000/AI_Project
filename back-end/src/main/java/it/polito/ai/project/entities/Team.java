package it.polito.ai.lab3.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Data
public class Team {
  @Id
  @GeneratedValue
  private Long id;

  private String name;
  private int status;

  @ManyToOne
  @JoinColumn(name = "course_id")
  private Course course;

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(
    name = "team_students",
    joinColumns = @JoinColumn(name = "team_id"),
    inverseJoinColumns = @JoinColumn(name = "student_id")
  )
  private List<Student> members = new ArrayList<>();

  public void setCourse(Course course) {
    if (course != null) {
      this.course = course;
      if (!course.getTeams().contains(this)) course.addTeam(this);
    } else {
      this.course.getTeams().remove(this);
      this.course = null;
    }
  }

  public void addMember(Student student) {
    members.add(student);
    student.getTeams().add(this);
  }

  public void removeMember(Student student) {
    members.remove(student);
    student.getTeams().remove(this);
  }
}
