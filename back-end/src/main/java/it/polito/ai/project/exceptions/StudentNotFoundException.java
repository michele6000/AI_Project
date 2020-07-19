package it.polito.ai.lab3.exceptions;

public class StudentNotFoundException extends TeamServiceException {

  public StudentNotFoundException(String message) {
    super(message);
  }
}
