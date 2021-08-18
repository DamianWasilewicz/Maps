package edu.brown.cs.student.logic;

import edu.brown.cs.student.kdtree.KDTree;
import edu.brown.cs.student.maps.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class MapsHandler {

  private final HashMap<String, Commandable> commandMap;
  // globals for Maps
  private Database db;
  //  private List<GraphNode> nodeList;
  private KDTree<GraphNode> mKDTree;

  //global vars for route mbt
  private List<GraphEdge> dijkstraPath = null;
  private List<GraphEdge> aStarPath = null;
  private List<String> shortestPathWayIDs = null;
  private List<List<Double>> shortestPathCoords = null;
  private List<Double> nearestCoords = null;


  public MapsHandler() {
    commandMap = new HashMap<>();
    this.fillMap();
  }

  public void fillMap() {
    commandMap.put("map", new Maps());
    commandMap.put("ways", new Ways());
    commandMap.put("route", new Route());
    commandMap.put("dijkstra_route", new NaiveRoute()); //object for testing
    commandMap.put("nearest", new Nearest());
  }

  /**
   * Command Hashmap getter.
   *
   * @return the hashmap used to map strings to commmands
   */
  public HashMap<String, Commandable> getMap() {
    return commandMap;
  }

  public Set<HashMap.Entry<String, Commandable>> getCommands() {
    return commandMap.entrySet();
  }

  /**
   * getter for testing.
   * @return the dijkstra shortest path
   */
  public List<GraphEdge> getDijkstraPath() {
    return dijkstraPath;
  }


  /**
   * getter for testing.
   *
   * @return the A* shortest path
   */
  public List<GraphEdge> getaStarPath() {
    return aStarPath;
  }

  /**
   * This method is for testing.
   *
   * @return the maps kdtree of the handler
   */
  public KDTree<GraphNode> getMapsTree() {
    return mKDTree;
  }

  /**
   * This method is for testing.
   *
   */
  public void setDb(Database database) {
    db = database;
  }

  public Database getDB() {
    return db;
  }
  /**
   * This method is for testing.
   *
   */
  public void setMapTree(KDTree<GraphNode> tree) {
    mKDTree = tree;
  }

  public List<List<List<String>>> getWaysInTile(List<Double> coords) {
    System.out.println("Called getWaysInTile");
    return db.getWaysInTile(coords);
  }

  public List<String> getShortestPathWayIDs(){
    return shortestPathWayIDs;
  }

  public List<List<Double>> getShortestPathCoords(){
    return shortestPathCoords;
  }

  public List<Double> getNearestCoords() { return nearestCoords; }
  /**
   * Private inner class for the "map" command.
   * Implements Commandable interface for repl.
   */
  private class Maps implements Commandable {

    /**
     * Method executed in repl.
     * Instantiates a database with the file from the input, queries traversable nodes,
     * and makes a KDTree with that query result.
     *
     * @param argument
     *      List of args from repl.
     */
    @Override
    public void execute(List<String> argument) {
      // args verification helper method
      if (!verifyMapsArgs(argument)) {
        return;
      }

      String fileName = argument.get(1);

      // instantiate Database class & error check
      try {
        db = new Database(argument.get(1));
      } catch (Exception e) {
        System.err.println("ERROR: Couldn't establish a connection with the file argument.");
        return;
      }

      // store list of node information & error check
      List<GraphNode> nodes = db.queryTraversableNodes();
      if (nodes == null) {
        System.err.println("ERROR: querying nodes database returned null list");
        return;
      }

      // instantiate KDTree with nodeList
      mKDTree = new KDTree<>(nodes, 2);

      for (GraphNode node : nodes) {
        node.setNodeTree(mKDTree);
      }

      System.out.println("map set to " + fileName);
    }

    /**
     * Helper method that verifies the arguments.
     * Checks that:
     *  - size is 2
     *  - file exists
     *
     * @param args
     *      list of args to verify
     * @return boolean
     *      True, if the args are valid. False otherwise.
     */
    public boolean verifyMapsArgs(List<String> args) {
      // input size verification
      if (args.size() != 2) {
        System.err.println("ERROR: incompatible input size for maps command");
        return false;
      }

      // input file verification
      BufferedReader fileReader;
      try {
        fileReader = new BufferedReader(new FileReader(args.get(1)));
      } catch (Exception e) {
        System.err.println("ERROR: invalid filepath.");
        return false;
      }
      return true;
    }
  }

  /**
   * Private inner class for the "ways" command.
   * Implements Commandable interface for repl.
   */
  private class Ways implements Commandable {

    /**
     * Method executed in repl.
     * Queries edges bounded within the arguments.
     *
     * @param argument
     *      List of args from repl.
     */
    @Override
    public void execute(List<String> argument) {
      // checks that "map" is called first
      if (mKDTree == null) {
        System.out.println("ERROR: load in map data first");
        return;
      }

      // args verification helper method
      if (!verifyWaysArgs(argument)) {
        return;
      }

      // parsing through input
      String nwLat = argument.get(1);
      String nwLon = argument.get(2);
      String seLat = argument.get(3);
      String seLon = argument.get(4);

      // store list of edge information & error check
      List<String> waysList;
      try {
        waysList = db.getBoundedWays(nwLat, nwLon, seLat, seLon);
      } catch (Exception e) {
        System.err.println("ERROR: SQL query failed for getBoundedWays");
        return;
      }

      // parse through list of way info, print IDs
      for (String wayInfo : waysList) {
        System.out.println(wayInfo);
      }
    }

    /**
     * Helper method that verifies the arguments.
     * Checks that:
     *  - size is 5
     *  - args are doubles
     *
     * @param args
     *      list of args to verify
     * @return boolean
     *      True, if the args are valid. False otherwise.
     */
    private boolean verifyWaysArgs(List<String> args) {
      // input size verification
      if (args.size() != 5) {
        System.err.println("ERROR: incompatible input size for ways command");
        return false;
      }

      // input coords verification
      for (int i = 1; i < 5; i++) {
        try {
          Double.parseDouble(args.get(i));
        } catch (Exception e) {
          System.err.println("ERROR: ways coordinates must be doubles");
          return false;
        }
      }
      return true;
    }
  }

  /**
   * Private inner class for the "route" commands.
   * Implements Commandable interface for repl.
   */
  private class Route implements Commandable {

    /**
     * Method executed in repl.
     * Finds the respective nodes represented by the arguments
     * (either coordinates or intersections) and runs dijkstra's (A*)
     * to print the shortest path from src to target.
     *
     * @param argument
     *      List of args from repl.
     */
    @Override
    public void execute(List<String> argument) {
      // checks that "map" is called first
      if (mKDTree == null) {
        System.out.println("ERROR: load in map data first");
        return;
      }

      // args verification helper method
      if (!verifyRouteArgs(argument)) {
        return;
      }

      Graph<GraphNode, GraphEdge> graph = new Graph<>();
      GraphNode sourceNode;
      GraphNode targetNode;

      // case coordinates
      if (isCoords(argument)) {
        // making dummy node reference to look for actual source node
        double sLat = Double.parseDouble(argument.get(1));
        double sLon = Double.parseDouble(argument.get(2));
        sourceNode = new GraphNode(null, sLat, sLon, 0, null);
        // making dummy node reference to look for actual target node
        double tLat = Double.parseDouble(argument.get(3));
        double tLon = Double.parseDouble(argument.get(4));
        targetNode = new GraphNode(null, tLat, tLon, Double.POSITIVE_INFINITY, null);
        // case intersections
      } else {
        // get rid of the quotes
        String street1 = argument.get(1).replace("\"", "");
        String cross1 = argument.get(2).replace("\"", "");
        String street2 = argument.get(3).replace("\"", "");
        String cross2 = argument.get(4).replace("\"", "");

        // queries for nodes at the intersections and creates dummy nodes reference
        List<GraphNode> intersections = db.findIntersections(street1, cross1, street2, cross2);
        if (intersections == null) {
          System.err.println("ERROR: no node found at the intersection.");
          return;
        }
        sourceNode = intersections.get(0);
        targetNode = intersections.get(1);
      }

      //find nodes using nearest neighbors (mKDTree is set only with traversible nodes)
      GraphNode source = mKDTree.neighbors(1, sourceNode, false).get(0);
      source.setCost(0);
      GraphNode target = mKDTree.neighbors(1, targetNode, false).get(0);

      // calls dijkstra on the source and target
      List<GraphEdge> shortestPath = graph.getAlternateAStarPath(source, target);
//      List<GraphEdge> shortestPath = graph.getDijkstraPath(source, target);
      aStarPath = new ArrayList<>(shortestPath);
      shortestPathWayIDs = new ArrayList<>();
      shortestPathCoords = new ArrayList<>();


      if (shortestPath.size() != 0) {
        for (int i = shortestPath.size() - 1; i >= 0; i--) {
          //TODO: identify what features are important to be returned.
          shortestPathWayIDs.add(shortestPath.get(i).getID());//adding to a render set.
          List<Double> way = new ArrayList<>();
          way.add(shortestPath.get(i).getStart().getLat());
          way.add(shortestPath.get(i).getStart().getLon());
          way.add(shortestPath.get(i).getEnd().getLat());
          way.add(shortestPath.get(i).getEnd().getLon());
          shortestPathCoords.add(way);
          System.out.println(
            shortestPath.get(i).getStart().getId() + " -> " + shortestPath.get(i).getEnd().getId()
              + " : " + shortestPath.get(i).getID());
        }
      } else {
        System.err.println(source.getId() + " -/- " + target.getId());
      }
    }

    /**
     * Helper method that verifies the arguments.
     * Checks that:
     *  - size is 5
     *  - args are doubles or valid street names
     *  - all coordinates or all street names
     *
     * @param args
     *      list of args to verify
     * @return boolean
     *      True, if the args are valid. False otherwise.
     */
    private boolean verifyRouteArgs(List<String> args) {
      // input size verification
      if (args.size() != 5) {
        System.err.println("ERROR: incompatible input size for route command");
        return false;
      }

      // input coords verification: if the arg isn't a double, check that it's a valid street name
      int coordCount = 0;
      int nameCount = 0;
      for (int i = 1; i < 5; i++) {
        try {
          Double.parseDouble(args.get(i));
          coordCount++;
        } catch (Exception e) {
          if (args.get(i).matches("^\"(\\.|[^\"])*\"$")) {
            nameCount++;
            continue;
          }
          System.err.println("ERROR: route args must be doubles or valid street names.");
          return false;
        }
      }

      // valid args will have either coordCount or nameCount == 0
      if (coordCount > 0 && nameCount > 0) {
        System.err.println("ERROR: route args must be all coordinates or all street names.");
        return false;
      }
      return true;
    }

    /**
     * Helper method that checks if the args uses coodinates or street names
     * Called after verifying args are valid, so checking the first one is enough.
     *
     * @param args
     *      list of args to verify
     * @return boolean
     *      True, if args are coords. False if args are street names
     */
    public boolean isCoords(List<String> args) {
      try {
        Double.parseDouble(args.get(1));
        return true;
      } catch (Exception e) {
        return false;
      }
    }
  }

  private class NaiveRoute implements Commandable {

    /**
     * Method executed in repl.
     * Finds the respective nodes represented by the arguments
     * (either coordinates or intersections) and runs dijkstra's (A*)
     * to print the shortest path from src to target.
     *
     * @param argument
     *      List of args from repl.
     */
    @Override
    public void execute(List<String> argument) {
      // checks that "map" is called first
      if (mKDTree == null) {
        System.out.println("ERROR: load in map data first");
        return;
      }

      // args verification helper method
      if (!verifyRouteArgs(argument)) {
        return;
      }

      Graph<GraphNode, GraphEdge> graph = new Graph<>();
      GraphNode sourceNode;
      GraphNode targetNode;

      // case coordinates
      if (isCoords(argument)) {
        // making dummy node reference to look for actual source node
        double sLat = Double.parseDouble(argument.get(1));
        double sLon = Double.parseDouble(argument.get(2));
        sourceNode = new GraphNode(null, sLat, sLon, 0, null);
        // making dummy node reference to look for actual target node
        double tLat = Double.parseDouble(argument.get(3));
        double tLon = Double.parseDouble(argument.get(4));
        targetNode = new GraphNode(null, tLat, tLon, Double.POSITIVE_INFINITY, null);
        // case intersections
      } else {
        // get rid of the quotes
        String street1 = argument.get(1).replace("\"", "");
        String cross1 = argument.get(2).replace("\"", "");
        String street2 = argument.get(3).replace("\"", "");
        String cross2 = argument.get(4).replace("\"", "");

        // queries for nodes at the intersections and creates dummy nodes reference
        List<GraphNode> intersections = db.findIntersections(street1, cross1, street2, cross2);
        if (intersections == null) {
          System.err.println("ERROR: no node found at the intersection.");
          return;
        }
        sourceNode = intersections.get(0);
        targetNode = intersections.get(1);
      }

      //find nodes using nearest neighbors (mKDTree is set only with traversible nodes)
      GraphNode source = mKDTree.neighbors(1, sourceNode, false).get(0);
      source.setCost(0);
      GraphNode target = mKDTree.neighbors(1, targetNode, false).get(0);

      // calls dijkstra on the source and target
      List<GraphEdge> shortestPath = graph.getDijkstraPath(source, target);
      dijkstraPath = new ArrayList<>(shortestPath);
//      List<GraphEdge> shortestPath = graph.getAStarPath(source, target);

      if (shortestPath.size() != 0) {
        for (int i = shortestPath.size() - 1; i >= 0; i--) {
          System.out.println(
            shortestPath.get(i).getStart().getId() + " -> " + shortestPath.get(i).getEnd().getId()
              + " : " + shortestPath.get(i).getID());
        }
      } else {
        System.err.println(source.getId() + " -/- " + target.getId());
      }
    }

    /**
     * Helper method that verifies the arguments.
     * Checks that:
     *  - size is 5
     *  - args are doubles or valid street names
     *  - all coordinates or all street names
     *
     * @param args
     *      list of args to verify
     * @return boolean
     *      True, if the args are valid. False otherwise.
     */
    private boolean verifyRouteArgs(List<String> args) {
      // input size verification
      if (args.size() != 5) {
        System.err.println("ERROR: incompatible input size for route command");
        return false;
      }

      // input coords verification: if the arg isn't a double, check that it's a valid street name
      int coordCount = 0;
      int nameCount = 0;
      for (int i = 1; i < 5; i++) {
        try {
          Double.parseDouble(args.get(i));
          coordCount++;
        } catch (Exception e) {
          if (args.get(i).matches("^\"(\\.|[^\"])*\"$")) {
            nameCount++;
            continue;
          }
          System.err.println("ERROR: route args must be doubles or valid street names.");
          return false;
        }
      }

      // valid args will have either coordCount or nameCount == 0
      if (coordCount > 0 && nameCount > 0) {
        System.err.println("ERROR: route args must be all coordinates or all street names.");
        return false;
      }
      return true;
    }

    /**
     * Helper method that checks if the args uses coodinates or street names
     * Called after verifying args are valid, so checking the first one is enough.
     *
     * @param args
     *      list of args to verify
     * @return boolean
     *      True, if args are coords. False if args are street names
     */
    public boolean isCoords(List<String> args) {
      try {
        Double.parseDouble(args.get(1));
        return true;
      } catch (Exception e) {
        return false;
      }
    }
  }

  /**
   * Private inner class for the "nearest" commands.
   * Implements Commandable interface for repl.
   */
  private class Nearest implements Commandable {

    /**
     * Method executed in repl.
     * Calls for 1 neighbor on coordinates in arguments.
     *
     * @param argument
     *      List of args from repl.
     */
    @Override
    public void execute(List<String> argument) {
      // checks that "map" is called first
      if (mKDTree == null) {
        System.out.println("ERROR: load in map data first");
        return;
      }

      // args verification helper method
      if (!verifyNearestArgs(argument)) {
        return;
      }

      // makes dummy node for KDTree to search for
      double lat = Double.parseDouble(argument.get(1));
      double lon = Double.parseDouble(argument.get(2));
      GraphNode target = new GraphNode(null, lat, lon, 0, null);
      GraphNode nearest = mKDTree.neighbors(1, target, false).get(0);
      nearestCoords = new ArrayList<>();
      nearestCoords.add(nearest.getLat());
      nearestCoords.add(nearest.getLon());
      System.out.println(nearest.getId());
    }

    /**
     * Helper method that verifies the arguments.
     * Checks that:
     *  - size is 3
     *  - args are coordinates (doubles)
     *
     * @param args
     *      list of args to verify
     * @return boolean
     *      True, if the args are valid. False otherwise.
     */
    private boolean verifyNearestArgs(List<String> args) {
      // input size verification
      if (args.size() != 3) {
        System.err.println("ERROR: incompatible input size for nearest command");
        return false;
      }


      // input coords verification
      for (int i = 1; i < 3; i++) {
        try {
          Double.parseDouble(args.get(i));
        } catch (Exception e) {
          System.err.println("ERROR: nearest coordinates must be doubles");
          return false;
        }
      }
      return true;
    }
  }

}
