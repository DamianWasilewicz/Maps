package edu.brown.cs.student.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Graph class (abstracted using explicit generics).
 *
 * @param <N> Node with getOutEdges() method that returns a collection of E
 * @param <E> Edge with startNode(), endNode(), and getWeight()
 */
public class Graph<N extends Node<N, E>, E extends Edge<N, E>> {
  private HashMap<N, Boolean> visited;
  private HashMap<N, Double> costMap;


  /**
   * Constructor for Graph class.
   * Instantiates new hashmap that maps nodes to their visited status.
   */
  public Graph() {
    visited = new HashMap<>();
    costMap = new HashMap<>();
  }

  /**
   * Dijkstra's algorithm, modified to find the shortest path between two nodes.
   *
   * @param src    the starting node
   * @param target the node to find a path to
   * @return list of edges from src to target
   */
  public List<E> getDijkstraPath(N src, N target) {
    // initialize path to be returned
    List<E> path = new ArrayList<>();
    // check inputs
    if (src == null) {
      System.out.println("ERROR: src is null for dijkstra's.");
      return path;
    }
    if (target == null) {
      System.out.println("ERROR: target is null for dijkstra's.");
      return path;
    }
    // setup priority queue and add the src node
    PriorityQueue<N> pq = new PriorityQueue<>(1, new Comparator<N>() {
      @Override
      public int compare(N o1, N o2) {
        return Double.compare(o1.getCost(), o2.getCost());
      }
    });
    src.setCost(0.0);//reset cost
    pq.add(src);

    //beginning dijkstra search and building the graph
    while (!pq.isEmpty()) {
      // popping a node off the next node off the priority queue
      N curr = pq.remove();
      visited.put(curr, true);

      // base case: if target is popped, the shortest path has been found
      if (curr.getId().equals(target.getId())) {
        break;
      }

      // for each outgoing edge, calculate the cost of the node on the other side
      Collection<E> outgoingEdges = null;
      try {
        outgoingEdges = curr.getOutEdges();
      } catch (Exception e) {
        e.printStackTrace();
      }
      for (E edge : outgoingEdges) {
        N opp = edge.endNode();
        //update opp cost and prev pointers if a cheaper path has been calculated
        double newCost = curr.getCost() + edge.getWeight();
        if ( newCost < opp.getCost()) {
          // if the node is in the priority queue already, add updated version
          if (pq.contains(opp)) {
            pq.remove(opp);
            opp.setCost(newCost);
            opp.setPrev(edge);
            pq.add(opp);
          } else {
            opp.setCost(newCost);
            opp.setPrev(edge);
            if (!visited.containsKey(opp)) {
              pq.add(opp);
            }
          }
        }
      }
    }

    // starting at the target node, backtrack through each node's cheapest edge and add to list
    N start = target;
    while (start.getPrev() != null) {
      E previous = start.getPrev();
      path.add(previous);
      start = previous.getStart();
    }
    return path;
  }

