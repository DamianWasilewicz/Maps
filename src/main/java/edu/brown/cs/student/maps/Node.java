package edu.brown.cs.student.maps;

import java.util.Collection;

/**
 * Generic Node interface.
 *
 * @param <N>
 *   generic node
 * @param <E>
 *   generic edge
 */
public interface Node<N extends Node<N, E>, E extends Edge<N, E>> {

  /**
   * Accessor for outgoing edges.
   *
   * @return
   *      list of outgoing edges
   */
  Collection<E> getOutEdges();

  /**
   * Accessor for node's cost.
   *
   * @return
   *      node's cost
   */
  double getCost();

  /**
   * Mutator for node's cost.
   *
   * @param cost
   *      node's new cost
   */
  void setCost(double cost);

  /**
   * Accessor for node's id.
   *
   * @return
   *      node's id
   */
  String getId();

  /**
   * Accessor for node's cheapest incoming edge.
   *
   * @return
   *      node's cheapest incoming edge
   */
  E getPrev();

  /**
   * Calculates haversine distance.
   *
   * @param n
   *      node finding distance to
   * @return
   *      node's distance to n
   */
  double haversine(N n);

  /**
   * Mutator for node's cheapest incoming edge.
   *
   * @param newEdge
   *      node's new cheapest incoming edge.
   */
  void setPrev(E newEdge);
}
