package edu.brown.cs.student.main.server.csvfuncs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.searcher.Searcher;
import java.io.File;
import java.io.IOException;
import java.util.List;

/** Class for searching csv files through an endpoint. */
public class SearchCSV {
  /** Moshi instance used to convert data objects to json form. */
  static Moshi moshi = new Moshi.Builder().build();

  /**
   * Function that searches the given csv file for the searchkey and returns all rows containing it.
   * If specified, this function can take in a columnID (name or index) and search specifically in
   * that column.
   *
   * @param filepath - the path to the file they want to search in
   * @param ifHeader - whether the data has a header row
   * @param searchKey - the value to search for
   * @param columnID - an identifier to a column to search in specifically, search in all cols if
   *     null
   * @return - A string representation of what json did with our results
   * @throws IOException if there are any issues with reading the csv file
   */
  public String searchCSV(String filepath, Boolean ifHeader, String searchKey, String columnID)
      throws IOException, FactoryFailureException {

    // Get our information for the search
    List<List<String>> rowsFound;
    File file = new File(filepath);
    String filename = file.getName();
    Searcher searcher = new Searcher(filename, ifHeader);

    if (columnID == null || columnID.isEmpty()) {
      rowsFound = searcher.search(searchKey);
    } else {

      /* The purpose of this statement is to handle the case where a column name is an integer (e.g.
      2020). so if there's an error with the column number being out of bounds or strange, we check
      to see if it works as a column name before throwing that error */

      try {
        // Check if the colID can be parsed into an int
        int columnNumber = Integer.parseInt(columnID);

        try {
          // Try to search using it if so
          rowsFound = searcher.search(searchKey, columnNumber);
        } catch (IllegalArgumentException e) {
          // If we fail, see if it may be a column name, but if that fails throw the old error
          try {
            rowsFound = searcher.search(searchKey, columnID);
          } catch (IllegalArgumentException | IOException e2) {
            throw new IllegalArgumentException(e);
          }
        }

      } catch (NumberFormatException e) {
        // Try to search assuming the colID is a string, allow errors to occur if we fail
        rowsFound = searcher.search(searchKey, columnID);
      }
    }

    // Initializes an adapter to a List of List of Strings class then uses it to parse the JSON.
    java.lang.reflect.Type type =
        Types.newParameterizedType(
            List.class, Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);

    String jsonData = adapter.toJson(rowsFound);

    // Return the viewable, json formatted rowsFound
    return jsonData;
  }
}
