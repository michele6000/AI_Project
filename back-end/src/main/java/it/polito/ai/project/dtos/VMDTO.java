package it.polito.ai.project.dtos;

import lombok.Data;

import javax.persistence.Lob;

@Data
public class VMDTO {
    private Long id;
    private String owner;
    private Integer cpu;
    private Integer ram;
    private Integer hdd;
    private String status;
    private String accessLink;
    @Lob
    private Byte[] image;
}
