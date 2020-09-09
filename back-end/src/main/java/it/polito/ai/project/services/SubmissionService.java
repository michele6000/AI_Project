package it.polito.ai.project.services;

import it.polito.ai.project.dtos.SolutionDTO;
import it.polito.ai.project.dtos.SubmissionDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface SubmissionService {

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PROFESSOR')")
    SubmissionDTO addSubmission(SubmissionDTO submissionDTO, String courseName, String profId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    List<SubmissionDTO> getAllSubmissions(String courseName, String username);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    SubmissionDTO getLastSubmission(String courseName, String username);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    SubmissionDTO getSubmission(String courseName, Long id, String username);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    SolutionDTO getLastSolution(String studentId, Long submissionId, String username);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN')")
    String addSolution(Long submissionId, SolutionDTO solutionDTO, String studentId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN')")
    String updateSolution(Long submissionId, SolutionDTO solutionDTO, String studentId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    List<SolutionDTO> getAllSolutionsForStudent(Long submissionId, String studentId);

    @PreAuthorize("hasAnyRole('ROLE_PROFESSOR','ROLE_ADMIN')")
    List<SolutionDTO> getAllSolutions(Long submissionId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    SolutionDTO getSolution(Long solutionId, String username);

    @PreAuthorize("hasAnyRole('ROLE_PROFESSOR','ROLE_ADMIN')")
    boolean evaluateLastSolution(String studentId, Long submissionId, Long evaluation, String profId);

    @PreAuthorize("hasAnyRole('ROLE_PROFESSOR','ROLE_ADMIN')")
    boolean evaluateSolution(Long solutionId, Long evaluation, String profId);

    @PreAuthorize("hasAnyRole('ROLE_PROFESSOR','ROLE_ADMIN')")
    void stopRevisions(Long solutionId, String profId);

}
