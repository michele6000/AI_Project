package it.polito.ai.lab3.repositories;

import it.polito.ai.lab3.entities.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, String> {}
