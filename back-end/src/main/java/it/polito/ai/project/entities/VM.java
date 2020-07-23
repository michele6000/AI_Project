package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class VM {
    @Id
    private String id;

    private Integer cpu;
    private Integer ram;
    private Integer hdd;
    private String status;
    private String accessLink;

    @ManyToOne
    @JoinColumn(name = "VMType_id")
    private VMType vmType;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToMany
    private List<Student> owners;

    public void setVmType(VMType t){
        vmType=t;
        t.getVMs().add(this);
    }

    public void setTeam(Team t){
        team=t;
        t.getVMInstance().add(this);
    }

}
