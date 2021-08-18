package edu.brown.cs.student.stars;

import edu.brown.cs.student.logic.StarsHandler;
import edu.brown.cs.student.logic.Commandable;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class StarsHandlerTest {
  //objects
  private StarsHandler handler;
  private Commandable starsCommand;
  private Commandable neighborsCommand;
  private Commandable naiveNeighborsCommand;
  private Commandable radiusCommand;
  private Commandable naiveRadiusCommand;

  /*
   * Before calls.
   */
  @Before
  public void setUpBasic() {
    handler = new StarsHandler();
    starsCommand = handler.getMap().get("stars");
    neighborsCommand = handler.getMap().get("neighbors");
    naiveNeighborsCommand = handler.getMap().get("naive_neighbors");
    radiusCommand = handler.getMap().get("radius");
    naiveRadiusCommand = handler.getMap().get("naive_radius");
  }

  /*
   * After calls.
   */
  @After
  public void tearDownBasic() {
    handler = null;
    starsCommand = null;
    radiusCommand = null;
    neighborsCommand = null;
    naiveNeighborsCommand = null;
    naiveRadiusCommand = null;
  }

  /**
   * Testing Hashmap implementation
   */
  @Test
  public void testCommandMap() {
    setUpBasic();
    assertTrue(handler.getMap().containsValue(starsCommand));
    assertTrue(handler.getMap().containsValue(neighborsCommand));
    assertTrue(handler.getMap().containsValue(naiveNeighborsCommand));
    assertTrue(handler.getMap().containsValue(radiusCommand));
    assertTrue(handler.getMap().containsValue(naiveRadiusCommand));
    tearDownBasic();
  }

}
