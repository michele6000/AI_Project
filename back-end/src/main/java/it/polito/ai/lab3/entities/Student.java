package it.polito.ai.lab3.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class Student {
  @Id
  private String id;

  private String name;
  private String firstName;

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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
    teams.add(team);
    team.getMembers().add(this);
  }

  public void removeTeam(Team team) {
    teams.remove(team);
    team.getMembers().remove(this);
  }
}
