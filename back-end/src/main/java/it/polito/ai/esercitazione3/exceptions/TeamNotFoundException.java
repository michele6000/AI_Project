package it.polito.ai.esercitazione3.exceptions;

public class TeamNotFoundException extends TeamServiceException {

  public TeamNotFoundException(String s) {
    super(s);
  }
}
