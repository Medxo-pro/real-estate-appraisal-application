package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.searcher.Searcher;
import java.io.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SearcherTest {

  private Searcher student_searcher;
  private Searcher star_searcher;
  private Searcher empty_searcher;
  private Searcher income_by_race_searcher;
  private Searcher header_student_searcher;
  private Searcher ten_star_no_header_searcher;

  @BeforeEach
  public void setUp() {
    try {
      student_searcher = new Searcher("students.csv", true);
      star_searcher = new Searcher("stardata", true);
      empty_searcher = new Searcher("empty.csv", false);
      income_by_race_searcher = new Searcher("income_by_race", true);
      header_student_searcher = new Searcher("students.csv", false);
      ten_star_no_header_searcher = new Searcher("ten_star_no_header.csv", false);
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
  }

  /** Tests that the searches returns the single row with the value */
  @Test
  public void testSimpleSearch() {
    // make sure we only get the one column with the value
    List<List<String>> result = student_searcher.search("1005");
    assertEquals(result.size(), 1);
    // make sure the whole row is returned correctly
    assertEquals(result.get(0).get(0), "1005");
    assertEquals(result.get(0).get(1), "Eva Brown");
    assertEquals(result.get(0).get(2), "Biology");
    assertEquals(result.get(0).size(), 3);

    // make sure we're checking the last row
    assertEquals(student_searcher.search("1010").size(), 1);
    // and the first row
    assertEquals(student_searcher.search("1001").size(), 1);

    // and finding things in other columns (not case-sensitive!)
    result = student_searcher.search("Frank adams");
    assertEquals(result.size(), 1);
    assertEquals(result.get(0).get(0), "1006");
    assertEquals(result.get(0).get(1), "Frank Adams");
    assertEquals(result.get(0).get(2), "Economics");

    result = student_searcher.search("History");
    assertEquals(result.size(), 1);
    assertEquals(result.get(0).get(0), "1009");
    assertEquals(result.get(0).get(1), "Ivy Young");
    assertEquals(result.get(0).get(2), "History");

    // last column last row
    assertEquals(student_searcher.search("Philosophy").size(), 1);
    assertEquals(student_searcher.search("PHILOSOPHY").get(0).size(), 3);
  }

  /** Tests searches when given colName */
  @Test
  public void testColNameSearch() {
    try {
      // make sure we're only searching the given column
      assertEquals(student_searcher.search("Jack Wilson", "studentid").size(), 0);
      assertEquals(student_searcher.search("1001", "Major").size(), 0);
      assertEquals(student_searcher.search("1001", "Name").size(), 0);

      // and we are searching the given column
      assertEquals(student_searcher.search("1005", "StudentID").size(), 1);
      assertEquals(student_searcher.search("ECONOMICS", "Major").size(), 1);
      assertEquals(student_searcher.search("Jack Wilson", "name").size(), 1);
      // make sure we're checking the last row
      assertEquals(student_searcher.search("1010", "StudentID").size(), 1);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Test searches when given colIndex */
  @Test
  public void testColIndexSearch() {
    // make sure we're only searching the given column
    assertEquals(student_searcher.search("Jack Wilson", 0).size(), 0);
    assertEquals(student_searcher.search("1001", 2).size(), 0);
    assertEquals(student_searcher.search("1001", 1).size(), 0);

    // and we are searching the given column
    assertEquals(student_searcher.search("1005", 0).size(), 1);
    assertEquals(student_searcher.search("Economics", 2).size(), 1);
    assertEquals(student_searcher.search("JACK WILSON", 1).size(), 1);
    // make sure we're checking the last row
    assertEquals(student_searcher.search("1010", 0).size(), 1);
  }

  /** Test header row being skipped and not skipped */
  @Test
  public void testHeaderSkipping() {
    // make sure the header row is skipped
    assertEquals(student_searcher.search("Major").size(), 0);
    assertEquals(student_searcher.search("StudentID").size(), 0);
    assertEquals(student_searcher.search("Name").size(), 0);

    // and not skipped
    assertEquals(header_student_searcher.search("Major").size(), 1);
    assertEquals(header_student_searcher.search("StudentID").size(), 1);
    assertEquals(header_student_searcher.search("Name").size(), 1);

    // with colIndex given too
    assertEquals(student_searcher.search("Major", 2).size(), 0);
    assertEquals(student_searcher.search("StudentID", 0).size(), 0);
    assertEquals(student_searcher.search("Name", 1).size(), 0);

    try {
      Searcher header_student_searcher = new Searcher("students.csv", false);
      assertEquals(header_student_searcher.search("Major", 2).size(), 1);
      assertEquals(header_student_searcher.search("StudentID", 0).size(), 1);
      assertEquals(header_student_searcher.search("Name", 1).size(), 1);

    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
  }

  /** Test search when the value isn't in the dataset */
  @Test
  public void testValueNotInData() {
    // make sure nothing breaks if the value isn't in the dataset
    assertEquals(student_searcher.search("2000").size(), 0);
    assertEquals(star_searcher.search("your mom").size(), 0);

    assertEquals(student_searcher.search("2000", 0).size(), 0);
    assertEquals(star_searcher.search("your mom", 1).size(), 0);

    try {
      assertEquals(student_searcher.search("2000", "StudentID").size(), 0);
      assertEquals(star_searcher.search("your mom", "ProperName").size(), 0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Test multiple results and large data sets */
  @Test
  public void testMultipleMatches() {
    try {
      List<List<String>> result = income_by_race_searcher.search("Native American");
      assertEquals(result.size(), 21);
      result = income_by_race_searcher.search("Native American", "Race");
      assertEquals(result.size(), 21);
      result = income_by_race_searcher.search("Native American", 1);
      assertEquals(result.size(), 21);

      result = income_by_race_searcher.search("\"Providence County, RI\"");
      assertEquals(result.size(), 78);
      result = income_by_race_searcher.search("\"Providence County, RI\"", "Geography");
      assertEquals(result.size(), 78);
      result = income_by_race_searcher.search("\"Providence County, RI\"", 6);
      assertEquals(result.size(), 78);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Test empty csv file search */
  @Test
  public void testEmptyFile() {
    List<List<String>> result = empty_searcher.search("Native American");
    assertEquals(result.size(), 0);
    result = empty_searcher.search("Native American", 1);
    assertEquals(result.size(), 0);
    try {
      result = empty_searcher.search("Native American", "Race");
      assertEquals(result.size(), 0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Test file not found or file extension not included */
  @Test
  public void testFileNotFound() {
    assertThrows(FileNotFoundException.class, () -> new Searcher("my_file.csv", false));
    assertThrows(FileNotFoundException.class, () -> new Searcher("my_file", false));
    assertThrows(FileNotFoundException.class, () -> new Searcher("studnts", false));
  }

  /** Test null constructor errors */
  @Test
  public void testNullConstructorInputs() {
    assertThrows(IllegalArgumentException.class, () -> new Searcher(null, false));
  }

  /** Test null parameter errors */
  @Test
  public void testNullParamInputs() {
    assertThrows(IllegalArgumentException.class, () -> income_by_race_searcher.search(null));
    assertThrows(IllegalArgumentException.class, () -> student_searcher.search(null, "Name"));
    assertThrows(IllegalArgumentException.class, () -> student_searcher.search("1001", null));
    assertThrows(IllegalArgumentException.class, () -> student_searcher.search(null, 0));
  }

  /** Test column name not found or no header row */
  @Test
  public void testColumnNameIssues() {
    // no headers so col name not found
    assertThrows(IOException.class, () -> ten_star_no_header_searcher.search("", "sol"));
    // headers and col name not found
    assertThrows(IOException.class, () -> student_searcher.search("", ""));
  }

  /** Test column index errors */
  @Test
  public void testColumnIndexIssues() {
    // negative column index
    assertThrows(IllegalArgumentException.class, () -> income_by_race_searcher.search("", -1));
    // col index out of bounds
    assertThrows(IllegalArgumentException.class, () -> header_student_searcher.search("", 19));
  }
}
