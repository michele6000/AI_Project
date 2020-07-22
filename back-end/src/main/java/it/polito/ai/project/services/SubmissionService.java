package it.polito.ai.project.services;

import it.polito.ai.project.dtos.SubmissionDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface SubmissionService {

    @PreAuthorize("hasAnyRole('ROLE_PROFESSOR')")
    boolean addSubmission(SubmissionDTO submissionDTO);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    List<SubmissionDTO> getAllSubmissions(String courseName);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN','ROLE_PROFESSOR')")
    SubmissionDTO getLastSubmission(String courseName);
}
