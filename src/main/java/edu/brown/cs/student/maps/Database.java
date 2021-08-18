package edu.brown.cs.student.maps;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Database Class
 * Models an object that interacts with an SQLite3 database.
 */
public class Database {

  private static Connection conn = null;
  private LoadingCache<GraphNode, List<GraphEdge>> outgoingEdges;
  private LoadingCache<List<Double>, List<List<List<String>>>> tileWays;

  /**
   * Instantiates the database and establishes the connection to the database.
   *
   * @param filename file name of SQLite3 database to open.
   */
  public Database(String filename) {
    try {
      Class.forName("org.sqlite.JDBC");
      String urlToDB = "jdbc:sqlite:" + filename;
      conn = DriverManager.getConnection(urlToDB);
      Statement statement = conn.createStatement();
      //turn foreign keys on
      statement.executeUpdate("PRAGMA foreign_keys=ON");
    } catch (Exception e) {
      System.err.println("ERROR: initializing database failed");
    }
    try {
      //creating table to store userCheckin
      PreparedStatement prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS users("
          + "time_stamp REAL,"
          + "user_id INTEGER ,"
          + "user_name TEXT,"
          + "latitude REAL,"
          + "longitude REAL,"
          + "PRIMARY KEY (user_id));");
      prep.executeUpdate();
      //can't really use caching here because checkins are constantly changing.
    } catch (Exception e) {
      System.err.println("ERROR: database failed creating checkin table");
    }
    //setting up the caches
    outgoingEdges = CacheBuilder.newBuilder()
        //NOTE: can set appropriate eviction protocol as needed
        .build(
            new CacheLoader<GraphNode, List<GraphEdge>>() {
              @Override
              public List<GraphEdge> load(GraphNode graphNode) { // no checked exception
                return queryOutgoingEdges(graphNode);
              }
            });
    //Tile cache
    tileWays = CacheBuilder.newBuilder()
        //NOTE: can set appropriate eviction protocol as needed
        .build(
            new CacheLoader<List<Double>, List<List<List<String>>>>() {
              @Override
              public List<List<List<String>>> load(List<Double> coords) { // no checked exception
                return queryAllWaysFromTile(coords);
              }
            });
  }

  /**
   * Query all the traversable nodes that are stored in our database and create
   * instances of them. "this" instance of the database is associated with each
   * node to give them the ability to query later.
   *
   * @return a list of traversable GraphNodes
   */
  public List<GraphNode> queryTraversableNodes() {
    List<GraphNode> nodes = new ArrayList<>();

    // query nodes that are either the start or end of a way (traversable)
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement(
          "SELECT DISTINCT * "
              + "FROM node "
              + "WHERE (node.id IN "
              + "(SELECT way.start FROM way WHERE way.type != ? AND way.type != ?))"
              + "OR (node.id IN "
              + "(SELECT way.end FROM way WHERE way.type != ? AND way.type != ?))");
      // unclassified or empty string means not traversable
      prep.setString(1, "unclassified");
      prep.setString(2, "");
      prep.setString(3, "unclassified");
      prep.setString(4, "");
    } catch (Exception e) {
      System.err.println("ERROR: prepareStatement SQL query failed");
      return null;
    }

    ResultSet rs;
    try {
      rs = prep.executeQuery();
    } catch (Exception e) {
      System.err.println("ERROR: executeQuery failed");
      return null;
    }

    // parses through each row of query and make a node with that information
    try {
      while (rs.next()) {
        String id = rs.getString("id");
        Double lat = rs.getDouble("latitude");
        Double lon = rs.getDouble("longitude");
        GraphNode n = new GraphNode(id, lat, lon, Double.POSITIVE_INFINITY, this);
        nodes.add(n);
      }
    } catch (Exception e) {
      System.err.println("ERROR: parsing node database failed");
      return null;
    }

    // closing request
    try {
      rs.close();
      prep.close();
    } catch (Exception e) {
      System.err.println("ERROR: close failed");
      return null;
    }

