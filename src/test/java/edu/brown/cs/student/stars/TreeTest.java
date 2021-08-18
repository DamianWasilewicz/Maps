package edu.brown.cs.student.stars;


import java.util.List;

import edu.brown.cs.student.csvpasrer.CSVParser;
import edu.brown.cs.student.kdtree.KDTree;
import edu.brown.cs.student.kdtree.KDTreeNode;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Testing JUNIT
 */
public class TreeTest {
  private KDTree<Star> tree;
  private CSVParser reader;


  @Before
  public void setUpTie() {
    reader = new CSVParser("data/stars/tie-star.csv", 5);
    List<String[]> body = reader.getBody();
    ArrayList<Star> stars = new ArrayList<>();
    for (String[] line : body) {
      Star star = new Star(Integer.parseInt(line[0]), line[1]
          , Double.parseDouble(line[2]), Double.parseDouble(line[3]), Double.parseDouble(line[4]));
      stars.add(star);
    }
    //returns an ordered list, builds a tree.
    tree = new KDTree<>(stars, 3);
  }

  @Before
  public void setUpTen() {
    reader = new CSVParser("data/stars/ten-star.csv", 5);
    List<String[]> body = reader.getBody();
    ArrayList<Star> stars = new ArrayList<>();
    for (String[] line : body) {
      Star star = new Star(Integer.parseInt(line[0]), line[1]
          , Double.parseDouble(line[2]), Double.parseDouble(line[3]), Double.parseDouble(line[4]));
      stars.add(star);
    }
    //returns an ordered list, builds a tree.
    tree = new KDTree<>(stars, 3);
  }

  @Before
  public void setEdgeTree() {
    reader = new CSVParser("data/stars/edgeCase.csv", 5);
    List<String[]> body = reader.getBody();
    ArrayList<Star> stars = new ArrayList<>();
    for (String[] line : body) {
      Star star = new Star(Integer.parseInt(line[0]), line[1]
          , Double.parseDouble(line[2]), Double.parseDouble(line[3]), Double.parseDouble(line[4]));
      stars.add(star);
    }
    //returns an ordered list, builds a tree.
    tree = new KDTree<>(stars, 3);
  }

  @Before
  public void setUpTestCase() {
    reader = new CSVParser("data/stars/kd-nine-stars.csv", 5);
    List<String[]> body = reader.getBody();
    ArrayList<Star> stars = new ArrayList<>();
    for (String[] line : body) {
      Star star = new Star(Integer.parseInt(line[0]), line[1]
          , Double.parseDouble(line[2]), Double.parseDouble(line[3]), Double.parseDouble(line[4]));
      stars.add(star);
    }
    //returns an ordered list, builds a tree.
    tree = new KDTree<>(stars, 3);
  }

  @After
  public void tearDown() {
    tree = null;
  }

  /**
   * Testing if tree builds correctly on tied tree.
   */
  @Test
  public void testTieTree() {
    setUpTie();
    KDTreeNode<Star> parent = tree.getRoot();
    while (!(parent.getRightChild() == null)) {
      assertTrue(parent.getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions())
          <= parent.getRightChild().getElement().getCoordinate(parent.getDepth() % parent.getElement()
              .numDimensions()));
      parent = parent.getRightChild(); //update parent pointer
    }
    while (!(parent.getLeftChild() == null)) {
      assertTrue(parent.getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions())
          >= parent.getLeftChild().getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions()));
      parent = parent.getLeftChild();
    }
    tearDown();
  }
  /**
   * Testing if tree builds correctly on reg tree.
   */
  @Test
  public void testTenTree() {
    setUpTen();
    KDTreeNode<Star> parent = tree.getRoot();
    while (!(parent.getRightChild() == null)) {
      assertTrue(parent.getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions())
          <= parent.getRightChild().getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions()));
      parent = parent.getRightChild(); //update parent pointer
    }
    while (!(parent.getLeftChild() == null)) {
      assertTrue(parent.getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions())
          >= parent.getLeftChild().getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions()));
      parent = parent.getLeftChild();
    }
    tearDown();
  }
  /**
   * Testing if tree builds correctly on edge case.
   */
  @Test
  public void testEdgeTree() {
    setEdgeTree();
    KDTreeNode<Star> parent = tree.getRoot();
    while (!(parent.getRightChild() == null)) {
      assertTrue(parent.getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions())
          <= parent.getRightChild().getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions()));
      parent = parent.getRightChild(); //update parent pointer
    }
    while (!(parent.getLeftChild() == null)) {
      assertTrue(parent.getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions())
          >= parent.getLeftChild().getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions()));
      parent = parent.getLeftChild();
    }
    tearDown();
  }
  /**
   * Testing if tree builds correctly on basic case.
   */
  @Test
  public void testTreeBuild() {
    setUpTestCase();
    KDTreeNode<Star> parent = tree.getRoot();
    while (!(parent.getRightChild() == null)) {
      assertTrue(parent.getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions())
          <= parent.getRightChild().getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions()));
      parent = parent.getRightChild(); //update parent pointer
    }
    while (!(parent.getLeftChild() == null)) {
      assertTrue(parent.getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions())
          >= parent.getLeftChild().getElement().getCoordinate(parent.getDepth() % parent.getElement()
          .numDimensions()));
      parent = parent.getLeftChild();
    }
    tearDown();
  }

  /**
   * Distance helper method
   * @param a one star
   * @param b another star
   * @return the euclidean distance
   */
  public Double distance(Star a, Star b) {
    double total = 0;
    for (int i = 0; i < a.numDimensions(); i++) {
      total = total
          + Math.pow(a.getCoordinate(i) - b.getCoordinate(i), 2);
    }
    return Math.sqrt(total);
  }

  /**
   * Testing if radius works.
   */
  @Test
  public void testRadius() {
    setUpTen();
    Star star = new Star(0, "Sol", 0, 0, 0);
    List<Star> returnList = tree.radius(5, star);
    for (Star i: returnList) {
      assertTrue(distance(star,i) <= 5);
    }
    tearDown();
  }

  /**
   * Testing if neighbors command works.
   */
  @Test
  public void testNeighbors() {
    setUpTen();
    Star star = new Star(0, "Sol", 0, 0, 0);
    List<Star> returnList = tree.neighbors(5, star,false);
    //returns in order?
    Double dis = 0.0;
    for (Star i: returnList){
      assertTrue(distance(star, i) >= dis);
      dis = distance(star, i);
    }
    tearDown();
  }

  /**
   * Testing getroot
   */
  @Test
  public void testGetRoot() {
    setUpTen();
    assertEquals(0, (double) tree.getRoot().getElement().getCoordinate(0), 0.0);
    tearDown();
  }


}
