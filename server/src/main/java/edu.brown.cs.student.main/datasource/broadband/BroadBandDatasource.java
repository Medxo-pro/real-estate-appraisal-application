package edu.brown.cs.student.main.datasource.broadband;

import edu.brown.cs.student.main.datasource.DatasourceException;

/**
 * A BroadBand data source can be used to get the current broadband at a certain location. It is not
 * specified _where_ the data comes from, how old it might be, etc.
 */
public interface BroadBandDatasource {
  /**
   * Retrieve the current broadband for given state and county codes.
   *
   * @param location the names of the state and county to retrieve data for
   * @return the broadband data obtained
   * @throws DatasourceException if there is an issue retrieving data for this state and county
   * @throws IllegalArgumentException if an input given is invalid or not recognized.
   */
  BroadBandData getBroadBand(Location location)
      throws DatasourceException, IllegalArgumentException;
}
