package it.polito.ai.esercitazione3.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class Student {
  @Id
  private String id;

  private String name;
  private String firstName;

  @ManyToMany(
    fetch = FetchType.EAGER,
    cascade = { CascadeType.PERSIST, CascadeType.MERGE }
  )
  @JoinTable(
    name = "student_course",
    joinColumns = @JoinColumn(name = "student_id"),
    inverseJoinColumns = @JoinColumn(name = "course_name")
  )
  private List<Course> courses = new ArrayList<>();

  @ManyToMany(mappedBy = "members")
  private List<Team> teams = new ArrayList<>();

  public void addCourse(Course course) {
    courses.add(course);
    course.getStudents().add(this);
  }

  public void addTeam(Team team) {
    this.teams.add(team);
    team.getMembers().add(this);
  }

  public void rmTeam(Team team) {
    this.teams.remove(team);
    team.getMembers().remove(this);
  }
}
