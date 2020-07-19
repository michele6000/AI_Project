package it.polito.ai.project.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
public class Team {
  @Id
  @GeneratedValue
  private Long id;
  private String name;
  private int status;

  // Limitazioni per la VM
  private String limit_hdd;
  private String limit_cpu;
  private String limit_ram;
  private String limit_instance;
  private String limit_active_instance;

  @ManyToOne
  private VMType vmType;

  @OneToMany
  private List<VM> VMInstance;

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
