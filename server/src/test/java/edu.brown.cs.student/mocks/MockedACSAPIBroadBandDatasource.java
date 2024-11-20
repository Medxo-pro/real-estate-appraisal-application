package edu.brown.cs.student.mocks;

import edu.brown.cs.student.main.datasource.broadband.BroadBandData;
import edu.brown.cs.student.main.datasource.broadband.BroadBandDatasource;
import edu.brown.cs.student.main.datasource.broadband.Location;

/**
 * A datasource that never actually calls the ACS API, but always returns a constant broadband-data
 * value.
 */
public class MockedACSAPIBroadBandDatasource implements BroadBandDatasource {
  /** The constant data to return instead of calling the API */
  private final BroadBandData constantData;

  public MockedACSAPIBroadBandDatasource(BroadBandData constantData) {
    this.constantData = constantData;
  }

  /**
   * Retrieve the current broadband for given state and county codes.
   *
   * @param location the names of the state and county to retrieve data for
   * @return the broadband data obtained
   * @throws IllegalArgumentException if a code given is invalid
   */
  @Override
  public BroadBandData getBroadBand(Location location) {
    return constantData;
  }
}