    return nodes;
  }

  /**
   * Queries edges that start at the node passed in the parameter.
   *
   * @param startNode node to find outgoing edges for
   * @return a list of outgoing GraphEdges from the startNode
   */
  public List<GraphEdge> queryOutgoingEdges(GraphNode startNode) {
    GraphNode startN = startNode;
    String startId = startNode.getId();

    // query from way table where start = startNode's ID and the type is traversable
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement(
          "SELECT DISTINCT "
              + "way.id, way.name, way.type, way.start, way.end, node.latitude, node.longitude "
              + "FROM way JOIN node "
              + "ON way.end=node.id "
              + "WHERE start=? AND way.type!=? AND way.type!=?");
      prep.setString(1, startId);
      prep.setString(2, "unclassified");
      prep.setString(3, "");
    } catch (Exception e) {
      System.err.println("ERROR: prepareStatement SQL query failed.");
      return null;
    }

    ResultSet rs;
    try {
      rs = prep.executeQuery();
    } catch (Exception e) {
      System.err.println("ERROR: executeQuery failed.");
      return null;
    }

    // parses through each row of query, and makes a GraphEdge
    List<GraphEdge> oEdges = new ArrayList<>();
    try {
      while (rs.next()) {
        // gets coordinates of end node
        String endID = rs.getString("end");
        Double endLat = rs.getDouble("latitude");
        Double endLon = rs.getDouble("longitude");
        GraphNode endN = new GraphNode(endID, endLat, endLon, Double.POSITIVE_INFINITY, this);
        String edgeID = rs.getString("id");
        String edgeName = rs.getString("name");
        String edgeType = rs.getString("type");
        Double weight = startN.haversine(endN);
        GraphEdge outgoingEdge = new GraphEdge(edgeID, edgeName, edgeType, startN, endN, weight);
        oEdges.add(outgoingEdge);
      }
    } catch (Exception e) {
      System.err.println("ERROR: parsing node database failed.");
      return null;
    }

    //closes request
    try {
      rs.close();
      prep.close();
    } catch (Exception e) {
      System.err.println("ERROR: close failed");
      return null;
    }

    return oEdges;
  }

  /**
   * Queries ways where both start and end node are between given range.
   * This method is used in the Ways class
   *
   * @param coords list of coordinates defining bound.
   * @return a list of ways included in bounded area
   */
  public List<List<List<String>>> queryAllWaysFromTile(List<Double> coords) {
    System.out.println("Called queryAllWaysFromTile");
    String nwLat = Double.toString(coords.get(0));
    String nwLon = Double.toString(coords.get(1));
    String seLat = Double.toString(coords.get(2));
    String seLon = Double.toString(coords.get(3));
    //we want to get all the ways that are between given lat and lon params
    //resource: https://stackoverflow.com/questions/4267929/whats-the-best-way-to-join-on-the-same-table-twice
    PreparedStatement prep;
    try {
      prep
          = conn.prepareStatement(
          "SELECT w.id, w.name, w.type, w.start, n1.latitude, n1.longitude, w.end, n2.latitude, n2.longitude "
              + "FROM (way w"
              + " JOIN node n1 ON n1.id = w.start"
              + " JOIN node n2 ON n2.id = w.end)"
              + "WHERE (n1.latitude BETWEEN ? and ? AND n1.longitude BETWEEN ? and ?) "
              + "OR (n2.latitude BETWEEN ? and ? AND n2.longitude BETWEEN ? and ?)"
              + " ORDER BY w.id ASC;");
      prep.setString(1, seLat);
      prep.setString(2, nwLat);
      prep.setString(3, nwLon);
      prep.setString(4, seLon);
      prep.setString(5, seLat);
      prep.setString(6, nwLat);
      prep.setString(MapsConstants.SEVEN, nwLon);
      prep.setString(MapsConstants.EIGHT, seLon);
    } catch (Exception e) {
      System.out.println("ERROR: prepareStatement SQL query failed.");
      return null;
    }

    ResultSet rs;
    try {
      rs = prep.executeQuery();
    } catch (Exception e) {
      System.out.println("ERROR: executeQuery failed.");
      return null;
    }

    List<List<List<String>>> ways = new ArrayList<>();
    try {
      while (rs.next()) {
        List<List<String>> tuple = new ArrayList<>();
        List<String> node1 = new ArrayList<>();
        //w.id, w.name, w.type, w.start, n1.latitude, n1.longitude, w.end, n2.latitude, n2.longitude "
//        String startID = rs.getString("start");
        String startLat = rs.getString(5);
        String startLon = rs.getString(6);
        node1.add(startLat);
        node1.add(startLon);
        tuple.add(node1);
        List<String> node2 = new ArrayList<>();
//        GraphNode startNode = new GraphNode(startID, startLat, startLon, Double.POSITIVE_INFINITY, this);
//        String endID = rs.getString("end");
        String endLat = rs.getString(8);
        String endLon = rs.getString(9);
        node2.add(endLat);
        node2.add(endLon);
        tuple.add(node2);
        List<String> meta = new ArrayList<>();
        String wayID = rs.getString(1);
        String wayName = rs.getString(2);
        String wayType = rs.getString(3);
        meta.add(wayType);
        meta.add(wayName);
        meta.add(wayID);
        tuple.add(meta);
//        Double weight = startNode.haversine(endNode);
//        GraphEdge outgoingEdge = new GraphEdge(edgeID, edgeName, edgeType, startNode, endNode, weight);
        ways.add(tuple);
      }
    } catch (Exception e) {
      System.out.println("ERROR: parsing node database failed.");
      return null;
    }

    // closes request
    try {
      rs.close();
      prep.close();
    } catch (Exception e) {
      System.out.println("ERROR: close failed");
      return null;
    }

    // return list of way IDs
    System.out.println("Returned ways from database");
    System.out.println(ways);
    return ways;
  }


  /**
   * Queries ways where both start and end node are between given range.
   * This method is used in the Ways class
   *
   * @param nwLat latitude of northwest node
   * @param nwLon longitude of northwest node
   * @param seLat latitude of southeast node
   * @param seLon longitude of southeast node
   * @return a list of way ids included in bounded area
   */
  public List<String> getBoundedWays(String nwLat, String nwLon, String seLat, String seLon) {
    //we want to get all the ways that are between given lat and lon params
    //so essentially we want to join the
    PreparedStatement prep;
    try {
      prep
          = conn.prepareStatement(
          "SELECT way.id "
              + "FROM way "
              + "WHERE (way.start IN (SELECT node.id FROM node "
              + "WHERE (node.latitude BETWEEN ? and ?) "
              + "AND (node.longitude BETWEEN ? and ?))) "
              + "OR (way.end IN (SELECT node.id FROM node "
              + "WHERE (node.latitude BETWEEN ? and ?) "
              + "AND (node.longitude BETWEEN ? and ?))) ORDER BY way.id ASC;");
      prep.setString(1, seLat);
      prep.setString(2, nwLat);
      prep.setString(3, nwLon);
      prep.setString(4, seLon);
      prep.setString(5, seLat);
      prep.setString(6, nwLat);
      prep.setString(MapsConstants.SEVEN, nwLon);
      prep.setString(MapsConstants.EIGHT, seLon);
    } catch (Exception e) {
      System.err.println("ERROR: prepareStatement SQL query failed.");
      return null;
    }

    ResultSet rs;
    try {
      rs = prep.executeQuery();
    } catch (Exception e) {
      System.err.println("ERROR: executeQuery failed.");
      return null;
    }

    List<String> ways = new ArrayList<>();
    try {
      while (rs.next()) {
        ways.add(rs.getString("id"));
      }
    } catch (Exception e) {
      System.err.println("ERROR: parsing node database failed.");
      return null;
    }

    // closes request
    try {
      rs.close();
      prep.close();
    } catch (Exception e) {
      System.err.println("ERROR: close failed");
      return null;
    }

    // return list of way IDs
    return ways;
  }

  /**
   * This method returns the nodes at the provided intersections.
   * This method is used in the routes class
   *
   * @param street1 name of street 1
   * @param cross1  name of street intersecting with street 1
   * @param street2 name of street 2
   * @param cross2  name of street intersecting with street 2
   * @return a list of 2 GraphNodes at the intersections
   */
  public List<GraphNode> findIntersections(String street1, String cross1, String street2,
                                           String cross2) {
    List<GraphNode> nodes = new ArrayList<>();
    List<GraphNode> intersect1 = new ArrayList<>();
    List<GraphNode> intersect2 = new ArrayList<>();

    //make statement
    PreparedStatement firstIntersection;
    try {
      //Piazza @1257 and @1303
      firstIntersection = conn.prepareStatement(
          "SELECT * FROM node WHERE "
              + "(node.id IN ("
              + "SELECT way.start FROM way WHERE way.name=? "
              + "INTERSECT SELECT way.start FROM way WHERE name=?))"
              + "OR (node.id IN ("
              + "SELECT way.start FROM way WHERE way.name=? "
              + "INTERSECT SELECT way.end FROM way WHERE name=?)) "
              + "OR (node.id IN ("
              + "SELECT way.end FROM way WHERE way.name=? "
              + "INTERSECT SELECT way.end FROM way WHERE name=?))"
              + "OR (node.id IN ("
              + "SELECT way.end FROM way WHERE way.name=? "
              + "INTERSECT SELECT way.start FROM way WHERE name=?))");
      //case end and start
      String x1 = street1;
      String x2 = cross1;
      firstIntersection.setString(1, x1);
      firstIntersection.setString(2, x2);
      firstIntersection.setString(3, x1);
      firstIntersection.setString(4, x2);
      firstIntersection.setString(5, x1);
      firstIntersection.setString(6, x2);
      firstIntersection.setString(MapsConstants.SEVEN, x1);
      firstIntersection.setString(MapsConstants.EIGHT, x2);
    } catch (Exception e) {
      System.err.println("ERROR: prepareStatement SQL query failed.");
      return null;
    }

    ResultSet firstIntersectResultSet;
    try {
      firstIntersectResultSet = firstIntersection.executeQuery();
    } catch (Exception e) {
      System.err.println("ERROR: executeQuery failed.");
      return null;
    }

    // parses through each row of query
    try {
      while (firstIntersectResultSet.next()) {
        String id = firstIntersectResultSet.getString("id");
        double lat = firstIntersectResultSet.getDouble("latitude");
        double lon = firstIntersectResultSet.getDouble("longitude");
        GraphNode intersectOne = new GraphNode(id, lat, lon, 0, this);
        intersect1.add(intersectOne);
      }
    } catch (Exception e) {
      System.err.println("ERROR: parsing node database failed.");
      return null;
    }

    // closing request
    try {
      firstIntersectResultSet.close();
      firstIntersection.close();
    } catch (Exception e) {
      System.err.println("ERROR: close failed.");
      return null;
    }

    //second node
    PreparedStatement secondIntersection;
    try {
      //Piazza @1257 and @1303
      secondIntersection = conn.prepareStatement(
          "SELECT * FROM node WHERE "
              + "(node.id IN ("
              + "SELECT way.start FROM way WHERE way.name=? "
              + "INTERSECT SELECT way.start FROM way WHERE name=?))"
              + "OR (node.id IN ("
              + "SELECT way.start FROM way WHERE way.name=? "
              + "INTERSECT SELECT way.end FROM way WHERE name=?)) "
              + "OR (node.id IN ("
              + "SELECT way.end FROM way WHERE way.name=? "
              + "INTERSECT SELECT way.end FROM way WHERE name=?))"
              + "OR (node.id IN ("
              + "SELECT way.end FROM way WHERE way.name=? "
              + "INTERSECT SELECT way.start FROM way WHERE name=?))");
      String y1 = street2;
      String y2 = cross2;
      secondIntersection.setString(1, y1);
      secondIntersection.setString(2, y2);
      secondIntersection.setString(3, y1);
      secondIntersection.setString(4, y2);
      secondIntersection.setString(5, y1);
      secondIntersection.setString(6, y2);
      secondIntersection.setString(MapsConstants.SEVEN, y1);
      secondIntersection.setString(MapsConstants.EIGHT, y2);
    } catch (Exception e) {
      System.err.println("ERROR: prepareStatement SQL query failed.");
      return null;
    }

    ResultSet secondIntersectResultSet;
    try {
      secondIntersectResultSet = secondIntersection.executeQuery();
    } catch (Exception e) {
      System.err.println("ERROR: executeQuery failed.");
      return null;
    }

    try {
      // parses through each row of query https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html
      while (secondIntersectResultSet.next()) {
        String id = secondIntersectResultSet.getString("id");
        double lat = secondIntersectResultSet.getDouble("latitude");
        double lon = secondIntersectResultSet.getDouble("longitude");
        GraphNode intersectTwo = new GraphNode(id, lat, lon, 0, this);
        intersect2.add(intersectTwo);
      }
    } catch (Exception e) {
      System.err.println("ERROR: parsing node database failed.");
      return null;
    }

    try {
      //closing request
      secondIntersectResultSet.close();
      secondIntersection.close();
    } catch (Exception e) {
      System.err.println("ERROR: close failed.");
      return null;
    }

    //a random node from each intersect set
    if (intersect1.size() == 0) {
      System.err.println("ERROR: could not find intersection between street 1 and cross street 1");
      return null;
    }
    if (intersect2.size() == 0) {
      System.err.println("ERROR: could not find intersection between street 2 and cross street 2");
      return null;
    }
    nodes.add(intersect1.get(new Random().nextInt(intersect1.size())));
    nodes.add(intersect2.get(new Random().nextInt(intersect2.size())));

    return nodes;
  }

  /**
   * Method using the guava cache.
   * Applies a "get-if-absent-compute" approach:
   * If the desired data is stored in the cache, return in.
   * If it isn't, query and store it, then return it.
   *
   * @param graphNode node getting outgoing edges for
   * @return list of GraphEdges that are outgoing from graphNode
   */
  public List<GraphEdge> getOutgoingEdges(GraphNode graphNode) {
    try {
      return outgoingEdges.get(graphNode);
    } catch (Exception e) {
      System.out.println("ERROR: guava cache error.");
      return null;
    }
  }

  /**
   * Ways in a given map tile cache
   */
  public List<List<List<String>>> getWaysInTile(List<Double> coords) {
    try {
      return tileWays.get(coords);
    } catch (Exception e) {
      System.out.println("ERROR: guava cache error.");
      return null;
    }
  }

  /**
   * Query all the traversable nodes within a bound
   *
   * @return a list of traversable GraphNodes
   */
  public List<GraphNode> queryPartialDatabase(GraphNode node, double rad) {
    List<GraphNode> nodes = new ArrayList<>();

    // query nodes that are either the start or end of a way (traversable)
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement(
          "SELECT * FROM node "
              + "WHERE (node.id IN (SELECT node.id FROM node "
              + "WHERE (node.id IN (SELECT way.start FROM way WHERE way.type !=? "
              + "AND way.type !=?)) OR (node.id IN (SELECT way.end FROM way "
              + "WHERE way.type !=? AND way.type !=?))))"
              + "AND (node.latitude BETWEEN ? and ?)"
              + "AND (node.longitude BETWEEN  ? and ?)");

//      SELECT * FROM node WHERE (node.id IN (SELECT node.id FROM node WHERE (node.id IN (SELECT way.start FROM way WHERE way.type !="unclassified" AND way.type !="")) OR (node.id IN (SELECT way.end FROM way WHERE way.type !="unclassified" AND way.type !="")))) AND (node.latitude BETWEEN "41.89" and "41.90") AND (node.longitude BETWEEN  "-72.9" and "-71");
      // unclassified or empty string means not traversable
      prep.setString(1, "unclassified");
      prep.setString(2, "");
      prep.setString(3, "unclassified");
      prep.setString(4, "");
      double latupper = node.getLat() + rad;
      double latlower = node.getLat() - rad;
      double lonupper = node.getLon() + rad;
      double lonlower = node.getLon() - rad;
      prep.setString(5, Double.toString(latlower));
      prep.setString(6, Double.toString(latupper));
      prep.setString(7, Double.toString(lonlower));
      prep.setString(8, Double.toString(lonupper));
    } catch (Exception e) {
      System.err.println("ERROR: prepareStatement SQL query failed");
      return null;
    }

    ResultSet rs;
    try {
      rs = prep.executeQuery();
    } catch (Exception e) {
      System.err.println("ERROR: executeQuery failed");
      return null;
    }

    // parses through each row of query and make a node with that information
    try {
      while (rs.next()) {
        String id = rs.getString("id");
        Double lat = rs.getDouble("latitude");
        Double lon = rs.getDouble("longitude");
        GraphNode n = new GraphNode(id, lat, lon, Double.POSITIVE_INFINITY, this);
        nodes.add(n);
      }
    } catch (Exception e) {
      System.err.println("ERROR: parsing node database failed");
      return null;
    }

    // closing request
    try {
      rs.close();
      prep.close();
    } catch (Exception e) {
      System.err.println("ERROR: close failed");
      return null;
    }

    return nodes;
  }

  /**
   * Query all the traversable nodes within a bound
   *
   * @return a random node GraphNodes
   */
  public GraphNode queryRandomNode() {
    GraphNode node = null;

    // query nodes that are either the start or end of a way (traversable)
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement(
          "SELECT * FROM node "
              + "order by random() "
              + "LIMIT 1");
    } catch (Exception e) {
      System.err.println("ERROR: prepareStatement SQL query failed");
      return null;
    }
    ResultSet rs;
    try {
      rs = prep.executeQuery();
    } catch (Exception e) {
      System.err.println("ERROR: executeQuery failed");
      return null;
    }
    // parses through each row of query and make a node with that information
    try {
      while (rs.next()) {
        String id = rs.getString("id");
        Double lat = rs.getDouble("latitude");
        Double lon = rs.getDouble("longitude");
        GraphNode n = new GraphNode(id, lat, lon, Double.POSITIVE_INFINITY, this);
        node = n;
      }
    } catch (Exception e) {
      System.err.println("ERROR: parsing node database failed");
      return null;
    }

    // closing request
    try {
      rs.close();
      prep.close();
    } catch (Exception e) {
      System.err.println("ERROR: close failed");
      return null;
    }

    return node;
  }

  //timestamp, user_id, user_name, latitude, longitude]
  public void insertUserData(String timestamp, String user_id, String user_name, String latitude,
                             String longitude) {
    PreparedStatement prep;
    try {
      prep = conn.prepareStatement(
          "INSERT INTO users "
              + "VALUES (?, ?, ?, ?, ?)");

//      SELECT * FROM node WHERE (node.id IN (SELECT node.id FROM node WHERE (node.id IN (SELECT way.start FROM way WHERE way.type !="unclassified" AND way.type !="")) OR (node.id IN (SELECT way.end FROM way WHERE way.type !="unclassified" AND way.type !="")))) AND (node.latitude BETWEEN "41.89" and "41.90") AND (node.longitude BETWEEN  "-72.9" and "-71");
      // unclassified or empty string means not traversable
      prep.setString(1, timestamp);
      prep.setString(2, user_id);
      prep.setString(3, user_name);
      prep.setString(4, latitude);
      prep.setString(5, longitude);

    } catch (Exception e) {
      System.err.println("ERROR: prepareStatement adding user data failed");
    }
  }

  public List<UserCheckin> queryUserData(String user_id) {
    List<UserCheckin> returnlist = new ArrayList<>();
    PreparedStatement prep = null;
    try {
      prep = conn.prepareStatement(
          "SELECT * from users where user_id = ?");

      prep.setString(1, user_id);
    } catch (Exception e) {
      System.err.println("ERROR: prepareStatement adding user data failed");
    }
    ResultSet rs;
    try {
      rs = prep.executeQuery();
    } catch (Exception e) {
      System.err.println("ERROR: executeQuery failed");
      return null;
    }
    // parses through each row of query and make a node with that information
    try {
      while (rs.next()) {
        //String timestamp, String user_id, String user_name, String latitude, String longitude
//        String time = rs.getString("time_stamp");
//        String id = Double.toString(rs.getInt("user_id"));
//        String name = rs.getString("user_name");
//        String lat = Double.toString(rs.getDouble("latitude"));
//        String lon = Double.toString(rs.getDouble("longitude"));
        Double time = rs.getDouble("time_stamp");
        Integer id = rs.getInt("user_id");
        String name = rs.getString("user_name");
        Double lat = rs.getDouble("latitude");
        Double lon = rs.getDouble("longitude");
        UserCheckin data = new UserCheckin(id, name, time, lat, lon);
        returnlist.add(data);
      }
    } catch (Exception e) {
      System.err.println("ERROR: parsing node database failed");
      return null;
    }
    // closing request
    try {
      rs.close();
      prep.close();
    } catch (Exception e) {
      System.err.println("ERROR: close failed");
      return null;
    }
    return returnlist;
  }



}
