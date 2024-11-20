package edu.brown.cs.student.main.searcher;

import edu.brown.cs.student.main.parser.Parser;
import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.parser.creators.TrivialCreator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/** A Searcher class used for searching through data, containing the search method. */
public class Searcher {
  /** Parsed csv data to search in. */
  private List<List<String>> parsedContent;
  /** boolean indicating if the data has a header row. */
  private boolean headerRow;

  private String csvFileName;

  /**
   * An object used to search CSV data with the search method.
   *
   * @param csvFileName - name of the csv file to search (String, extension optional)
   * @param headerRow - boolean indicating if the file has a header row (true if so, false if not)
   * @throws IllegalArgumentException when given a null input
   * @throws FileNotFoundException if file name isn't found in the data directory
   * @throws IOException if parser fails
   * @throws FactoryFailureException if parser fails
   */
  public Searcher(String csvFileName, boolean headerRow)
      throws IllegalArgumentException, IOException, FactoryFailureException {
    // throw an exception if given a null input
    if (csvFileName == null) {
      throw new IllegalArgumentException("null csvFileName input");
    }
    if (!csvFileName.endsWith(".csv")) {
      csvFileName += ".csv";
    }

    this.csvFileName = csvFileName;

    // get the file path using the name by searching in our resources directory
    String csvFilePath = this.getFilePath(csvFileName);
    this.headerRow = headerRow;

    // parse the data
    Parser<List<String>> parser =
        new Parser<>(new FileReader(csvFilePath), new TrivialCreator(), false, true);
    parser.parse();
    this.parsedContent = parser.getParsedContent();
  }

  /**
   * A method which searches through parsed data for the value given and returns the rows containing
   * the value (not case-sensitive).
   *
   * @param value - the exact value to search for (not case-sensitive)
   * @return a list of all the rows containing the value
   * @throws IllegalArgumentException if given a null input
   */
  public List<List<String>> search(String value) throws IllegalArgumentException {
    if (value == null) {
      throw new IllegalArgumentException("null value input");
    }
    // list to return
    List<List<String>> rowsContainingValue = new ArrayList<>();

    // If there's a header row, skip it in our search
    boolean firstLine = this.headerRow;
    for (List<String> row : this.parsedContent) {
      if (firstLine) {
        firstLine = false;
        continue;
      }
      for (String word : row) {
        if (word.equalsIgnoreCase(value)) {
          rowsContainingValue.add(row);
          break; // we found a match, so don't need to check the rest of the row
        }
      }
    }

    return rowsContainingValue;
  }

  /**
   * This method looks for a specific value in the specified column of parsed data and returns all
   * rows containing that value in the column (not case-sensitive).
   *
   * @param value - the exact value to search for (not case-sensitive)
   * @param colName - the name of the column to search in (not case-sensitive)
   * @return a list of all the rows containing the value in the column
   * @throws IOException if the column name isn't found in the dataset or if there's no header row
   * @throws IllegalArgumentException if given null inputs for any params
   */
  public List<List<String>> search(String value, String colName)
      throws IOException, IllegalArgumentException {
    if (value == null && colName == null) {
      throw new IllegalArgumentException("null inputs");
    } else if (value == null) {
      throw new IllegalArgumentException("null value input");
    } else if (colName == null) {
      throw new IllegalArgumentException("null colName input");
    }

    // if our csv file was empty, return an empty list (avoids bugs with finding colName)
    if (parsedContent.isEmpty()) {
      return new ArrayList<>();
    }

    // Throw an exception if there's no header row, and we're expected to search off of colName
    if (!headerRow) {
      throw new IOException("header row not present, cannot access colName");
    }

    // Find the index corresponding to the column name
    int colIndex = -1;
    for (int i = 0; i < parsedContent.get(0).size(); i++) {
      if (parsedContent.get(0).get(i).equalsIgnoreCase(colName)) {
        colIndex = i;
      }
    }

    // if we didn't find it, throw an exception
    if (colIndex == -1) {
      throw new IOException(
          "column name "
              + colName
              + " not found in dataset "
              + csvFileName
              + ". Columns in dataset: "
              + String.join(", ", parsedContent.get(0)));
    }
    return this.search(value, colIndex);
  }

  /**
   * This method looks for a specific value in the specified column of parsed data and returns all
   * rows containing that value in the column (not case-sensitive).
   *
   * @param value - the exact value to search for (not case-sensitive)
   * @param colIndex - the index of the column to search in (0 corresponds to the first column, 1
   *     the next, etc.)
   * @return a list of all the rows containing the value in the column
   * @throws IllegalArgumentException if given any null inputs, if the colIndex is less than zero,
   *     if the colIndex is out of range.
   */
  public List<List<String>> search(String value, int colIndex) {
    // throw specific errors for various issues with the inputs...
    if (value == null) {
      throw new IllegalArgumentException("null value input");
    } else if (colIndex < 0) {
      throw new IllegalArgumentException("column index " + colIndex + " must be zero or greater");
    } else if (parsedContent.isEmpty()) {
      return new ArrayList<>(); // this is to make sure the below clause won't error
    } else if (colIndex > parsedContent.get(0).size() - 1) {
      throw new IllegalArgumentException(
          "column index "
              + colIndex
              + " out of range of "
              + parsedContent.get(0).size()
              + " number"
              + " of columns");
    }

    // conduct the search
    List<List<String>> rowsContainingValue = new ArrayList<>();
    boolean firstLine = this.headerRow;
    for (List<String> row : this.parsedContent) {
      if (firstLine) {
        firstLine = false;
        continue;
      }

      if (row.get(colIndex).equalsIgnoreCase(value)) {
        rowsContainingValue.add(row);
      }
    }

    return rowsContainingValue;
  }

  /**
   * A method which finds the corresponding file path in the local data directory to a given file
   * name.
   *
   * @param csvFileName - the file name to search for
   * @return the file path corresponding to the file name
   * @throws FileNotFoundException if the file doesn't exist in the directory
   */
  private String getFilePath(String csvFileName) throws FileNotFoundException {
    File dir = new File("data/resources");

    String filePath = getFilePathHelper(dir, csvFileName);
    if (filePath == null) {
      throw new FileNotFoundException(csvFileName + " not found in resources folder");
    }
    return filePath;
  }

  /**
   * Helper method which preforms recursion to find the path corresponding to the csvFileName in the
   * given directory.
   *
   * @param directory - the directory to search in
   * @param csvFileName - the file name we're looking for
   * @return the csv file path, or none if it's not found
   */
  @Nullable
  private String getFilePathHelper(File directory, String csvFileName) {
    File[] files = directory.listFiles();

    if (files == null) {
      return null;
    }

    for (File file : files) {
      if (file.isDirectory()) {
        String result = getFilePathHelper(file, csvFileName);
        if (result != null) {
          return result;
        }
      } else {
        if (file.getName().equalsIgnoreCase(csvFileName)) {
          return file.getAbsolutePath();
        }
      }
    }

    return null;
  }
}
