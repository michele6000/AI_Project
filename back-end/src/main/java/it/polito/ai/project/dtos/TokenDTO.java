package it.polito.ai.project.dtos;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class TokenDTO {
  String id;
  Long teamId;
  Timestamp expiryDate;
}
