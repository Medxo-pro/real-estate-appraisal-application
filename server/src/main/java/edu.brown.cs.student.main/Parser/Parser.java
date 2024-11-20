package edu.brown.cs.student.main.parser;

import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.parser.creators.CreatorFromRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/** A Parser class used for parsing data and containing the parse method. */
public class Parser<T> {
  /** Reader object for reading from the input source. */
  private Reader reader;
  /** describes how to convert rows into data objects. */
  private CreatorFromRow<T> creator;
  /** The content parsed using the reader object. */
  private List<T> parsedContent;
  /** Indicates whether there's a header row to skip when parsing data. */
  private boolean headerRow;
  /** If there's a set number of columns. */
  private boolean setNumberColumns;

  /**
   * Used to parse CSV data from any Reader object provided with any creator class provided.
   *
   * @param reader - reader objected provided to parse data from/with
   * @param creator - creator class provided to convert Strings to data objects
   * @param headerRow - indicates whether there's a header row to skip when parsing data (true if
   *     there is)
   * @param setNumColumns - indicates whether there's a set number of columns in the data (if true,
   *     we throw an error when encountering an inconsistent number of columns)
   * @throws IllegalArgumentException when given a null input
   */
  public Parser(Reader reader, CreatorFromRow<T> creator, boolean headerRow, boolean setNumColumns)
      throws IllegalArgumentException {
    // detailed error throwing for user
    if (reader == null && creator == null) {
      throw new IllegalArgumentException("null reader and creator inputs");
    } else if (reader == null) {
      throw new IllegalArgumentException("null reader input");
    } else if (creator == null) {
      throw new IllegalArgumentException("null creator input");
    }
    this.reader = reader;
    this.creator = creator;
    this.headerRow = headerRow;
    this.setNumberColumns = setNumColumns;
    this.parsedContent = new ArrayList<>();
  }

  /**
   * a method which parses the given data with the Parser object's reader, runs the data through the
   * Parser objects creator, then adds it to the parsed content instance variable. Skips the first
   * line if the headerRow instance variable is true.
   *
   * @throws IOException when buffer reader fails to read-in a line
   * @throws FactoryFailureException when data isn’t of the form the creator object expected
   */
  public void parse() throws FactoryFailureException, IOException {
    String line;
    Pattern regexSplitCsvRow =
        Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")" + "*(?![^\\\"]*\\\"))");
    BufferedReader readInBuffer =
        new BufferedReader(reader); // wraps around readers to improve efficiency when reading
    // skip the header if it's there
    if (headerRow) {
      line = readInBuffer.readLine();
    }

    // use firstline and numcolumns to handle errors when there's an inconsistent number of columns
    boolean firstLine = true;
    int numColumns = 0;
    while ((line = readInBuffer.readLine()) != null) {
      String[] result = regexSplitCsvRow.split(line);
      List<String> lineToArr = Arrays.stream(result).toList();
      if (setNumberColumns) {
        if (firstLine) {
          numColumns = lineToArr.size();
          firstLine = false;
        } else if (lineToArr.size() != numColumns) {
          throw new FactoryFailureException(
              "Inconsistent number of columns at row: " + line, lineToArr);
        }
      }
      T arrToData = creator.create(lineToArr);
      parsedContent.add(arrToData);
    }
    readInBuffer.close();
  }

  public List<T> getParsedContent() {
    return new ArrayList<>(parsedContent);
  }
}
