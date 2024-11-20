package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs.student.main.datasource.DatasourceException;
import edu.brown.cs.student.main.datasource.broadband.ACSAPIBroadBandDatasource;
import edu.brown.cs.student.main.datasource.broadband.BroadBandData;
import edu.brown.cs.student.main.datasource.broadband.BroadBandDatasource;
import edu.brown.cs.student.main.datasource.broadband.Location;
import org.junit.jupiter.api.Test;

/** Class containing tests for ACSAPIBroadBandDataSourceTest */
public class ACSAPIBroadBandDataSourceTest {

  /** Tests an api request to real datasource */
  @Test
  public void testBroadBandCanLoad_REAL() throws DatasourceException {
    BroadBandDatasource source = new ACSAPIBroadBandDatasource();
    Location loc = new Location("Florida", "Broward County");
    BroadBandData res = source.getBroadBand(loc);

    assertNotNull(res);

    assertTrue(Double.parseDouble(res.broadBand()) > 0);
  }

  /** Tests an api request to real datasource with a non-existent county as parameter */
  @Test
  public void testCountyNotFound_REAL() {
    BroadBandDatasource source = new ACSAPIBroadBandDatasource();
    Location loc = new Location("Florida", "John County");
    assertThrows(IllegalArgumentException.class, () -> source.getBroadBand(loc));
  }

  /** Tests an api request to real datasource with a non-existent state as parameter */
  @Test
  public void testStateNotFound_REAL() {
    BroadBandDatasource source = new ACSAPIBroadBandDatasource();
    Location loc = new Location("Flo", "Broward County");
    assertThrows(IllegalArgumentException.class, () -> source.getBroadBand(loc));
  }
}
