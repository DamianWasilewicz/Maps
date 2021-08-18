package edu.brown.cs.student.maps;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class DatabaseTest {

  Database db;

  @Before
  public void setUp() {
    db = new Database("data/maps/smallMaps.sqlite3");
  }

  @After
  public void tearDown() {
    db = null;
  }

  @Test
  public void testQueryNodes() {
    List<GraphNode> result = db.queryTraversableNodes();
    assertTrue(result.size() == 6);
  }
}
