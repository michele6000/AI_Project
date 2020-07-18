package it.polito.ai.lab3.exceptions;

public class CourseNotFoundException extends TeamServiceException {

  public CourseNotFoundException(String message) {
    super(message);
  }
}
