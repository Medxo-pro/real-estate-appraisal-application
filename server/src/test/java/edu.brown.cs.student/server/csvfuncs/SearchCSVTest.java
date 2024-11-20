package edu.brown.cs.student.server.csvfuncs;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.server.csvfuncs.SearchCSV;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchCSVTest {
  private SearchCSV searchCSV;

  @BeforeEach
  void setUp() throws IOException {
    searchCSV = new SearchCSV();
  }

  /** Is given a key and a valid file path */
  @Test
  void testValidSimpleSearchCSV() {
    String jsonResult = null;
    try {
      jsonResult = searchCSV.searchCSV("resources/ten-star.csv", true, "Sol", null);
      assertTrue(jsonResult.contains("Sol"));
      assertTrue(jsonResult.contains("0"));

    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
  }

  /** Test that it searches in the correct column when given an integer column name */
  @Test
  void testSearchCSVWithIntColName() {
    try {
      String jsonResult =
          searchCSV.searchCSV("data/resources/headers_are_nums.csv", true, "sad", "2021");
      assertNotNull(jsonResult);
      assertTrue(jsonResult.contains("happy"));
      assertTrue(jsonResult.contains("sad"));
      assertTrue(jsonResult.contains("boring"));
      assertFalse(jsonResult.contains("womp"));
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
  }

  /** Test it with a column number given */
  @Test
  void testSearchCSVColNum() {
    try {
      String jsonResult = searchCSV.searchCSV("resources/students.csv", true, "Frank Adams", "1");
      assertNotNull(jsonResult);
      assertTrue(jsonResult.contains("Frank Adams"));
      assertTrue(jsonResult.contains("Economics"));
      assertTrue(jsonResult.contains("1006"));
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
  }

  /** Test it with a column name given */
  @Test
  void testSearchCSVColName() {
    try {
      String jsonResult =
          searchCSV.searchCSV("data/csv/students/students.csv", true, "Frank Adams", "Name");
      assertNotNull(jsonResult);
      assertTrue(jsonResult.contains("Frank Adams"));
      assertTrue(jsonResult.contains("Economics"));
      assertTrue(jsonResult.contains("1006"));
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
  }
}
