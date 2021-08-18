//package edu.brown.cs.student.stars;
//
//import edu.brown.cs.student.kdtree.KDTree;
//import edu.brown.cs.student.logic.StarsHandler;
//import edu.brown.cs.student.maps.Database;
//import org.junit.Test;
//import org.junit.Before;
//import org.junit.After;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
///**
// * This model based testing would help us debug any issues with the KDTree
// */
//public class KDTreeTest {
//  private final static int MAX_NUM_STARS = 100;
//  private final static int MIN_NUM_STARS = 1;
//  private final static double MAX_COORD = 500;
//  private final static double MIN_COORD = -500;
//  private final static int NUM_TRIALS = 500;
//  private final static int MAX_K = MAX_NUM_STARS - 1;
//  private final static int MIN_K = 0;
//  private final static double MAX_R = 500;
//  private final static double MIN_R = 0;
//  private StarsHandler starsApp;
//  private ArrayList<Star> starsList;
//  private KDTree<Star> tree;
//  private Star src;
//
//  @Before
//  public void setUp() {
//    starsApp = new StarsHandler();
//    starsList = new ArrayList<Star>();
//    populateStars();
//  }
//
//  @After
//  public void tearDown() {
//    starsApp = null;
//    starsList = null;
//  }
//
//  public void populateStars() {
//    int numStars = getRandInt(MIN_NUM_STARS, MAX_NUM_STARS);
//    int i = 0;
//    for (i = 0; i < numStars; i++) {
//      double xCoord = getRandDouble(MIN_COORD, MAX_COORD);
//      double yCoord = getRandDouble(MIN_COORD, MAX_COORD);
//      double zCoord = getRandDouble(MIN_COORD, MAX_COORD);
//      starsList.add(new Star(i, "", xCoord, yCoord, zCoord));
//    }
//    starsList.add(new Star(i, "Name", getRandDouble(MIN_COORD, MAX_COORD),
//        getRandDouble(MIN_COORD, MAX_COORD), getRandDouble(MIN_COORD, MAX_COORD)));
//    starsApp.setStarList(starsList);
//    tree = new KDTree<>(starsList, 3);
//    starsApp.setTree(tree);
//
//  }
//
//  private int getRandInt(int min, int max) {
//    return (int) ((Math.random() * (max - min)) + min);
//  }
//
//  private double getRandDouble(double min, double max) {
//    return (double) ((Math.random() * (max - min)) + min);
//  }
//
//  private double distance(ArrayList<Double> a, ArrayList<Double> b) {
//    double distance = 0;
//    for (int i = 0; i < a.size(); i++) {
//      distance += Math.pow(a.get(i) - b.get(i), 2);
//    }
//    return Math.sqrt(distance);
//  }
//
//  public void compareLists(ArrayList<Star> efficientStars, ArrayList<Star> naiveStars,
//                           Star target) {
//    assertEquals(efficientStars.size(), naiveStars.size());
//
//    for (int i = 0; i < efficientStars.size(); i++) {
//      assertEquals(efficientStars.get(i).euclideanDistance(target),
//          efficientStars.get(i).euclideanDistance(target), .05);
//    }
//  }
//
//  @Test
//  public void runNeighborsCoordModel() {
//    setUp();
//    for (int i = 0; i < NUM_TRIALS; i++) {
//      int k = getRandInt(MIN_K, MAX_K);
//      double x = getRandDouble(MIN_COORD, MAX_COORD);
//      double y = getRandDouble(MIN_COORD, MAX_COORD);
//      double z = getRandDouble(MIN_COORD, MAX_COORD);
//      List<String> argument = new ArrayList<>();
//      argument.add("neighbors");
//      argument.add(Integer.toString(k));
//      argument.add(Double.toString(x));
//      argument.add(Double.toString(y));
//      argument.add(Double.toString(z));
//      starsApp.getMap().get("naive_neighbors").execute(argument);
//      ArrayList<Star> inefficientNeighbors = starsApp.getReturnList();
//      starsApp.getMap().get("neighbors").execute(argument);
//      ArrayList<Star> efficientNeighbors = starsApp.getReturnList();
//      //set up the source as a star
//      src = new Star(0, "test", x, y, z);
//      compareLists(efficientNeighbors, inefficientNeighbors, src);
//    }
//    tearDown();
//  }
//
////    @Test
////    public void runNeighborsNamedModel() {
////      setUp();
////      for (int i = 0; i < NUM_TRIALS; i ++) {
////        int k = getRandInt(MIN_K, MAX_K);
////        Star namedStar = starsApp.findStar("Name");
////        double x = namedStar.getX();
////        double y = namedStar.getY();
////        double z = namedStar.getZ();
////        ArrayList<Node> efficientNeighbors = starsApp.indirectEfficientNeighborsSearch(k + 1, x, y, z);
////        efficientNeighbors.remove(0);
////        ArrayList<Star> naiveNeighbors = starsApp.indirectNaiveNeighborsSearch(k, x, y, z, true, "Name");
////        compareLists(efficientNeighbors, naiveNeighbors, namedStar.getCoords());
////      }
////      tearDown();
////    }
//
//  @Test
//  public void runRadiusCoordModel() {
//    setUp();
//    for (int i = 0; i < NUM_TRIALS; i++) {
//      double radius = getRandDouble(MIN_R, MAX_R);
//      double x = getRandDouble(MIN_COORD, MAX_COORD);
//      double y = getRandDouble(MIN_COORD, MAX_COORD);
//      double z = getRandDouble(MIN_COORD, MAX_COORD);
//      List<String> argument = new ArrayList<>();
//      argument.add("radius");
//      argument.add(Double.toString(radius));
//      argument.add(Double.toString(x));
//      argument.add(Double.toString(y));
//      argument.add(Double.toString(z));
//      starsApp.getMap().get("naive_radius").execute(argument);
//      ArrayList<Star> inefficientNeighbors = starsApp.getReturnList();
//      starsApp.getMap().get("radius").execute(argument);
//      ArrayList<Star> efficientNeighbors = starsApp.getReturnList();
//      ArrayList<Double> coords = new ArrayList<Double>();
//      coords.add(x);
//      coords.add(y);
//      coords.add(z);
//      src = new Star(0, "target", x, y, z);
//      compareLists(efficientNeighbors, inefficientNeighbors, src);
//    }
//    tearDown();
//  }
//
////  @Test
////    public void runRadiusNamedModel() {
////      setUp();
////      for (int i = 0; i < NUM_TRIALS; i ++) {
////        double radius = getRandDouble(MIN_R, MAX_R);
////        Star namedStar = starsApp.findStar("Name");
////        double x = namedStar.getX();
////        double y = namedStar.getY();
////        double z = namedStar.getZ();
////        ArrayList<Node> efficientRadius = starsApp.indirectEfficientRadiusSearch(radius, x, y, z);
////        efficientRadius.remove(0);
////        ArrayList<Star> naiveRadius = starsApp.indirectNaiveRadiusSearch(radius, x, y, z, true, "Name");
////        compareLists(efficientRadius, naiveRadius, namedStar.getCoords());
////      }
////      tearDown();
////    }
//}
//
