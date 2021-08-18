package edu.brown.cs.student.maps;

/**
 * Generic Edge interface.
 *
 * @param <N>
 *   generic node
 * @param <E>
 *   generic edge
 */
public interface Edge<N extends Node<N, E>, E extends Edge<N, E>> extends Comparable<Edge<N, E>> {
  /**
   * Accessor for startNode.
   * @return N
   *      the start Node
   */
  N startNode();

  /**
   * Accessor for endNode.
   * @return N
   *      the end Node
   */
  N endNode();

  /**
   * Accessor for weight.
   * @return double
   *      edge weight
   */
  double getWeight();

  /**
   * Accessor for ID.
   * @return String
   *      the node's ID
   */
  String getID();

  /**
   * Accessor for startNode.
   * @return N
   *      the start Node
   */
  N getStart();

  /**
   * Accessor for endNode.
   * @return N
   *      the end Node
   */
  N getEnd();
}
