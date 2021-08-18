package edu.brown.cs.student.maps;

import edu.brown.cs.student.kdtree.KDTree;
import edu.brown.cs.student.logic.MapsHandler;
import edu.brown.cs.student.logic.Commandable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RoutesTests {
  //objects
  private MapsHandler handler;
  private Commandable routeDijkstra;
  private Commandable routeAStar;
  private List<String> arguments = null;
  private KDTree<GraphNode> mapsTree = null;

  //static reference
  private static String filepath1 = "data/maps/smallMaps.sqlite3";
  private static String filepath2 = "data/maps/maps.sqlite3";
  private static double smallDBLatRange = 0.1;
  private static double smallDBLonRange = 0.01;
  private static double smallDBlatMin = 41.8;
  private static double smallDBlonMin = -71.4;

  @Before
  public void setUpBigMaps() {
    handler = new MapsHandler();
    Database db = new Database(filepath2);
    handler.setDb(db);//setting database,
    GraphNode start = db.queryRandomNode();
    double radius = 0.005; //want to keep path short not to strain compiling time
    //setting maps tree
    List<GraphNode> nodes = db.queryPartialDatabase(start, radius);

    mapsTree = new KDTree<>(nodes, 2);
    for (GraphNode node : nodes) {
      node.setNodeTree(mapsTree);
    }
    handler.setMapTree(mapsTree);
    List<String> a = new ArrayList<>();
    a.add("testArg");
    a.add(Double.toString(start.getLat()));
    a.add(Double.toString(start.getLon()));
    //want to ensure i am not picking a path that is too long so
    GraphNode end = mapsTree.neighbors(7, start, false).get(6);
    a.add(Double.toString(end.getLat()));
    a.add(Double.toString(end.getLat()));
    //at this point I can simply run route on my handler, using a smaller tree.
    routeDijkstra = handler.getMap().get("dijkstra_route");
    routeAStar = handler.getMap().get("route");
    arguments = a;
  }

  @Before
  public void setUpSmallMaps() {
    handler = new MapsHandler();
    ArrayList<String> args = new ArrayList<>();
    args.add("");
    args.add(filepath1);
    handler.getMap().get("map").execute(args);
    mapsTree = handler.getMapsTree();
    routeDijkstra = handler.getMap().get("dijkstra_route");
    routeAStar = handler.getMap().get("route");
    ArrayList<String> a = new ArrayList<>();
    //want lat to be from 41.8-41.9, lon to be from -71.4 to -71.41
    a.add("testArg");
    //first coords
    a.add(Double.toString((Math.random() * smallDBLatRange) + smallDBlatMin));
    a.add(Double.toString((Math.random() * smallDBLonRange) + smallDBlonMin));
    //second coords
    a.add(Double.toString((Math.random() * smallDBLatRange) + smallDBlatMin));
    a.add(Double.toString((Math.random() * smallDBLonRange) + smallDBlonMin));
    arguments = a;
  }


  @After
  public void tearDown() {
    handler = null;
    routeDijkstra = null;
    routeAStar = null;
    arguments = null;
  }

  /**
   * Testing property of dijkstra on small db
   */
//  @Test
  public void smallDBDijkstraAStar() {
    setUpSmallMaps();
    routeAStar.execute(arguments);
    List<GraphEdge> edges = handler.getaStarPath();
    //check conditions: cost should be incrementing
    for (int i=0; i<edges.size()-2; i++){
      assertTrue(edges.get(i).getStart().getCost() < edges.get(i+1).getStart().getCost());
    }
    routeDijkstra.execute(arguments);
    List<GraphEdge> edges2 = handler.getDijkstraPath();
    //check conditions: cost should be incrementing
    for (int i=0; i<edges2.size()-2; i++) {
      assertTrue(edges2.get(i).getStart().getCost() < edges2.get(i + 1).getStart().getCost());
    }
    tearDown();
  }

  /**
   * testing that standard neighbors method with kd tree works properly on coordinates.
   */
//  @Test
  public void bigDBDijkstraAStar() {
    setUpBigMaps();
    routeAStar.execute(arguments);
    List<GraphEdge> edges = handler.getaStarPath();
    System.out.println(edges.size());
    //check conditions: cost should be decrementing
    for (int i=0; i<edges.size()-2; i++){
      //bigger because in reverse order
      assertTrue(edges.get(i).getStart().getCost() > edges.get(i+1).getStart().getCost());
    }
    routeDijkstra.execute(arguments);
    List<GraphEdge> edges2 = handler.getDijkstraPath();
    //check conditions: cost should be incrementing
    for (int i=0; i<edges2.size()-2; i++) {
      assertTrue(edges2.get(i).getStart().getCost() > edges2.get(i + 1).getStart().getCost());
    }
    tearDown();
  }


  /**
   * MBT: Testing a match between dijkstra and aStar on a random route in the small db
   */
//  @Test
  public void smallDBDijkstraAStarMatch() {
    setUpSmallMaps();
    routeAStar.execute(arguments);
    routeDijkstra.execute(arguments);
    List<GraphEdge> dijkstraPath = handler.getaStarPath();
    List<GraphEdge> aStarPath = handler.getDijkstraPath();
    //check conditions: the two algorithms return the same path, meaning same edge IDs.
    for (int i=0; i< dijkstraPath.size(); i++) {
      assertTrue(dijkstraPath.get(i).getID().equals(aStarPath.get(i).getID()));
    }
    tearDown();
  }

  /**
   * MBT: Testing a match between dijkstra and aStar on a random route in the big db
   *
   * NOTE: we expect this test to fail in some cases. We managed to isolate a case were it fails
   * in our systems tests, titled "route_coords_big_db_isolate_case". It passes for our * implementation
   * but not Dijkstra.
   */
//  @Test
  public void bigDBDijkstraAStarMatch() {
    setUpBigMaps();
    routeAStar.execute(arguments);
    routeDijkstra.execute(arguments);
    List<GraphEdge> dijkstraPath = handler.getaStarPath();
    List<GraphEdge> aStarPath = handler.getDijkstraPath();
    //check conditions: the two algorithms return the same path, meaning same edge IDs.
    for (int i=0; i< dijkstraPath.size(); i++) {
      assertTrue(dijkstraPath.get(i).getID().equals(aStarPath.get(i).getID()));
    }
    tearDown();
  }



}
