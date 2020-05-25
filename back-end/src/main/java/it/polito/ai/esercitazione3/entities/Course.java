package it.polito.ai.esercitazione3.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class Course {
  @ManyToMany(mappedBy = "courses")
  List<Professor> professors = new ArrayList<>();

  @Id
  private String name;

  private int min;
  private int max;
  private boolean enabled;

  @ManyToMany(fetch = FetchType.EAGER, mappedBy = "courses")
  private List<Student> students = new ArrayList<>();

  @OneToMany(mappedBy = "course")
  private List<Team> teams;

  public void addStudent(Student student) {
    students.add(student);
    student.getCourses().add(this);
  }

  public void addTeam(Team team) {
    teams.add(team);
    team.setCourse(this);
  }

  public boolean addProfessor(Professor p) {
    if (professors.contains(p)) {
      return false;
    }
    professors.add(p);
    p.courses.add(this);
    return true;
  }
}
