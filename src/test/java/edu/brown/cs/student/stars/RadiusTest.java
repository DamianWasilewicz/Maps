package edu.brown.cs.student.stars;


import edu.brown.cs.student.csvpasrer.CSVParser;
import edu.brown.cs.student.kdtree.KDTree;
import edu.brown.cs.student.logic.StarsHandler;
import edu.brown.cs.student.logic.Commandable;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * JUnit tests
 */
public class RadiusTest {
  //objects
  private CSVParser reader;
  private StarsHandler handler;
  private Commandable radiusCommand;
  private Commandable naiveRadiusCommand;
  //variables
  private List<String> arguments = null;
  private RandomInputGenerator generator;
  //static reference
  private String path1 = "data/stars/tie-star.csv";
  private String path2 = "data/stars/ten-star.csv";
  private String path3 = "data/stars/edgeCase.csv";

  final static int MAX_INPUT_SIZE = 10;
  final static int SPACE_RANGE = 2;
  final static int SPACE_MIN = -1;
  private final static int NUM_TRIALS = 500;


  /*
  Random inputs
   */
  @Before
  public void setUpRandom() {
    handler = new StarsHandler();
    generator = new RandomInputGenerator();
    ArrayList<Star> inputList
        = new ArrayList<>(generator.generateRandomInputs(MAX_INPUT_SIZE, SPACE_MIN, SPACE_RANGE));
    handler.setStarList(new ArrayList<>(inputList));
    handler.setTree(new KDTree<>(inputList, 3));
    radiusCommand = handler.getMap().get("radius");
    naiveRadiusCommand = handler.getMap().get("naive_radius");
  }

  @Before
  public void setUpEdgeCase() {
    handler = new StarsHandler();
    ArrayList<String> a = new ArrayList<>();
    a.add("testarg");
    a.add(path3);
    handler.getMap().get("stars").execute(a);
    radiusCommand = handler.getMap().get("neighbors");
    naiveRadiusCommand = handler.getMap().get("naive_neighbors");
    List<String> args = new ArrayList<>();
    args.add("testarg");
    args.add("1.5117978404420584");
    args.add("0.13741854345756943");
    args.add("0.41879669784643103");
    args.add("-0.9888834421540045");
    arguments = args; //set arguments
    radiusCommand = handler.getMap().get("radius");
    naiveRadiusCommand = handler.getMap().get("naive_radius");
  }

  @Before
  public void setUpEdgeCase2() {
    handler = new StarsHandler();
    ArrayList<String> a = new ArrayList<>();
    a.add("testarg");
    a.add(path3);
    handler.getMap().get("stars").execute(a);
    radiusCommand = handler.getMap().get("neighbors");
    naiveRadiusCommand = handler.getMap().get("naive_neighbors");
    List<String> args = new ArrayList<>();
    args.add("testarg");
    args.add("0.7297276204474863");
    args.add("0.12045551834121704");
    args.add("0.984604653800145");
    args.add("0.7767517898775682");
    arguments = args; //set arguments
    radiusCommand = handler.getMap().get("radius");
    naiveRadiusCommand = handler.getMap().get("naive_radius");
  }

  @After
  public void tearDownRandom() {
    handler = null;
    radiusCommand = null;
    naiveRadiusCommand = null;
    arguments = null;
  }

  /**
   * testing radius function works
   */
  @Test
  public void testStandard() {
    setUpRandom();
    arguments = new ArrayList<>(generator.generateRandomArgument(true, SPACE_MIN, SPACE_RANGE));
    Star tar = handler.findTarget(arguments);
    radiusCommand.execute(arguments);
    ArrayList<Star> standardOutput = new ArrayList<>(handler.getReturnList());
    //check match
    assertTrue(handler.getTotalDistance() == handler.getNaiveTotalDistance());
    for (int i = 0; i < standardOutput.size(); i++) {
      if (i + 1 < standardOutput.size()) {
        assertTrue(standardOutput.get(i).euclideanDistance(tar)
            <= standardOutput.get(i + 1).euclideanDistance(tar));
      }
    }
    tearDownRandom();
  }

  /**
   * testing naive radius functionality
   */
  @Test
  public void testNaive() {
    setUpRandom();
    arguments = new ArrayList<>(generator.generateRandomArgument(true, SPACE_MIN, SPACE_RANGE));
    Star tar = handler.findTarget(arguments);
    naiveRadiusCommand.execute(arguments);
    ArrayList<Star> naiveOutput = new ArrayList<>(handler.getReturnList());
    //check match
    assertTrue(handler.getTotalDistance() == handler.getNaiveTotalDistance());
    for (int i = 0; i < naiveOutput.size(); i++) {
      if (i + 1 < naiveOutput.size()) {
        assertTrue(naiveOutput.get(i).euclideanDistance(tar)
            <= naiveOutput.get(i + 1).euclideanDistance(tar));
      }
    }
    tearDownRandom();
  }

