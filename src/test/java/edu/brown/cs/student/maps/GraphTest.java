package edu.brown.cs.student.maps;

import edu.brown.cs.student.kdtree.KDTree;
import edu.brown.cs.student.logic.MapsHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;


import static org.junit.Assert.assertTrue;
//TODO: just make sure that dijkstra is founding a path if the both src and target node are in the tree/
/**
 * Testing Graph functionality
 */
public class GraphTest {
  private Graph<GraphNode, GraphEdge> g;
  private GraphNode src;
  private GraphNode target;
  private Database db;
  private KDTree<GraphNode> t;
  private List<GraphNode> nodes;
  private GraphNode correct;
  private GraphNode actual;
  private MapsHandler handler;


  @Before
  public void setUpTree() {
    g = new Graph<GraphNode, GraphEdge>();
    db = new Database("data/maps/maps.sqlite3");
    nodes = db.queryTraversableNodes();
    t = new KDTree<>(nodes, 2);
    //setup source and target
    src = new GraphNode("/n/4187.7176.201435564", 41.872916 ,-71.765908, 0, db);
    actual = new GraphNode("/n/4187.7176.201435559", 41.871471 ,-71.765404, 0, db);
    correct = new GraphNode("/n/4187.7176.201636011", 41.872112 ,-71.765205, 0, db);
  }

  @Before
  public void setUpRandom() {
    g = new Graph<GraphNode, GraphEdge>();
    db = new Database("data/maps/maps.sqlite3");
    src = db.queryRandomNode();
    double radius = 0.005; //want to keep path short not to strain compiling time
    //setting maps tree
    nodes = db.queryPartialDatabase(src, radius);
    t = new KDTree<>(nodes, 2);
    //setup source and target
  }

  @After
  public void tearDown() {
    g = null;
    db = null;
    nodes = null;
    src = null;
    target = null;
    t = null;
  }

  /**
   * Testing tree correctness. Assumption is modeled based on an isolated case showcased in
   * the system test titled route_coords_big_db_isolated_case.test
   */
  @Test
  public void testTreeNearest() {
    setUpTree();
    GraphNode s = src;
    for (GraphNode n: nodes) {
      if (n.getId().equals(src.getId())) {
        s = n;
        System.out.println("Found");
      }
    }
    s.setNodeTree(t);
    Collection<GraphEdge> edges = s.getOutEdges();
    edges.toArray();
    System.out.println("end nodes");

    for (GraphEdge e: edges) {
      System.out.println(e.endNode().getId());
    }
    //test if assumption is correct
    GraphNode n = t.neighbors(1,correct,false).get(0);
    assertTrue(n.getId().equals(correct.getId()));
    tearDown();

  }


  /**
   * Testing tree correctness. Assumption is modeled based on an isolated case showcased in
   * the system test titled route_coords_big_db_isolated_case.test
   */
  @Test
  public void testNearest() {
    setUpTree();
    GraphNode s = src;
    for (GraphNode n: nodes) {
      if (n.getId().equals(src.getId())) {
        s = n;
        System.out.println("Found desired node using KDTreeNeighbor");
      }
    }
    s.setNodeTree(t);
    Collection<GraphEdge> edges = s.getOutEdges();
    edges.toArray();
    tearDown();
  }

}
