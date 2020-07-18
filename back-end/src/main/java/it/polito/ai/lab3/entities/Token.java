package it.polito.ai.lab3.entities;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Token {
  @Id
  private String id;

  private Long teamId;
  private Timestamp expiryDate;
}