  /**
   * testing radius function works on an edgecase the random generator found
   */
  @Test
  public void testEdgeCase() {
    setUpEdgeCase();
    arguments = new ArrayList<>(generator.generateRandomArgument(true, SPACE_MIN, SPACE_RANGE));
    Star tar = handler.findTarget(arguments);
    radiusCommand.execute(arguments);
    ArrayList<Star> standardOutput = new ArrayList<>(handler.getReturnList());
    naiveRadiusCommand.execute(arguments);
    ArrayList<Star> naiveOutput = new ArrayList<>(handler.getReturnList());
    //check match
    assertTrue(naiveOutput.size() == standardOutput.size());
    assertTrue(handler.getTotalDistance() == handler.getNaiveTotalDistance());
    for (int i = 0; i < naiveOutput.size(); i++) {
      assertTrue(naiveOutput.get(i).euclideanDistance(tar) ==
          standardOutput.get(i).euclideanDistance(tar));
    }
    tearDownRandom();
  }

  /**
   * testing radius function works on an edgecase the random generator found
   */
  @Test
  public void testEdgeCase2() {
    setUpEdgeCase2();
    Star tar = handler.findTarget(arguments);
    radiusCommand.execute(arguments);
    ArrayList<Star> standardOutput = new ArrayList<>(handler.getReturnList());
    naiveRadiusCommand.execute(arguments);
    ArrayList<Star> naiveOutput = new ArrayList<>(handler.getReturnList());
    //check match
    assertTrue(naiveOutput.size() == standardOutput.size());
    assertTrue(handler.getTotalDistance() == handler.getNaiveTotalDistance());
    for (int i = 0; i < naiveOutput.size(); i++) {
      assertTrue(naiveOutput.get(i).euclideanDistance(tar) ==
          standardOutput.get(i).euclideanDistance(tar));
    }
    tearDownRandom();
  }

  /**
   * MBT testing for the edgeCase 1
   */
  @Test
  public void testMatchEdge() {
    setUpEdgeCase();
    Star tar = handler.findTarget(arguments);
    radiusCommand.execute(arguments);
    ArrayList<Star> standardOutput = new ArrayList<>(handler.getReturnList());
    naiveRadiusCommand.execute(arguments);
    ArrayList<Star> naiveOutput = new ArrayList<>(handler.getReturnList());
    //check match
    assertTrue(naiveOutput.size() == standardOutput.size());
    assertTrue(handler.getTotalDistance() == handler.getNaiveTotalDistance());
    for (int i = 0; i < naiveOutput.size(); i++) {
      assertTrue(naiveOutput.get(i).euclideanDistance(tar) ==
          standardOutput.get(i).euclideanDistance(tar));
    }
    tearDownRandom();
  }


  /**
   * MBT test that the two functions return the same output
   */
//  @Test
//  public void testMatch() {
//    setUpRandom();
//    for (int i = 0; i < NUM_TRIALS; i++) {
//      arguments = generator.generateRandomArgument(true, SPACE_MIN, SPACE_RANGE);
//      Star tar = handler.findTarget(arguments);
//      radiusCommand.execute(arguments);
//      ArrayList<Star> standardOutput = new ArrayList<>(handler.getReturnList());
//      naiveRadiusCommand.execute(arguments);
//      ArrayList<Star> naiveOutput = new ArrayList<>(handler.getReturnList());
//      standardOutput.sort(new Comparator<>() {
//        @Override
//        public int compare(Star a, Star b) {
//          if (a.euclideanDistance(tar) < b.euclideanDistance(tar)) {
//            return -1;
//          } else if (a.euclideanDistance(tar) > b.euclideanDistance(tar)) {
//            return 1;
//          } else {
//            return 0;
//          }
//        }
//      });
//      naiveOutput.sort(new Comparator<>() {
//        @Override
//        public int compare(Star a, Star b) {
//          if (a.euclideanDistance(tar) < b.euclideanDistance(tar)) {
//            return -1;
//          } else if (a.euclideanDistance(tar) > b.euclideanDistance(tar)) {
//            return 1;
//          } else {
//            return 0;
//          }
//        }
//      });
//      //check match
//      assertTrue(naiveOutput.size() == standardOutput.size());
//      assertTrue(handler.getTotalDistance() == handler.getNaiveTotalDistance());
//      for (int j = 0; j < naiveOutput.size(); j++) {
//        assertTrue(naiveOutput.get(j).euclideanDistance(tar) ==
//            standardOutput.get(j).euclideanDistance(tar));
//      }
//    }
//    tearDownRandom();
//  }

}