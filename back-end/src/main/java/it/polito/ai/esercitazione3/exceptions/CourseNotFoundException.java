package it.polito.ai.esercitazione3.exceptions;

public class CourseNotFoundException extends TeamServiceException {

  public CourseNotFoundException(String s) {
    super(s);
  }
}
