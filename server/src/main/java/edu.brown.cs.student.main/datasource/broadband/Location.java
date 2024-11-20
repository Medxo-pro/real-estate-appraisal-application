package edu.brown.cs.student.main.datasource.broadband;

/**
 * A record to hold a specific location.
 *
 * @param state the state
 * @param county the county
 */
public record Location(String state, String county) {
  /** Check input-validity by using a custom constructor. */
  public Location {
    if (state == null | county == null) {
      throw new IllegalArgumentException();
    }
  }
}
