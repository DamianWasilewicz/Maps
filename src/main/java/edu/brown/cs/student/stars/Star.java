package edu.brown.cs.student.stars;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class that stores data about a star according to its id.
 */
public class Star implements HasCoordinates {
  private int id;
  private String name;
  private double x;
  private double y;
  private double z;
  private List<Double> coords = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param idint
   *      Star's id
   * @param namestring
   *      Star's name
   * @param xval
   *      Star's X
   * @param yval
   *      Star's Y
   * @param zval
   *      Star's Z
   */
  public Star(int idint, String namestring, double xval, double yval, double zval) {
    id = idint;
    name = namestring;
    x = xval;
    y = yval;
    z = zval;
    coords.add(x);
    coords.add(y);
    coords.add(z);
  }

  /**
   * Gets the id of a star object.
   *
   * @return
   *      id
   */
  public int getID() {
    return id;
  }

  /**
   * gets the name of a star object.
   *
   * @return
   *      name
   */
  public String getName() {
    return name;
  }

  /**
   * gets the x val of a star object.
   *
   * @return
   *      x val
   */
  public double getX() {
    return x;
  }

  /**
   * gets the y val of a star object.
   *
   * @return
   *      y val
   */
  public double getY() {
    return y;
  }

  /**
   * gets the z val of a star object.
   *
   * @return
   *      z val
   */
  public double getZ() {
    return z;
  }

  /**
   * get the value of a star coordinate at a given axis.
   *
   * @param i
   *       index of coord to find
   * @return
   *       double val of coord at dimension i
   */
  public Double getCoordinate(int i) {
    return coords.get(i);
  }

  /**
   * returns the number of dimensions a star coordinate has.
   *
   * @return
   *      num of coords
   */
  public int numDimensions() {
    return coords.size();
  }

  /**
   * Distance from star to another star.
   *
   * @param other
   *        other star to find distance to
   * @return
   *        the double distance between a star and another star.
   */
  public double euclideanDistance(Star other) {
    double total = 0.0;
    for (int i = 0; i < numDimensions(); i++) {
      total = total
          + Math.pow(this.getCoordinate(i) - other.getCoordinate(i), 2);
    }
    return Math.sqrt(total);
  }


}
