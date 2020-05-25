package it.polito.ai.esercitazione3.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class StudentDTO extends RepresentationModel<StudentDTO> {
  @CsvBindByName
  String id;

  @CsvBindByName
  String name;

  @CsvBindByName
  String firstName;
}
