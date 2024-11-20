package edu.brown.cs.student.main.parser.classes;

import java.util.ArrayList;
import java.util.List;

/** Exception class for creators. */
public class FactoryFailureException extends Exception {
  final List<String> row;

  /**
   * Communicates that the data wasn’t of the form the creator object expected.
   *
   * @param message the message sent with the error
   */
  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}
