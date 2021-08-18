package edu.brown.cs.student.maps;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertTrue;

//TODO:this JUNIT test covers the correctness of the haversine function. pivotal for the correctness of the program.
/**
 * Testing Graph haversine correctness
 * There is a case where the algorithm searches route from src and find it in actual instead of target
 */
public class GraphNodeTest {
  private GraphNode src;
  private GraphNode correct;
  private GraphNode actual;
  private Database db;

  @Before
  public void setUp() {
    db = new Database("data/maps/maps.sqlite3");
    src = new GraphNode("/n/4187.7176.201435564", 41.872916 ,-71.765908, 0, db);
    correct = new GraphNode("/n/4187.7176.201435559", 41.871471 ,-71.765404, 0, db);
    actual = new GraphNode("/n/4187.7176.201636011", 41.872112 ,-71.765205, 0, db);
    //setup source and target
  }

  @After
  public void tearDown() {
    db = null;
    src = null;
    correct = null;
    actual = null;
  }

  /**
   * Testing graph functionality
   * testing at any node within the return list is closer to target than target (PBT)
   * testing that
   */
  @Test
  public void testHaversine() {
    setUp();
    //test that the return results in what we want
    //testing haversine correctness with external calculator https://www.vcalc.com/wiki/vCalc/Haversine+-+Distance
    //correct distance is actually 0.166007680977053;
    //the algorithm is choosing this dist = 0.106680270690947;
    double correctDistance = src.haversine(correct);
    System.out.println("correct :" + correctDistance);
    double pickedDistance = src.haversine(actual);
    System.out.println("actual :" + pickedDistance);
    assertTrue(correctDistance > pickedDistance);
    tearDown();
  }
}
