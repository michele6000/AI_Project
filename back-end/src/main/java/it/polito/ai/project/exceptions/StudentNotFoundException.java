package it.polito.ai.project.exceptions;

public class StudentNotFoundException extends TeamServiceException {

    public StudentNotFoundException(String message) {
        super(message);
    }
}
