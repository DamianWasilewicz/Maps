package edu.brown.cs.student.stars;

import edu.brown.cs.student.kdtree.KDTree;
import edu.brown.cs.student.logic.StarsHandler;
import edu.brown.cs.student.logic.Commandable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

public class NeighborsTest {
  //objects
  private StarsHandler handler;
  private Commandable neighbors;
  private Commandable naiveNeighbors;
  private List<String> arguments = null;
  private RandomInputGenerator generator;

  //static reference
  private String path1 = "data/stars/tie-star.csv";
  private String path2 = "data/stars/ten-star.csv";
  final static int MAX_INPUT_SIZE = 100;
  final static int SPACE_RANGE = 40;
  final static int SPACE_MIN = -20;
  private final static int NUM_TRIALS = 500;

  @Before
  public void setUp() {
    handler = new StarsHandler();
    ArrayList<String> args = new ArrayList<>();
    args.add("");
    args.add(path2);
    handler.getMap().get("stars").execute(args);
    neighbors = handler.getMap().get("neighbors");
    naiveNeighbors = handler.getMap().get("naive_neighbors");
  }

  @Before
  public void setUpRandom() {
    handler = new StarsHandler();
    generator = new RandomInputGenerator();
    ArrayList<Star> inputList
        = new ArrayList<>(generator.generateRandomInputs(MAX_INPUT_SIZE, SPACE_MIN, SPACE_RANGE));
//    ArrayList<String> args
//        = new ArrayList<>(generator.generateRandomArgument(false, SPACE_MIN, SPACE_RANGE));
//    arguments = args;
    handler.setStarList(new ArrayList<>(inputList));
    handler.setTree(new KDTree<>(inputList, 3));
    neighbors = handler.getMap().get("neighbors");
    naiveNeighbors = handler.getMap().get("naive_neighbors");
  }

  @After
  public void tearDown() {
    handler = null;
    neighbors = null;
    naiveNeighbors = null;
  }

  /**
   * testing that standard neighbors method with kd tree works properly on coordinates.
   */
  @Test
  public void testNeighborsCoords() {
    setUp();
    ArrayList<String> args = new ArrayList<>();
    args.add("");
    args.add("5");
    args.add("0");
    args.add("0");
    args.add("0");
    neighbors.execute(args);
    assertTrue(handler.getReturnList().size() == 5);
    tearDown();
  }

  /**
   * testing neighbors function works on the kdtree
   */
  @Test
  public void testStandard() {
    setUpRandom();
    arguments = generator.generateRandomArgument(false, SPACE_MIN, SPACE_RANGE);
    Star tar = handler.findTarget(arguments);
    neighbors.execute(arguments);
    ArrayList<Star> standardOutput = new ArrayList<>(handler.getReturnList());
    //check match
    assertTrue(handler.getTotalDistance() == handler.getNaiveTotalDistance());
    for (int i = 0; i < standardOutput.size(); i++) {
      if (i + 1 < standardOutput.size()) {
        assertTrue(standardOutput.get(i).euclideanDistance(tar)
            <= standardOutput.get(i + 1).euclideanDistance(tar));
      }
    }
    tearDown();
  }


  /**
   * MBT test that the two functions return the same output
   * PBT
   */
  @Test
  public void testMatchRandom() {
    setUpRandom();
    for (int i = 0; i < NUM_TRIALS; i++) {
      arguments = generator.generateRandomArgument(false, SPACE_MIN, SPACE_RANGE);
      Star tar = handler.findTarget(arguments);
      neighbors.execute(arguments);
      ArrayList<Star> standardOutput = new ArrayList<>(handler.getReturnList());
      naiveNeighbors.execute(arguments);
      ArrayList<Star> naiveOutput = new ArrayList<>(handler.getReturnList());
      //check match
      assertTrue(naiveOutput.size() == standardOutput.size());
      assertTrue(handler.getTotalDistance() == handler.getNaiveTotalDistance());
      for (int j = 0; j < naiveOutput.size(); j++) {
        assertTrue(naiveOutput.get(j).euclideanDistance(tar) ==
            standardOutput.get(j).euclideanDistance(tar));
      }
    }
    tearDown();
  }

  /**
   * testing naive radius functionality
   * PBT
   */
  @Test
  public void testNaive() {
    setUpRandom();
    arguments = generator.generateRandomArgument(false, SPACE_MIN, SPACE_RANGE);
    Star tar = handler.findTarget(arguments);
    naiveNeighbors.execute(arguments);
    ArrayList<Star> naiveOutput = new ArrayList<>(handler.getReturnList());
    //check match
    assertTrue(handler.getTotalDistance() == handler.getNaiveTotalDistance());
    for (int i = 0; i < naiveOutput.size(); i++) {
      if (i + 1 < naiveOutput.size()) {
        assertTrue(naiveOutput.get(i).euclideanDistance(tar)
            <= naiveOutput.get(i + 1).euclideanDistance(tar));
      }
    }
    tearDown();
  }



}
