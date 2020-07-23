package it.polito.ai.project.entities;

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
  private String email;
  private String image; //TODO: mettere binary

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(
    name = "student_course",
    joinColumns = @JoinColumn(name = "student_id"),
    inverseJoinColumns = @JoinColumn(name = "course_name")
  )
  private List<Course> courses = new ArrayList<>();

  @ManyToMany(mappedBy = "members")
  private List<Team> teams = new ArrayList<>();

  @OneToMany
  private List<Solution> solutions=new ArrayList<>();

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(
          name = "student_vms",
          joinColumns = @JoinColumn(name = "student_id"),
          inverseJoinColumns = @JoinColumn(name = "vm_id")
  )
  private List<VM> vms;

  public void addCourse(Course course) {
    courses.add(course);
    course.getStudents().add(this);
  }

  public void addTeam(Team team) {
    teams.add(team);
    team.getMembers().add(this);
  }

  public void addSolution(Solution solution) {
    solutions.add(solution);
    solution.setStudent(this);
  }

  public void removeTeam(Team team) {
    teams.remove(team);
    team.getMembers().remove(this);
  }

  public void removeCourse(Course c){
    courses.remove(c);
    c.getStudents().remove(this);
  }
}
