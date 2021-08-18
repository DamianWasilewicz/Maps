package edu.brown.cs.student.stars;

import java.util.Comparator;

/**
 * This class compares between the elements of two KDTreeNodes elements that extend
 * HasCoordinates.
 *
 * @param <T>
 *      Takes in a generic T that extends the HasCoordinates interface and implements
 *      a Comparator.
 */
public class HasCoordinatesComparator<T extends HasCoordinates> implements Comparator<T> {
  private final int dim;

  /**
   * Constructor.
   *
   * @param dimensionIndex the number of dimension an object of this type has.
   */
  public HasCoordinatesComparator(int dimensionIndex) {
    dim = dimensionIndex;
  }
  /**
   * Comparator compares between double values at a given dimension.
   *
   * @param a one comparison object
   * @param b second comparison object
   * @return a value indicator for comparison.
   */
  @Override
  public int compare(T a, T b) {
    if (a.getCoordinate(dim) < b.getCoordinate(dim)) {
      return -1;
    } else if (a.getCoordinate(dim) > b.getCoordinate(dim)) {
      return 1;
    } else {
      return 0;
    }
  }
}

