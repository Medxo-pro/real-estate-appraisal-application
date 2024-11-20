package edu.brown.cs.student.main.commandprocessor;

import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.searcher.Searcher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

/**
 * commandprocessor that runs a CSV search tool. The program searches a CSV file based on the
 * command-line inputs. It continues to run until the user exits. When it receives input, it parses
 * it and executes the appropriate command.
 */
public class SearchCommandProcessor {
  private final InputStream in;
  private final PrintStream out;
  private final PrintStream err;
  /** Variables that help track where in the query we are. */
  private Stage stage = Stage.INITIAL;

  private Searcher searcher;
  private String value;

  /** Enum to represent different stages of the search process. */
  private enum Stage {
    INITIAL,
    SEARCHER_CREATED,
    VALUE_SET
  }

  /**
   * Create a SearchCommandProcessor that listens for input, sends output, and sends errors to the
   * given streams.
   *
   * @param in the input stream to use
   * @param out the output stream to use
   * @param err the error stream to use
   */
  public SearchCommandProcessor(InputStream in, PrintStream out, PrintStream err) {
    this.in = in;
    this.out = out;
    this.err = err;
  }

  /**
   * Create a SearchCommandProcessor using the standard System-defined input, output, and error
   * streams.
   */
  public SearchCommandProcessor() {
    this(System.in, System.out, System.err);
  }

