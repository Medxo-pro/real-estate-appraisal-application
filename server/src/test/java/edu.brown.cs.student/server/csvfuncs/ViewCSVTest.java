package edu.brown.cs.student.server.csvfuncs;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.parser.classes.FactoryFailureException;
import edu.brown.cs.student.main.server.csvfuncs.ViewCSV;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Class containing tests for the ViewCSV class */
public class ViewCSVTest {
  /** The viewcsv object we'll use to test */
  private ViewCSV viewCSV;

  /** Set up by creating a viewcsv file */
  @BeforeEach
  public void setUp() {
    viewCSV = new ViewCSV();
  }

  /** Tests that viewCSV returns the expected json version of our csv file */
  @Test
  public void testViewCSV_ValidFile() {
    try {
      // Try using viewCSV on a basic case
      String expectedJson =
          "[[\"StarID\",\"ProperName\",\"X\",\"Y\",\"Z\"],"
              + "[\"0\",\"Sol\",\"0\",\"0\",\"0\"],[\"1\",\"\",\"282.43485\",\"0.00449\",\"5.36884\"],"
              + "[\"2\",\"\",\"43.04329\",\"0.00285\",\"-15.24144\"],"
              + "[\"3\",\"\",\"277.11358\",\"0.02422\",\"223.27753\"],"
              + "[\"3759\",\"96 G. Psc\",\"7.26388\",\"1.55643\",\"0.68697\"],"
              + "[\"70667\",\"Proxima Centauri\",\"-0.47175\",\"-0.36132\",\"-1.15037\"],"
              + "[\"71454\",\"Rigel Kentaurus B\",\"-0.50359\",\"-0.42128\",\"-1.1767\"],"
              + "[\"71457\",\"Rigel Kentaurus A\",\"-0.50362\",\"-0.42139\",\"-1.17665\"],"
              + "[\"87666\",\"Barnard's Star\",\"-0.01729\",\"-1.81533\",\"0.14824\"],"
              + "[\"118721\",\"\",\"-2.28262\",\"0.64697\",\"0.29354\"]]";
      String actualJson = viewCSV.viewCSV("data/csv/stars/ten-star.csv");

      // Verify the output matches the expected JSON
      assertEquals(expectedJson, actualJson);
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Tests that viewCSV doesn't throw an exception for inconsistent number of columns or empty file
   */
  @Test
  public void testViewCSV_InconsistentColumns() {
    try {
      viewCSV.viewCSV("data/csv/malformed/malformed_signs.csv");
      viewCSV.viewCSV("data/csv/malformed/empty.csv");
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
  }
}
