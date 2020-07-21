package it.polito.ai.project.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.Data;

@Entity
@Data
public class Course {
  @Id
  private String name;
  private String acronymous;
  private int min;
  private int max;
  private boolean enabled;

  @ManyToOne
  private VMType vmType;

  @ManyToMany(mappedBy = "courses")
  private List<Student> students = new ArrayList<>();

  @OneToMany(mappedBy = "course")
  private List<Team> teams = new ArrayList<>();

  @ManyToMany(mappedBy = "courses")
  private List<Professor> professors = new ArrayList<>();

  @OneToMany() //AUTO-MAPPING
  private List<Submission> submissions = new ArrayList<>();

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

  public void addSubmission(Submission s){
    submissions.add(s);
    s.setCourse(this);
  }

  public void deleteStudent(Student s){
    students.remove(s);
    s.getCourses().remove(this);
  }
}


