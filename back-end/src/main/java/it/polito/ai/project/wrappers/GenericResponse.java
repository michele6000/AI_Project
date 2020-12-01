package it.polito.ai.project.wrappers;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
public class GenericResponse extends RepresentationModel<GenericResponse> {
    String genericResponse;
    List<Long> teamIDs;
}
