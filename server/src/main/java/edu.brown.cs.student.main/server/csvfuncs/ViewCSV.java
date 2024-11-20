package edu.brown.cs.student.main.server.csvfuncs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.parser.Parser;
import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.parser.creators.TrivialCreator;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/** Class for viewing csv files. */
public class ViewCSV {
  /** Object used to transfer data into json form. */
  static Moshi moshi = new Moshi.Builder().build();

  /**
   * Views a CSV file with the specified file path.
   *
   * @param filepath The file path of the CSV file to view.
   * @return the parsed data of the csv file in the form of json data
   * @throws IOException if the parser has errors reading the CSV file
   * @throws FactoryFailureException if the parser's Trivial Creator has errors converting the csv
   *     file to Strings
   */
  public String viewCSV(String filepath) throws IOException, FactoryFailureException {
    // Parses and gets the csv data
    Parser<List<String>> parser =
        new Parser<>(new FileReader(filepath), new TrivialCreator(), false, false);
    parser.parse();
    List<List<String>> parsedData = parser.getParsedContent();

    // Initializes an adapter to a List of List of Strings class then uses it to parse the JSON.
    java.lang.reflect.Type type =
        Types.newParameterizedType(
            List.class, Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);

    // Return the viewable, json format data
    return adapter.toJson(parsedData);
  }
}
