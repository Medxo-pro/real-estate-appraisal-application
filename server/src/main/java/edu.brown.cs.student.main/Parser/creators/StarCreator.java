package edu.brown.cs.student.main.parser.creators;

import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.parser.classes.Star;
import java.util.List;

/** A creator to convert parsed data into a Star object. */
public class StarCreator implements CreatorFromRow<Star> {

  /**
   * Create method which turns a (parsed) list of strings into a Star object.
   *
   * @param row - the list of strings to be converted
   * @return - the Star created from the list of strings
   * @throws FactoryFailureException if there are any errors with the conversion
   */
  @Override
  public Star create(List<String> row) throws FactoryFailureException {
    try {
      if (row.size() != 5) {
        throw new FactoryFailureException(
            "Error creating Star, " + "incorrect number of columns", row);
      }
      double[] coordinates = new double[3];
      coordinates[0] = Double.parseDouble(row.get(2));
      coordinates[1] = Double.parseDouble(row.get(3));
      coordinates[2] = Double.parseDouble(row.get(4));
      return new Star(Integer.parseInt(row.get(0)), row.get(1), coordinates);
    } catch (Exception e) {
      throw new FactoryFailureException("Error creating Star", row);
    }
  }
}
