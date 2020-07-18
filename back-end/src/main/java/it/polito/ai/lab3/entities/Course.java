package it.polito.ai.lab3.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Course {
  @Id
  private String name;

  private int min;
  private int max;
  private boolean enabled;

  @ManyToMany(mappedBy = "courses")
  private List<Student> students = new ArrayList<>();

  @OneToMany(mappedBy = "course")
  private List<Team> teams;

  @ManyToMany(mappedBy = "courses")
  List<Professor> professors = new ArrayList<>();

  public void addStudent(Student student) {
    students.add(student);
    student.getCourses().add(this);
  }

  public void addTeam(Team team) {
    teams.add(team);
    team.setCourse(this);
  }

  public void addProfessor(Professor p) {
    professors.add(p);
    p.getCourses().add(this);
  }
}
