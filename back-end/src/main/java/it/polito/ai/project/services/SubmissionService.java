package it.polito.ai.project.services;

import it.polito.ai.project.dtos.SolutionDTO;
import it.polito.ai.project.dtos.SubmissionDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface SubmissionService {

    @PreAuthorize("hasAnyRole('ROLE_PROFESSOR')")
    String addSubmission(SubmissionDTO submissionDTO, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    List<SubmissionDTO> getAllSubmissions(String courseName);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    SubmissionDTO getLastSubmission(String courseName);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    SubmissionDTO getSubmission(String courseName, Long id);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    SolutionDTO getSolution(String studentId, Long submissionId);

    @PreAuthorize("hasAnyRole('ROLE_PROFESSOR')")
    boolean evaluateSolution(String studentId, Long submissionId, Long evaluation);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    String addSolution(Long submissionId, SolutionDTO solutionDTO, String studentId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    String updateSolution(Long submissionId, SolutionDTO solutionDTO, String studentId);
}
