package edu.brown.cs.student.main.parser.classes;

/** Class for star objects. */
public class Star {
  /** The star's ID. */
  private int starId;
  /** The star's name. */
  private String properName;
  /** Array for XYZ coordinates. */
  private double[] coordinates;

  /**
   * Initializes a star object.
   *
   * @param starId - the integer id of the star
   * @param properName - the star's proper name, as a string
   * @param coordinates - the star's coordinates, as an array of doubles
   * @throws IllegalArgumentException if coordinates array contains more than three doubles
   */
  public Star(int starId, String properName, double[] coordinates) throws IllegalArgumentException {
    if (coordinates.length != 3) {
      throw new IllegalArgumentException("Coordinates array must contain exactly 3 elements.");
    }
    this.starId = starId;
    this.properName = properName;
    this.coordinates = coordinates;
  }

  /**
   * Gets the star object's ID.
   *
   * @return the id
   */
  public int getStarId() {
    return starId;
  }

  /**
   * Gets the star object's properName.
   *
   * @return the properName
   */
  public String getProperName() {
    return properName;
  }

  /**
   * Gets the star object's coordinates.
   *
   * @return the coordinates
   */
  public double[] getCoordinates() {
    return coordinates;
  }
}
