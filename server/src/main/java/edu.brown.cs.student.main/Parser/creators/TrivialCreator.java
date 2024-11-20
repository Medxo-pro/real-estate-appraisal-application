package edu.brown.cs.student.main.parser.creators;

import java.util.List;

/** A creator to convert parsed data, in the form of a list of strings, into a list of strings. */
public class TrivialCreator implements CreatorFromRow<List<String>> {

  /**
   * Create method which turns a (parsed) list of strings into a list of strings.
   *
   * @param row - the list of strings to be converted
   * @return - the same list of strings
   */
  @Override
  public List<String> create(List<String> row) {
    return row;
  }
}
