package it.polito.ai.project.wrappers;

import it.polito.ai.project.dtos.SubmissionDTO;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Submission extends SubmissionDTO{
    SubmissionDTO submissionDTO;
    MultipartFile multipartFile;
}
