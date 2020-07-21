package it.polito.ai.project.repositories;

import it.polito.ai.project.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, String> {
}
