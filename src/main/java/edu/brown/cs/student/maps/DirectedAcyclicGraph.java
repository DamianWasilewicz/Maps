package edu.brown.cs.student.maps;


import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

/**
 * This class represents a Directed Acyclic Graph.
 *
 * @param <N> : Node
 * @param <E> : Edge
 */
public class DirectedAcyclicGraph<N extends Node<N, E>, E extends Edge<N, E>> {

  public DirectedAcyclicGraph() {
  }

  /**
   * Uses A* algorithm to return the list of edges.
   *
   * @param src  : src
   * @param dest : dest
   * @return shortest path from src to dest
   */
  public List<E> getAStarPath(N src, N dest) {

    List<E> shortestPath = new ArrayList<>();

    if (src == null || dest == null) {
      return shortestPath;
    }

    HashMap<String, Double> distances = new HashMap<>();
    HashMap<N, E> prev = new HashMap<>();

    distances.put(src.getId(), 0.0);

    //PQ that updates when you add to it
    PriorityQueue<N> queue = new PriorityQueue<>(1, new Comparator<N>() {

      @Override
      public int compare(N o1, N o2) {
        if (distances.get(o1.getId()) < distances.get(o2.getId())) {
          return -1;
        } else if (distances.get(o1.getId()) > distances.get(o2.getId())) {
          return 1;
        } else {
          return 0;
        }
      }
    });

    queue.add(src);
    N closest = null;


    while (!queue.isEmpty()) {

      //gets node with lowest priority
      closest = queue.poll();

      if (closest.getId().compareTo(dest.getId()) == 0) {
        break;
      }

      //each node knows about its outgoing edges
      Collection<E> outgoingEdges = closest.getOutEdges();

      for (E edge : outgoingEdges) {

        N neighbor = edge.getEnd();
        if (!distances.containsKey(neighbor.getId())) {
          distances.put(neighbor.getId(), Double.MAX_VALUE);
          queue.add(neighbor);
        }

        //this is where we combine greedy BFS and djisktra to produce A*
        double distBetween = distances.get(closest.getId())
            + edge.getWeight() + neighbor.haversine(dest);

        if (distBetween < distances.get(neighbor.getId())) {

          distances.put(neighbor.getId(), distBetween);

          //ensures distances are not getting updated in PQ
          if (queue.contains(neighbor)) {
            queue.remove(neighbor);
            queue.add(neighbor);
          }

          prev.put(neighbor, edge);
        }
      }
    }

    E previousEdge = prev.get(closest);

    while (previousEdge != null) {
      shortestPath.add(previousEdge);
      previousEdge = prev.get(previousEdge.getStart());
    }

//    Collections.reverse(shortestPath);
    return shortestPath;

  }

  /**
   * Uses A* algorithm to return the list of edges.
   *
   * @param src  : src
   * @param dest : dest
   * @return shortest path from src to dest
   */
  public List<E> getDijkstraPath(N src, N dest) {

    List<E> shortestPath = new ArrayList<>();

    if (src == null || dest == null) {
      return shortestPath;
    }

    HashMap<String, Double> distances = new HashMap<>();
    HashMap<N, E> prev = new HashMap<>();

    distances.put(src.getId(), 0.0);

    //PQ that updates when you add to it
    PriorityQueue<N> queue = new PriorityQueue<>(1, new Comparator<N>() {

      @Override
      public int compare(N o1, N o2) {
        if (distances.get(o1.getId()) < distances.get(o2.getId())) {
          return -1;
        } else if (distances.get(o1.getId()) > distances.get(o2.getId())) {
          return 1;
        } else {
          return 0;
        }
      }
    });

    queue.add(src);
    N closest = null;


    while (!queue.isEmpty()) {

      //gets node with lowest priority
      closest = queue.poll();

      if (closest.getId().compareTo(dest.getId()) == 0) {
        break;
      }

      //each node knows about its outgoing edges
      Collection<E> outgoingEdges = closest.getOutEdges();

      for (E edge : outgoingEdges) {

        N neighbor = edge.getEnd();
        if (!distances.containsKey(neighbor.getId())) {
          distances.put(neighbor.getId(), Double.MAX_VALUE);
          queue.add(neighbor);
        }

        //this is where we combine greedy BFS and djisktra to produce A*
        double distBetween = distances.get(closest.getId())
            + edge.getWeight() + neighbor.haversine(dest);

        if (distBetween < distances.get(neighbor.getId())) {

          distances.put(neighbor.getId(), distBetween);

          //ensures distances are not getting updated in PQ
          if (queue.contains(neighbor)) {
            queue.remove(neighbor);
            queue.add(neighbor);
          }

          prev.put(neighbor, edge);
        }
      }
    }

    E previousEdge = prev.get(closest);

    while (previousEdge != null) {
      shortestPath.add(previousEdge);
      previousEdge = prev.get(previousEdge.getStart());
    }

    Collections.reverse(shortestPath);
    return shortestPath;

  }

}
