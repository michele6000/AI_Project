package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Team {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int status;
    private String proposer;

    // Limitazioni per la VM
    private Integer limit_hdd; // espresso in MB
    private Integer limit_cpu; // espresso in core
    private Integer limit_ram; // espresso in MB
    private Integer limit_instance;
    private Integer limit_active_instance;

    @ManyToOne
    @JoinColumn(name = "VMType_id")
    private VMType vmType;

    @OneToMany(mappedBy = "team")
    private List<VM> VMInstance = new ArrayList<>() ;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "team_students",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> members = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "team_pendent_students",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> pendentStudents = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "team_confirmed_students",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> confirmedStudents = new ArrayList<>();

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
        pendentStudents.remove(student);
        confirmedStudents.remove(student);
    }

    public void confirmStudent(Student student) {
        pendentStudents.remove(student);
        confirmedStudents.add(student);
    }
}
