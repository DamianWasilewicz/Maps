package edu.brown.cs.student.maps;

/**
 * Concrete GraphEdge class that implements the Edge interface.
 */
public class GraphEdge implements Edge<GraphNode, GraphEdge> {
  private GraphNode from;
  private GraphNode to;
  private double weight;
  private String name;
  private String id;
  private String type;

  /**
   * GraphEdge constructor.
   *
   * @param nID
   *      setting id field to this
   * @param nName
   *      setting name field to this
   * @param nType
   *      setting type field to this
   * @param s
   *      setting from (start) field to this
   * @param e
   *      setting to (end) field to this
   * @param w
   *      setting weight field to this
   */
  public GraphEdge(String nID, String nName, String nType, GraphNode s, GraphNode e, double w) {
    this.id = nID;
    this.from = s;
    this.to = e;
    this.name = nName;
    this.weight = w;
    this.type = nType;
  }

  /**
   * Accessor for startNode.
   * @return GraphNode
   *      the start Node
   */
  @Override
  public GraphNode startNode() {
    return from;
  }

  /**
   * Accessor for endNode.
   * @return GraphNode
   *      the end Node
   */
  @Override
  public GraphNode endNode() {
    return to;
  }

  /**
   * Accessor for weight.
   * @return double
   *      edge weight
   */
  @Override
  public double getWeight() {
    return weight;
  }

  /**
   * Accessor for ID.
   * @return String
   *      the node's ID
   */
  @Override
  public String getID() {
    return id;
  }

  /**
   * Accessor for startNode.
   * @return GraphNode
   *      the start Node
   */
  @Override
  public GraphNode getStart() {
    return from;
  }

  /**
   * Accessor for endNode.
   * @return GraphNode
   *      the end Node
   */
  @Override
  public GraphNode getEnd() {
    return to;
  }

  /**
   * Mutator for ID.
   * @param e
   *      the node's new end Node
   */
  public void setEnd(GraphNode e) {
    to = e;
  }

  /**
   * ???.
   *
   * @param o
   *      ???
   */
  @Override
  public int compareTo(Edge<GraphNode, GraphEdge> o) {
    return 0;
  }
}
