package it.polito.ai.project.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class VM {
    @Id
    private String id;
    private String owner;
    private String cpu;
    private String ram;
    private String hdd;
    private String status;
    private String accessLink;

    @ManyToOne
    private VMType vmType;

    @ManyToOne
    private Team team;

    public void setVmType(VMType t){
        vmType=t;
        t.getVMs().add(this);
    }

    public void setTeam(Team t){
        team=t;
        t.getVMInstance().add(this);
    }

}
