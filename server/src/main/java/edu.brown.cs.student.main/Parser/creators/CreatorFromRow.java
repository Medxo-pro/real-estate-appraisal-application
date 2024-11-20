package edu.brown.cs.student.main.parser.creators;

import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import java.util.List;

/** Interface for creators which turn parsed data into objects. */
public interface CreatorFromRow<T> {

  /**
   * Create method which turns a (parsed) list of strings into a general object.
   *
   * @param row - the list of strings to be converted
   * @return - the object created from the list of strings
   * @throws FactoryFailureException if there are any errors with the conversion
   */
  T create(List<String> row) throws FactoryFailureException;
}