  /** Start listening for input, and don't stop until you see "exit". */
  public void run() {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(this.in))) {

      // Starting messages and input variable declared
      String input;
      this.out.println(
          "Welcome to the CSV file searcher! \nInput \"EXIT\" to exit, \"HELP\""
              + " for help, or \"RESET\" to reset.");
      promptForFileName();

      boolean running = true;
      while (running && (input = br.readLine()) != null) {
        // Switch on the input to check specials (exit, reset, help)
        switch (input.trim().toUpperCase()) {
          case "":
            this.err.println("ERROR: Empty input");
            break;
          case "EXIT":
            this.out.println("Program exiting");
            running = false; // Break the loop if exit is inputted
            break;
          case "HELP": // Print the help response if help is inputted
            printHelpResponse();
            break;
          case "RESET": // Reset to the initial stage if RESET is inputted
            stage = Stage.INITIAL;
            searcher = null;
            value = null;
            promptForFileName();
            break;
          default: // If none of the special cases were inputted, check which stage we're in
            switch (stage) {
              case INITIAL: // If it's the initial stage, we need to create our searcher still
                // createSearcher returns true if it succeeds in creation
                // advance the stage if so, otherwise continue prompting the file name
                if (createSearcher(input)) {
                  stage = Stage.SEARCHER_CREATED;
                  promptForValue();
                }
                break;
              case SEARCHER_CREATED: // If the searcher was created, we now need the value
                // Get the value, confirm it, and update the stage
                value = input.trim();
                this.out.println("Value to search for is set to: " + value);
                promptForColumnIdentifier();
                stage = Stage.VALUE_SET;
                break;

              case VALUE_SET: // Now we have the value and are checking for a column identifier
                // Initialize the result to null so we know if we fail to run the search
                List<List<String>> result = null;

                // If no column identifier is inputted, run the search normally
                if (input.trim().equalsIgnoreCase("None")) {
                  result = searcher.search(value);
                } else { // Otherwise, use our helper
                  result = runSearch(input);
                }

                // Print the results, or prompt for a column identifier again
                if (result != null) {
                  for (List<String> row : result) {
                    this.out.println(String.join(", ", row));
                  }
                  if (result.isEmpty()) {
                    this.out.println("No results found");
                  }
                  // Reset to when the searcher was created and prompt for a new value
                  stage = Stage.SEARCHER_CREATED;
                  value = null;
                  promptForValue();
                }
                break;

              default:
                this.err.println("ERROR: Error reading input.");
            }
        }
      }
    } catch (IOException ex) {
      this.err.println("ERROR: Error reading input.");
      System.exit(1); // exit with error status
    }
  }

  /**
   * Method to create a searcher object and initialize the searcher instance variable when given a
   * user input.
   *
   * @param input the users input, containing a file name and Y/N representing if the file has a
   *     header row
   * @return true if the searcher was created, false if not
   */
  private boolean createSearcher(String input) {
    String[] splitInput = input.trim().split("\\s");

    // If the input is formatted incorrectly, restart
    if (splitInput.length != 2
        || !(splitInput[1].equalsIgnoreCase("Y") || splitInput[1].equalsIgnoreCase("N"))) {
      this.err.println("ERROR: Make sure your input follows the format: file_name Y/N");
      return false;
    }

    // Attempt the creation of the searcher and print error messages if we fail for any reason
    try {
      boolean hasHeader = splitInput[1].equalsIgnoreCase("Y");
      searcher = new Searcher(splitInput[0], hasHeader);
      this.out.println(
          "Created searcher for "
              + splitInput[0]
              + (hasHeader ? " file with a header row." : " file without a header row."));
      return true;
    } catch (IOException | FactoryFailureException e) {
      this.err.println("ERROR: Unable to create searcher - " + e.getMessage());
      return false;
    }
  }

  /**
   * Method which runs searches with column identifiers on the search object.
   *
   * @param input the user input with the category of the column identifier (I for index, N for
   *     name) followed directly by the column identifier.
   * @return the result of the search under the identified column.
   */
  private List<List<String>> runSearch(String input) {
    // Separate the category and identifiers
    String category = input.substring(0, 1);
    String identifier = input.substring(1);

    // If it
    if (category.equalsIgnoreCase("I")) {
      try {
        int colIndex = Integer.parseInt(identifier);
        if (colIndex < 0) {
          this.err.println("ERROR: column index cannot be less than zero");
        }
        return searcher.search(value, colIndex);
      } catch (NumberFormatException e) {
        this.err.println("ERROR: \"I\" should be followed by a number");
        return null;
      } catch (IllegalArgumentException e) {
        this.err.println("ERROR: column index out of range.");
        return null;
      }
    } else if (category.equalsIgnoreCase("N")) {
      try {
        return searcher.search(value, identifier);
      } catch (IOException e) {
        this.err.println("ERROR: " + e);
        return null;
      }
    } else {
      this.err.println("ERROR: column identifier should begin with N or I or be None");
      return null;
    }
  }

  /** Prints a message prompting the user for the file info. */
  private void promptForFileName() {
    this.out.println(
        "Please enter the name of the csv file you'd like to search in, and "
            + "whether or not it has a header row \nex. students_data Y");
  }

  /** Prints a message prompting the user for the search value. */
  private void promptForValue() {
    this.out.println("Please input your search query's value, or RESET to search a new file ");
  }

  /** Prints a message prompting the user for a column identifier. */
  private void promptForColumnIdentifier() {
    this.out.println(
        "Please input a column identifier or 'None'.\nIf the identifier is an"
            + " index, begin it with I, and if it is a name begin it with N\nex. I6, NStudentID");
  }

  /** Prints the help response. */
  private void printHelpResponse() {
    this.out.println(
        """
       CSV File Searcher
       =================
       Begin by inputting a csv file name and Y or N (Y if there's a header row and N if not)
       Example inputs: stardata Y, students Y, malformed_signs N
      \s
       After the searcher was successfully created, input the value you'd like to search for
       Example inputs: Sarah Parker, Sol, 300
      \s
       After the value is set, please input the type of column identifier you'd like to provide followed directly by the identifier, or None
       This can be the column index (0 for the first column, 1 for the next) or the column name, or None
       Example inputs: I0, I6, NStudentIDs, N2020, None
      \s
       Then the search will be conducted, and you can re-input a value and identifier, or input RESET to search in a new file.
      """);
  }

  /** This is the entry point for the command-line application. */
  public static void main(String[] args) {
    SearchCommandProcessor proc = new SearchCommandProcessor();
    proc.run();
  }
}
