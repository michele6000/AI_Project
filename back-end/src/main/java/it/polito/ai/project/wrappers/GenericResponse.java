package it.polito.ai.project.wrappers;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class GenericResponse extends RepresentationModel<GenericResponse> {
    String genericResponse;
}
