package it.polito.ai.project.exceptions;

public class ProfessorNotFoundException extends TeamServiceException {

    public ProfessorNotFoundException(String message) {
        super(message);
    }
}