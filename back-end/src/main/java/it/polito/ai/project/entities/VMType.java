package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class VMType {
    @Id
    @GeneratedValue
    private Long id;
    private String dockerFile; // visto come path del dockerfile

    @OneToMany(mappedBy = "vmType")
    private List<Course> courses = new ArrayList<>() ;// una VM_type può essere associata a cpiù corsi ma non viceversa

    @OneToMany(mappedBy = "vmType")
    private List<VM> VMs= new ArrayList<>() ;   // posso avere più istanze con lo stesso tipo ma non viceversa

    @OneToMany(mappedBy = "vmType")
    private List<Team> teams= new ArrayList<>() ; //posso avere più team che usano la stessa vm type

    public void addCourse(Course c) {
        courses.add(c);
        c.setVmType(this);
    }

    public void addVM(VM v) {
        VMs.add(v);
        v.setVmType(this);
    }

    public void addTeam(Team t) {
        teams.add(t);
        t.setVmType(this);
    }
}