  public List<E> getAlternateAStarPath(N src, N dest) {

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
   * Dijkstra's algorithm, modified to find the shortest path between two nodes,
   * extra modified to account for the distance from the opposite node to the target.
   *
   * @param src    the starting node
   * @param target the node to find a path to
   * @return list of edges from src to target
   */
  public List<E> getAStarPath(N src, N target) {
    // initialize path to be returned
    List<E> path = new ArrayList<>();

    // check inputs
    if (src == null) {
      System.out.println("ERROR: src is null for dijkstra's.");
      return path;
    }
    if (target == null) {
      System.out.println("ERROR: target is null for dijkstra's.");
      return path;
    }

    // setup priority queue and add the src node
    PriorityQueue<N> pq = new PriorityQueue<>(1, new Comparator<N>() {
      @Override
      public int compare(N o1, N o2) {
        if (o1.getCost() < o2.getCost()) {
          return -1;
        } else if (o1.getCost() > o2.getCost()) {
          return 1;
        } else {
          return 0;
        }
      }
    });
    //initialize the cost to avoid negative values when adding the heuristic
    src.setCost(src.haversine(target));
    pq.add(src);

    while (!pq.isEmpty()) {
      // popping a node off the next node off the priority queue
      N curr = pq.remove();
      visited.put(curr, true);

      // if the node popped is the target, the shortest path to this target has found
      if (curr.getId().equals(target.getId())) {
        break;
      }

      // for each outgoing edge, calculate the cost of the node on the other side
      Collection<E> outgoingEdges = null;
      try {
        outgoingEdges = curr.getOutEdges();
      } catch (Exception e) {
        e.printStackTrace();
      }
      for (E edge : outgoingEdges) {
        N opp = edge.endNode();
        //update opp cost and prev pointers if a cheaper path has been calculated
        if ((curr.getCost() + edge.getWeight() + opp.haversine(target)) < opp.getCost()) {
          //update opp cost and prev pointers if a cheaper path has been calculated
          if (pq.contains(opp)) {
            //NOTE: remember to override the equals method to ensure your pq is using object specific equals
            pq.remove(opp);
            opp.setCost(curr.getCost() + edge.getWeight() + opp.haversine(target) - curr.haversine(target));
            opp.setPrev(edge);
            pq.add(opp);
          } else {
            opp.setCost(curr.getCost() + edge.getWeight() + opp.haversine(target) - curr.haversine(target));
            opp.setPrev(edge);
            // add node to pq if it's not already in the node
            if (!visited.containsKey(opp)) {
              pq.add(opp);
            }
          }
        }
      }
    }
    // starting at the target node, backtrack through each node's cheapest edge and add to list
    N start = target;
    while (start.getPrev() != null) {
      E previous = start.getPrev();
      path.add(previous);
      start = previous.getStart();
    }
    return path;
  }
}

//
//  public List<E> aStar1(N src, N target) {
//
//    // initialize path to be returned
//    List<E> path = new ArrayList<>();
//
//    // check inputs
//    if (src == null) {
//      System.out.println("ERROR: src is null for dijkstra's.");
//      return path;
//    }
//    if (target == null) {
//      System.out.println("ERROR: target is null for dijkstra's.");
//      return path;
//    }
//
//    // setup priority queue and add the src node
//    PriorityQueue<N> pq = new PriorityQueue<>(10, new Comparator<N>() {
//      @Override
//      public int compare(N o1, N o2) {
//        if (costMap.get(o1) < costMap.get(o2)) {
//          return -1;
//        } else if (costMap.get(o1) > costMap.get(o2)) {
//          return 1;
//        } else {
//          return 0;
//        }
//      }
//    });
////    List<N> q = new ArrayList<>();
////    q.sort(new Comparator<N>() {
////      @Override
////      public int compare(N o1, N o2) {
////        if (costMap.get(o1) < costMap.get(o2)) {
////        return -1;
////      } else if (costMap.get(o1) > costMap.get(o2)) {
////        return 1;
////      } else {
////        return 0;
////      }
////      }
////    });
////    q.add(src);
//    pq.add(src);
//    costMap.put(src, 0.0);
//    //while the priority queue is not empty, meaning target hasn't been reached
//    while (!pq.isEmpty()) {
//      // popping a node off the next node off the priority queue
//      N curr = pq.remove();
//      visited.put(curr, true);//mark visited
//      System.out.println("CURR" + curr.getId()); //NNNNNNN
//
//      // base case: break from loop if target was found
//      if (curr.getId().equals(target.getId())) {
//        break;
//      }
//
//      // retrieve the least of outgoing edges of the curr node
//      Collection<E> outgoingEdges = null;
//      try {
//        outgoingEdges = curr.getOutEdges();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      System.out.println("size of outgoingedges set is " + outgoingEdges.size()); //NNNNNNN
//      for (E edge : outgoingEdges) {
//        N opp = edge.endNode();
//        System.out.println(
//            "node is " + opp.getId() + " and visited status is " + visited.get(opp)); //NNNNNNN
//        costMap.put(opp, Double.POSITIVE_INFINITY);
//        //update opp cost and prev pointers if a cheaper path has been calculated
//        Double newCost = costMap.get(curr) + edge.getWeight() ; // NNNNNNN
//        if (newCost < opp.getCost()) { //NNNNNNNNNN
////          if ((curr.getCost() + edge.getWeight() + opp.haversine(target)) < opp.getCost()) {
//          //update opp cost and prev pointers if a cheaper path has been calculated
//          if (pq.contains(opp)) {
//            pq.remove(opp);
//            costMap.put(opp, newCost);//NNNNNNNNNNNNNNNN
//            opp.setCost(curr.getCost() + edge.getWeight());
////            System.out.println("IN PQ opp cost is " + opp.getCost() + " and the cost map is " + costMap.get(opp)); //NNNNNNN
//            System.out.println("hello is this happening?"); //NNNNNNN
//
//            opp.setPrev(edge);
//            pq.add(opp);
//          } else {
//            costMap.put(opp, newCost);//NNNNNNNNNNNNNNNN
//            opp.setCost(curr.getCost() + edge.getWeight());
//            System.out.println("NOT IN PQ YET"); //NNNNNNN
//            opp.setPrev(edge);
//            // add node to pq if it's not already in the node
//            if (!visited.containsKey(opp)) {
//            pq.add(opp);
//            System.out.println("adding " + opp.getId() + " to pq "); //NNNNNNN
//            }
//          }
//        }
//      }
//    }
//
//    // starting at the target node, backtrack through each node's cheapest edge and add to list
//    N start = target;
//    while (start.getPrev() != null) {
//      E previous = start.getPrev();
//      path.add(previous);
//      start = previous.getStart();
//    }
//
//    return path;
//  }
//
//
