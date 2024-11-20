package edu.brown.cs.student.server.csvfuncs;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.csvfuncs.LoadCSV;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Class containing tests for the LoadCSV class */
public class LoadCSVTest {

  /** The loadcsv object we'll use to test */
  private LoadCSV loadCSV;

  /** Sets up by creating the loadcsv object */
  @BeforeEach
  public void setUp() {
    loadCSV = new LoadCSV();
  }

  /** Test that LoadCSV doesn't throw errors on good files */
  @Test
  public void testLoadCSV_Success() throws Exception {
    assertDoesNotThrow(() -> loadCSV.loadCSV("data/resources/empty.csv"));
    assertEquals("empty", loadCSV.loadCSV("data/resources/empty.csv"));
    assertDoesNotThrow(() -> loadCSV.loadCSV("data/resources/students.csv"));
    assertEquals("students", loadCSV.loadCSV("data/resources/students.csv"));
    assertDoesNotThrow(() -> loadCSV.loadCSV("data/resources/ten-star.csv"));
    assertEquals("ten-star", loadCSV.loadCSV("data/resources/ten-star.csv"));
  }

  /** Test that LoadCSV does throw errors when files are in the wrong location */
  @Test
  public void testLoadCSV_FileNotInDirectory() {
    // Provide loadcsv with a filepath outside of resources
    Exception exception =
        assertThrows(
            FileNotFoundException.class,
            () -> {
              loadCSV.loadCSV("data/csv/census/postsecondary_education.csv");
            });
    Exception exception2 =
        assertThrows(
            FileNotFoundException.class,
            () -> {
              loadCSV.loadCSV(
                  "src/test/java/edu/brown/cs/student/server/csvfuncs/" + "SearchCSVTest.java");
            });

    assertEquals("File not in resources folder", exception.getMessage());
    assertEquals("File not in resources folder", exception2.getMessage());
  }

  /** Test when the file doesn't exist */
  @Test
  public void testLoadCSV_FileNotFound() {
    String nonExistentFilePath = "data/resources/non_existent_file.csv";

    Exception exception =
        assertThrows(
            FileNotFoundException.class,
            () -> {
              loadCSV.loadCSV(nonExistentFilePath);
            });

    assertEquals("File not found at the specified path", exception.getMessage());
  }
}
