package edu.brown.cs.student.stars;

/**
 * This interfaces defines a generic type that the KDTree takes.
 */
public interface HasCoordinates {
  /**
   * Gets the coordinate at index i.
   *
   *  @param i
   *      index of coord
   * @return
   *      double value of coord
   */
  Double getCoordinate(int i);

  /**
   * returns the num,ner of dimension a coordinate object has.
   * @return
   *      number of dimensions
   */
  int numDimensions();

  /**
   * accessor for name.
   *
   *  @return
   *      name
   */
  String getName();
}
