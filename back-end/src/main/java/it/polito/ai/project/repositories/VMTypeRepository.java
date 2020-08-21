package it.polito.ai.project.repositories;

import it.polito.ai.project.entities.VMType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VMTypeRepository extends JpaRepository<VMType, Long> {
}
