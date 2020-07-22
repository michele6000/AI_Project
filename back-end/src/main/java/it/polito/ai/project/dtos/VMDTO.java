package it.polito.ai.project.dtos;

import lombok.Data;

@Data
public class VMDTO {
    private String id;
    private String owner;
    private String cpu;
    private String ram;
    private String hdd;
    private String status;
    private String accessLink;
}
