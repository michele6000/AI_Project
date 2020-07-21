package it.polito.ai.project.dtos;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class TokenDTO {
  private String id;
  private Long teamId;
  private Timestamp expiryDate;
}
