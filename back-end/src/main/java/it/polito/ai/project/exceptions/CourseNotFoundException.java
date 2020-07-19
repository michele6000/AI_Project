package it.polito.ai.project.exceptions;

public class CourseNotFoundException extends TeamServiceException {

  public CourseNotFoundException(String message) {
    super(message);
  }
}
