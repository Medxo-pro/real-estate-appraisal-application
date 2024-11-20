package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.commandprocessor.SearchCommandProcessor;
import edu.brown.cs.student.mocks.MockSystemIn;
import edu.brown.cs.student.mocks.MockSystemOut;
import java.io.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestSearchCommandProcessor {
  MockSystemIn mockIn;
  MockSystemOut mockOut;
  MockSystemOut mockErr;
  SearchCommandProcessor proc;

  @BeforeEach
  public void setUp() {
    try {
      this.mockIn = MockSystemIn.build();
      this.mockOut = MockSystemOut.build();
      this.mockErr = MockSystemOut.build();
      this.proc =
          new SearchCommandProcessor(
              mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** Testing creating searchers using the SCP, including their errors and resetting. */
  @Test
  public void testCreatingSearchers() throws IOException {

    mockIn.println("...");
    mockIn.println("student_data Y");
    mockIn.println("students Y");
    mockIn.println("reset");
    mockIn.println("blah");
    mockIn.println("mally.csv Y");
    mockIn.println("ten-star.csv N");
    mockIn.println("exit");

    proc.run();

    assertTrue(mockOut.terminal().ready());
    String welcome = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    welcome += mockOut.terminal().readLine();
    assertEquals(
        "Welcome to the CSV file searcher! Input \"EXIT\" to exit, \"HELP\""
            + " for help, or \"RESET\" to reset.",
        welcome);

    assertTrue(mockOut.terminal().ready());
    String out1 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out1 += mockOut.terminal().readLine();
    assertEquals(
        "Please enter the name of the csv file you'd like to search in, and"
            + " whether or not it has a header row ex. students_data Y",
        out1);

    assertTrue(mockErr.terminal().ready());
    String err1 = mockErr.terminal().readLine();
    assertEquals("ERROR: Make sure your input follows the format: file_name Y/N", err1);

    assertTrue(mockErr.terminal().ready());
    String err2 = mockErr.terminal().readLine();
    assertEquals(
        "ERROR: Unable to create searcher - " + "student_data.csv not found in resources folder",
        err2);

    assertTrue(mockOut.terminal().ready());
    String out2 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out2 += mockOut.terminal().readLine();
    assertEquals(
        "Created searcher for students file with a header row."
            + "Please input your search query's value, or RESET to search a new file ",
        out2);

    assertTrue(mockOut.terminal().ready());
    String out4 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out4 += mockOut.terminal().readLine();
    assertEquals(
        "Please enter the name of the csv file you'd like to search in, and"
            + " whether or not it has a header row ex. students_data Y",
        out4);

    assertTrue(mockErr.terminal().ready());
    String err3 = mockErr.terminal().readLine();
    assertEquals("ERROR: Make sure your input follows the format: file_name Y/N", err3);

    assertTrue(mockErr.terminal().ready());
    String err4 = mockErr.terminal().readLine();
    assertEquals(
        "ERROR: Unable to create searcher - mally.csv not found in resources folder", err4);

    assertTrue(mockOut.terminal().ready());
    String out5 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out5 += mockOut.terminal().readLine();
    assertEquals(
        "Created searcher for ten-star.csv file without a header row."
            + "Please input your search query's value, or RESET to search a new file ",
        out5);

    assertTrue(mockOut.terminal().ready());
    String out6 = mockOut.terminal().readLine();
    assertEquals("Program exiting", out6);
  }

  /** Test using HELP command. */
  @Test
  public void testHelpCommand() throws IOException {

    mockIn.println("help");
    mockIn.println("exit");

    proc.run();

    assertTrue(mockOut.terminal().ready());
    String welcome = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    welcome += mockOut.terminal().readLine();
    assertEquals(
        "Welcome to the CSV file searcher! Input \"EXIT\" to exit, \"HELP\""
            + " for help, or \"RESET\" to reset.",
        welcome);

    assertTrue(mockOut.terminal().ready());
    String out1 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out1 += mockOut.terminal().readLine();
    assertEquals(
        "Please enter the name of the csv file you'd like to search in, and"
            + " whether or not it has a header row ex. students_data Y",
        out1);

    assertTrue(mockOut.terminal().ready());
    String help =
        mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine()
            + mockOut.terminal().readLine();

    assertEquals(
        " CSV File Searcher ================= Begin by inputting a csv file name"
            + " and Y or N (Y if there's a header row and N if not) Example inputs: stardata Y, "
            + "students Y, malformed_signs N  After the searcher was successfully created, input the "
            + "value you'd like to search for Example inputs: Sarah Parker, Sol, 300  After the value "
            + "is set, please input the type of column identifier you'd like to provide followed "
            + "directly by the identifier, or None This can be the column index (0 for the first "
            + "column, 1 for the next) or the column name, or None Example inputs: I0, I6, "
            + "NStudentIDs, N2020, None  Then the search will be conducted, and you can re-input "
            + "a value and identifier, or input RESET to search in a new file.",
        help);

    assertTrue(mockOut.terminal().ready());
    String out6 = mockOut.terminal().readLine();
    assertEquals("Program exiting", out6);
  }

  /**
   * Test setting the value of a searcher and basic searches, and multiple searches with one
   * searcher (consecutive)
   */
  @Test
  public void testSetValue() throws IOException {
    mockIn.println("students Y");
    mockIn.println("1004");
    mockIn.println("None");
    mockIn.println("Eva Brown");
    mockIn.println("none");
    mockIn.println("exit");

    proc.run();

    assertTrue(mockOut.terminal().ready());
    String welcome = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    welcome += mockOut.terminal().readLine();
    assertEquals(
        "Welcome to the CSV file searcher! Input \"EXIT\" to exit, \"HELP\""
            + " for help, or \"RESET\" to reset.",
        welcome);

    assertTrue(mockOut.terminal().ready());
    String out1 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out1 += mockOut.terminal().readLine();
    assertEquals(
        "Please enter the name of the csv file you'd like to search in, and"
            + " whether or not it has a header row ex. students_data Y",
        out1);

    assertTrue(mockOut.terminal().ready());
    String out2 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out2 += mockOut.terminal().readLine();
    assertEquals(
        "Created searcher for students file with a header row."
            + "Please input your search query's value, or RESET to search a new file ",
        out2);

    assertTrue(mockOut.terminal().ready());
    String out3 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertEquals(
        "Value to search for is set to: 1004"
            + "Please input a column identifier or 'None'."
            + "If the identifier is an index, begin it with I, and if it is a name begin it with N"
            + "ex. I6, NStudentID",
        out3);

    assertTrue(mockOut.terminal().ready());
    String out4 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out4 += mockOut.terminal().readLine();
    assertEquals(
        "1004, David Green, Mathematics"
            + "Please input your search query's value, or RESET to search a new file ",
        out4);

    assertTrue(mockOut.terminal().ready());
    String out = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out += mockOut.terminal().readLine();

    assertEquals(
        "Value to search for is set to: Eva BrownPlease input a column identifier or "
            + "'None'.If the identifier is an index, begin it with I, and if it is a name begin it"
            + " with Nex. I6, NStudentID",
        out);

    assertTrue(mockOut.terminal().ready());
    out = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out += mockOut.terminal().readLine();
    assertEquals(
        "1005, Eva Brown, BiologyPlease input your search query's value, or RESET to "
            + "search a new file ",
        out);

    assertTrue(mockOut.terminal().ready());
    String out6 = mockOut.terminal().readLine();
    assertEquals("Program exiting", out6);
  }

  /** Test conducting searches with colIDs */
  @Test
  public void testSearchWithID() throws IOException {
    mockIn.println("students Y");
    mockIn.println("1004");
    mockIn.println("I7");
    mockIn.println("IA");
    mockIn.println("umm");
    mockIn.println("I-1");
    mockIn.println("I1");
    mockIn.println("exit");
    proc.run();

    assertTrue(mockOut.terminal().ready());
    String welcome = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    welcome += mockOut.terminal().readLine();
    assertEquals(
        "Welcome to the CSV file searcher! Input \"EXIT\" to exit, \"HELP\""
            + " for help, or \"RESET\" to reset.",
        welcome);

    assertTrue(mockOut.terminal().ready());
    String out1 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out1 += mockOut.terminal().readLine();
    assertEquals(
        "Please enter the name of the csv file you'd like to search in, and"
            + " whether or not it has a header row ex. students_data Y",
        out1);

    assertTrue(mockOut.terminal().ready());
    String out2 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out2 += mockOut.terminal().readLine();
    assertEquals(
        "Created searcher for students file with a header row."
            + "Please input your search query's value, or RESET to search a new file ",
        out2);

    assertTrue(mockOut.terminal().ready());
    String out3 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertEquals(
        "Value to search for is set to: 1004"
            + "Please input a column identifier or 'None'."
            + "If the identifier is an index, begin it with I, and if it is a name begin it with N"
            + "ex. I6, NStudentID",
        out3);

    assertTrue(mockErr.terminal().ready());
    String err = mockErr.terminal().readLine();
    assertEquals("ERROR: column index out of range.", err);

    assertTrue(mockErr.terminal().ready());
    err = mockErr.terminal().readLine();
    assertEquals("ERROR: \"I\" should be followed by a number", err);

    assertTrue(mockErr.terminal().ready());
    err = mockErr.terminal().readLine();
    assertEquals("ERROR: column identifier should begin with N or I or be None", err);

    assertTrue(mockErr.terminal().ready());
    err = mockErr.terminal().readLine();
    assertEquals("ERROR: column index cannot be less than zero", err);

    assertTrue(mockOut.terminal().ready());
    String out = mockOut.terminal().readLine();
    assertEquals("No results found", out);

    assertTrue(mockOut.terminal().ready());
    out2 = mockOut.terminal().readLine();
    assertEquals("Please input your search query's value, or RESET to search a new file ", out2);

    assertTrue(mockOut.terminal().ready());
    String out6 = mockOut.terminal().readLine();
    assertEquals("Program exiting", out6);
  }

  /** Test more searches with colNames */
  @Test
  public void testMoreSearchWithID() throws IOException {
    mockIn.println("students Y");
    mockIn.println("1004");
    mockIn.println("Nmaor");
    mockIn.println("NMajor");
    mockIn.println("1004");
    mockIn.println("");
    mockIn.println("Nstudentid");
    mockIn.println("exit");

    proc.run();

    assertTrue(mockOut.terminal().ready());
    String welcome = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    welcome += mockOut.terminal().readLine();
    assertEquals(
        "Welcome to the CSV file searcher! Input \"EXIT\" to exit, \"HELP\""
            + " for help, or \"RESET\" to reset.",
        welcome);

    assertTrue(mockOut.terminal().ready());
    String out1 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out1 += mockOut.terminal().readLine();
    assertEquals(
        "Please enter the name of the csv file you'd like to search in, and"
            + " whether or not it has a header row ex. students_data Y",
        out1);

    assertTrue(mockOut.terminal().ready());
    String out2 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out2 += mockOut.terminal().readLine();
    assertEquals(
        "Created searcher for students file with a header row."
            + "Please input your search query's value, or RESET to search a new file ",
        out2);

    assertTrue(mockOut.terminal().ready());
    String out3 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertEquals(
        "Value to search for is set to: 1004"
            + "Please input a column identifier or 'None'."
            + "If the identifier is an index, begin it with I, and if it is a name begin it with N"
            + "ex. I6, NStudentID",
        out3);

    assertTrue(mockErr.terminal().ready());
    String err = mockErr.terminal().readLine();
    assertEquals(
        "ERROR: java.io.IOException: column name maor not found in dataset "
            + "students.csv. Columns in dataset: StudentID, Name, Major",
        err);

    assertTrue(mockOut.terminal().ready());
    String out4 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out4 += mockOut.terminal().readLine();
    assertEquals(
        "No results foundPlease input your search query's value, or RESET to "
            + "search a new file ",
        out4);

    assertTrue(mockOut.terminal().ready());
    out3 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out3 += mockOut.terminal().readLine();
    assertEquals(
        "Value to search for is set to: 1004"
            + "Please input a column identifier or 'None'."
            + "If the identifier is an index, begin it with I, and if it is a name begin it with N"
            + "ex. I6, NStudentID",
        out3);

    assertTrue(mockErr.terminal().ready());
    err = mockErr.terminal().readLine();
    assertEquals("ERROR: Empty input", err);

    assertTrue(mockOut.terminal().ready());
    out4 = mockOut.terminal().readLine();
    assertTrue(mockOut.terminal().ready());
    out4 += mockOut.terminal().readLine();
    assertEquals(
        "1004, David Green, Mathematics"
            + "Please input your search query's value, or RESET to search a new file ",
        out4);

    assertTrue(mockOut.terminal().ready());
    String out6 = mockOut.terminal().readLine();
    assertEquals("Program exiting", out6);
  }
}
