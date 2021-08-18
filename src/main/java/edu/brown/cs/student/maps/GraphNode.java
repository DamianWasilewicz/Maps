package edu.brown.cs.student.maps;

import edu.brown.cs.student.kdtree.KDTree;
import edu.brown.cs.student.stars.HasCoordinates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * GraphNode class.
 */
public class GraphNode implements Node<GraphNode, GraphEdge>, HasCoordinates {

  // properties of the node
  private String id;
  private double latitude;
  private double longitude;

  // dijkstra pointers
  private GraphEdge prev = null;
  private double cost; //set to infinity
  private Database db;
  private Collection<GraphEdge> outgoingEdges = null;
  private Boolean visited;
  private KDTree<GraphNode> nodeKDTree;

  // HasCoordinates globals
  private double[] coords;
  private int dims;
  private String name;

  /**
   * Constructor for a GraphNode.
   * implements:
   *    - Node: interface that takes in a generic node and edge
   *    - HasCoordinates:
   *
   * @param newID
   *          a unique id
   * @param lat
   *          latitude coordinate
   * @param lon
   *          longitude coordinate
   * @param costWeight
   *          for dijkstra - the cost it would take to get to this node
   * @param database
   *          allows the node to perform queries
   *
   */
  public GraphNode(String newID, double lat, double lon, double costWeight, Database database) {
    id = newID;
    latitude = lat;
    longitude = lon;
    cost = costWeight;
    db = database;
    visited = false;
  }

  /**
   * Mutator method for nodeList.
   *
   * @param tree
   *          setting the KDTree to list
   */
  public void setNodeTree(KDTree<GraphNode> tree) {
    nodeKDTree = tree;
  }

  /**
   * Accessor method for nodeList.
   *
   * @return double
   *     the node's cost
   */
  @Override
  public double getCost() {
    return cost;
  }

  /**
   * Mutator method for cost.
   *
   * @param newCost
   *          setting the cost to newCost
   */
  public void setCost(double newCost) {
    cost = newCost;
  }

  /**
   * Accessor method for the id.
   *
   * @return String
   *     the node's id
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * Accessor method for the edge.
   *
   * @return GraphEdge
   *     the node's cheapest edge
   */
  public GraphEdge getPrev() {
    return prev;
  }

  /**
   * Mutator method for prev (cheapest edge).
   *
   * @param newPrev
   *          setting the cost to newCost
   */
  @Override
  public void setPrev(GraphEdge newPrev) {
    prev = newPrev;
  }

  /**
   * Accessor method for the latitude coordinate.
   *
   * @return double
   *     the node's latitude coordinate
   */
  public double getLat() {
    return latitude;
  }

  /**
   * Accessor method for the longitude coordinate.
   *
   * @return double
   *     the node's longitude coordinate
   */
  public double getLon() {
    return longitude;
  }

  /**
   * Accessor method for the longitude coordinate.
   *
   * @param i
   *     the index of the desired coordinate
   * @return double
   *     the coordinate at that index
   */
  @Override
  public Double getCoordinate(int i) {
    if (i == 0) {
      return latitude;
    } else {
      return longitude;
    }
  }

  /**
   * Accessor method for the dimensions.
   *
   * @return int
   *     the node's dimension
   */
  @Override
  public int numDimensions() {
    return dims;
  }

  /**
   * Accessor method for the node's name.
   *
   * @return
   *     the node's name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Haversine formula gets the distance between a node and another node.
   * source: https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
   *
   * @param o
   *        node to calculate distance to
   * @return
   *        the haversine distance between two nodes.
   */
  @Override
  public double haversine(GraphNode o) {
    double lat1 = latitude;
    double lon1 = longitude;
    double lat2 = o.getLat();
    double lon2 = o.getLon();
    // distance between latitudes and longitudes
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    // convert to radians
    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);

    // apply formulae
    double a = Math.pow(Math.sin(dLat / 2), 2)
            + Math.pow(Math.sin(dLon / 2), 2)
            * Math.cos(lat1)
            * Math.cos(lat2);
    double rad = MapsConstants.RADIUS;
    double c = 2 * Math.asin(Math.sqrt(a));
    return rad * c;
  }

  /**
   * Accessor method for the node's outgoing edges.
   * Queries the node's outgoing edges with the database.
   *
   * @return
   *        a list of the outgoing edges
   */
  @Override
  public Collection<GraphEdge> getOutEdges() {
    //we only want to query if the outgoing edges are null
    if (outgoingEdges != null) {
      return outgoingEdges;
    }

    // query the database
    List<GraphEdge> ways;
    ways = db.getOutgoingEdges(this);


    outgoingEdges = new ArrayList<>();
    for (GraphEdge edge: ways) {
      GraphNode find = findNode(edge.getEnd());
      edge.setEnd(find);
      outgoingEdges.add(edge);
    }
    return outgoingEdges;
  }

  /**
   * Helper method that finds the node in the KDTree, given a node.
   *
   * @param node
   *        node we're tryna find in the kd tree
   * @return GraphNode
   *        the node from the nodeList that matches the id
   */
  public GraphNode findNode(GraphNode node) {
    return nodeKDTree.neighbors(1, node, false).get(0);
  }

  /**
   * Overriding the object's equals method to provide necessary context for the comparator in Graph
   * @param node object to be compared
   * @return a boolean value depending on the relation between two object.
   */
  @Override
  public boolean equals(Object node) {
    if (node == null) {
      return false;
    }

    if (node.getClass() != this.getClass()) {
      return false;
    }
    GraphNode other = (GraphNode) node;
    if ((this.getId() == null) ? (other.getId() != null) : !this.getId().equals(other.getId())) {
      return false;
    }

    if (this.getId() != other.getId()) {
      return false;
    }

    return true;
  }
}
