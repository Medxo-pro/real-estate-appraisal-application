package edu.brown.cs.student.main.parser.creators;

import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.parser.classes.StudentRecord;
import java.util.List;

/** A creator to convert parsed data into a StudentRecord object. */
public class StudentRecordCreator implements CreatorFromRow<StudentRecord> {

  /**
   * Create method which turns a (parsed) list of strings into a StudentRecord object.
   *
   * @param row - the list of strings to be converted
   * @return - the StudentRecord created from the list of strings
   * @throws FactoryFailureException if there are any errors with the conversion
   */
  @Override
  public StudentRecord create(List<String> row) throws FactoryFailureException {
    try {
      if (row.size() != 3) {
        throw new FactoryFailureException(
            "Error creating StudentRecord, " + "incorrect number of columns", row);
      }
      return new StudentRecord(Integer.parseInt(row.get(0)), row.get(1), row.get(2));
    } catch (Exception e) {
      throw new FactoryFailureException("Error creating StudentRecord", row);
    }
  }
}
