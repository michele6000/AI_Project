package it.polito.ai.lab3.dtos;

import java.sql.Timestamp;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class TokenDTO {
  String id;
  Long teamId;
  Timestamp expiryDate;
}
