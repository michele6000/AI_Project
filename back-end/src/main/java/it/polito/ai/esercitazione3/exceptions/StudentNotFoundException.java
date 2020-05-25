package it.polito.ai.esercitazione3.exceptions;

public class StudentNotFoundException extends TeamServiceException {

  public StudentNotFoundException(String s) {
    super(s);
  }
}
