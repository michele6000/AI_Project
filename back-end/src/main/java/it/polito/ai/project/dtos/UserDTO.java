package it.polito.ai.project.dtos;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Lob;

@Data
public class UserDTO {
    private String username; //matricola
    private String firstName;
    private String name;
    private String email; //email completa
    private String password;
    private Byte[] image;
}
