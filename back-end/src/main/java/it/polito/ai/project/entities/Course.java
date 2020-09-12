package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
//  @JoinColumn(name = "VMType_id") //TODO:chiedere a malnati
    private VMType vmType;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    private List<Professor> professors = new ArrayList<>();

    @OneToMany(mappedBy = "course")
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

    public void addSubmission(Submission s) {
        submissions.add(s);
        s.setCourse(this);
    }

    public void deleteStudent(Student s) {
        students.remove(s);
        s.getCourses().remove(this);
    }
}


