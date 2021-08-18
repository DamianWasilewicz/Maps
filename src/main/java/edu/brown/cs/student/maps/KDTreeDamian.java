//package edu.brown.cs.student.maps;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
///**
// * Implementation of a K-dimensional Tree. Works with any object
// * that implements Node. Implements nearest neighbor search and radius
// * search.
// */
//public class KDTree {
//  private Node root;
//  private int k;
//  private static final double RANDOM_CHANCE = 0.50;
//
//  /**
//   * Constructor for the KDTree. Takes in a list of nodes
//   * and a value k representing how many dimensions each node
//   * should have.
//   * @param nodes List of nodes.
//   * @param kVal number of dimensions.
//   * @param <T> Generic, must implement Node interface.
//   */
//  public <T extends Node> KDTree(List<T> nodes, int kVal) {
//    Collections.sort(nodes, new NodeComparator(0));
//    k = kVal;
//    root = nodes.get(nodes.size() / 2);
//    root.setParent(null);
//    root.setDepth(0);
//    root.setLeftChild(buildTree(new ArrayList<Node>(nodes.subList(0, nodes.size() / 2)), root));
//    root.setRightChild(buildTree(new ArrayList<Node>(nodes.subList(
//            (nodes.size() / 2) + 1, nodes.size())), root));
//  }
//
//  /**
//   * Helper function for calculating the distance between
//   * two Nodes, as a function of their coordinates arrays.
//   * @param a first node.
//   * @param b second node.
//   * @return double distance
//   */
//  private double distance(ArrayList<Double> a, ArrayList<Double> b) {
//    double distance = 0;
//    for (int i = 0; i < a.size(); i++) {
//      distance += Math.pow(a.get(i) - b.get(i), 2);
//    }
//    return Math.sqrt(distance);
//  }
//
//  /**
//   * Function for constructing the tree, splitting on
//   * the dimension (current depth modulo k).
//   * @param nodes Current slice of nodes to build from.
//   * @param parent The parent node
//   * @return recently added node.
//   */
//  private Node buildTree(List<Node> nodes, Node parent) {
//    if (nodes.size() == 0) {
//      return null;
//    }
//    if (nodes.size() == 1) {
//      Node median = nodes.get(0);
//      median.setParent(parent);
//      median.setDepth(parent.getDepth() + 1);
//      median.setLeftChild(null);
//      median.setRightChild(null);
//    }
//    Collections.sort(nodes, new NodeComparator((parent.getDepth() + 1) % k));
//    Node median = nodes.get(nodes.size() / 2);
//    median.setParent(parent);
//    median.setDepth(parent.getDepth() + 1);
//    median.setLeftChild(buildTree(nodes.subList(0, nodes.size() / 2), median));
//    median.setRightChild(buildTree(nodes.subList((nodes.size() / 2)  + 1, nodes.size()), median));
//    return median;
//  }
//
//  /**
//   * Function that returns the numNeighbors nearest nodes
//   * to the inputted coordinates.
//   * @param coords Basis of closest neighbors.
//   * @param numNeighbors Number of neighbors.
//   * @return List of nearest neighbors.
//   */
//  public ArrayList<Node> kNearestNeighbors(ArrayList<Double> coords, int numNeighbors) {
//    ArrayList<Node> nearestNeighbors = new ArrayList<Node>();
//    searchNeighbors(root, coords, nearestNeighbors, numNeighbors);
//    return nearestNeighbors;
//  }
//
//  /**
//   * Recursive helper function for implementing nearest neighbors search.
//   * @param currNode Current node.
//   * @param targetCoords Basis for search.
//   * @param nearestNeighbors List of nearest neighbors so far.
//   * @param numNeighbors Number of neighbors to return.
//   */
//  private void searchNeighbors(Node currNode, ArrayList<Double> targetCoords,
//                               ArrayList<Node> nearestNeighbors, int numNeighbors) {
//    if (currNode == null) {
//      return;
//    }
//
//    if (nearestNeighbors.size() < numNeighbors) {
//      nearestNeighbors.add(currNode);
//    } else if (distance(currNode.getCoords(), targetCoords) < distance(targetCoords,
//            nearestNeighbors.get(nearestNeighbors.size() - 1).getCoords())) {
//
//      nearestNeighbors.remove(nearestNeighbors.size() - 1);
//      nearestNeighbors.add(currNode);
//    } else if (distance(currNode.getCoords(), targetCoords) == distance(targetCoords,
//            nearestNeighbors.get(nearestNeighbors.size() - 1).getCoords())) {
//      if (Math.random() < RANDOM_CHANCE) {
//        nearestNeighbors.remove(nearestNeighbors.size() - 1);
//        nearestNeighbors.add(currNode);
//      }
//    }
//    Collections.sort(nearestNeighbors, new NodeDistanceComparator(targetCoords));
//
//    double maxDistance = distance(targetCoords,
//            nearestNeighbors.get(nearestNeighbors.size() - 1).getCoords());
//
//    int coord = currNode.getDepth() % k;
//
//    if (maxDistance >= Math.abs(targetCoords.get(coord) - currNode.getCoord(coord))) {
//      searchNeighbors(currNode.getLeftChild(), targetCoords, nearestNeighbors, numNeighbors);
//      searchNeighbors(currNode.getRightChild(), targetCoords, nearestNeighbors, numNeighbors);
//    } else if (currNode.getCoord(coord) < targetCoords.get(coord)) {
//      searchNeighbors(currNode.getRightChild(), targetCoords, nearestNeighbors, numNeighbors);
//    } else if (currNode.getCoord(coord) > targetCoords.get(coord)) {
//      searchNeighbors(currNode.getLeftChild(), targetCoords, nearestNeighbors, numNeighbors);
//    } else {
//      return;
//    }
//  }
//
//  /**
//   * Implementation of radius search on nodes within inputted radius.
//   * @param radius Radius to search within.
//   * @param coords Basis for search.
//   * @return List of nodes within radius of inputted coords.
//   */
//  public ArrayList<Node> radius(double radius, ArrayList<Double> coords) {
//    ArrayList<Node> nodesWithinReach = new ArrayList<Node>();
//    searchRadius(root, coords, nodesWithinReach, radius);
//    Collections.sort(nodesWithinReach, new NodeDistanceComparator(coords));
//    return nodesWithinReach;
//  }
//
//  /**
//   * Recursive helper function for implementing radius search.
//   * @param currNode Current node.
//   * @param targetCoords Basis for search.
//   * @param nodesWithinR Nodes within radius so far.
//   * @param radius Radius to search within.
//   */
//  private void searchRadius(Node currNode, ArrayList<Double> targetCoords,
//                            ArrayList<Node> nodesWithinR, double radius) {
//    if (currNode == null) {
//      return;
//    }
//
//    if (distance(currNode.getCoords(), targetCoords) <= radius) {
//      nodesWithinR.add(currNode);
//    }
//
//    int coordOfInterest = currNode.getDepth() % k;
//    double currCoord = currNode.getCoord(coordOfInterest);
//    double targetMinusR = targetCoords.get(coordOfInterest) - radius;
//    double targetPlusR = targetCoords.get(coordOfInterest) + radius;
//
//    if ((targetMinusR < currCoord) && (currCoord < targetPlusR)) {
//      searchRadius(currNode.getLeftChild(), targetCoords, nodesWithinR, radius);
//      searchRadius(currNode.getRightChild(), targetCoords, nodesWithinR, radius);
//    } else if (currCoord < targetMinusR) {
//      searchRadius(currNode.getRightChild(), targetCoords, nodesWithinR, radius);
//    } else if (currCoord > targetPlusR) {
//      searchRadius(currNode.getLeftChild(), targetCoords, nodesWithinR, radius);
//    } else {
//      return;
//    }
//
//  }
//
//  /**
//   * Comparator used for sorting nodes according to their
//   * distance to a particular set of coordinates.
//   */
//  private class NodeDistanceComparator implements Comparator<Node> {
//    private ArrayList<Double> targetCoords;
//
//    /**
//     * Constructor for comparator, takes in basis coordinates.
//     * @param targCoords Target coordinates.
//     */
//    NodeDistanceComparator(ArrayList<Double> targCoords) {
//      targetCoords = targCoords;
//    }
//
//    /**
//     * Function for comparing two nodes, on basis of their
//     * distance from a set of nodes.
//     * @param o1
//     * @param o2
//     * @return
//     */
//    @Override
//    public int compare(Node o1, Node o2) {
//      if (distance(o1.getCoords(), targetCoords) < distance(o2.getCoords(), targetCoords)) {
//        return -1;
//      } else if (distance(o1.getCoords(), targetCoords) > distance(o2.getCoords(), targetCoords)) {
//        return 1;
//      } else {
//        return 0;
//      }
//    }
//  }
//}
