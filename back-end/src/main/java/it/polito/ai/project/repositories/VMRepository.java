package it.polito.ai.project.repositories;

import it.polito.ai.project.entities.VM;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VMRepository extends JpaRepository<VM, Long> {
}
