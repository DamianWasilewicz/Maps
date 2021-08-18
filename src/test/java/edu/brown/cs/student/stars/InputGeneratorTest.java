package edu.brown.cs.student.stars;

import edu.brown.cs.student.logic.StarsHandler;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;


public class InputGeneratorTest {
  private static final int MAX_INPUT_SIZE = 10;
  private static final int SPACE_MIN = -10;
  private static final int SPACE_RANGE = 20;
  private RandomInputGenerator generator;


  @Before
  public void setGenerator() {
    generator = new RandomInputGenerator();
  }

  @After
  public void tearDown() {
    generator = null;
  }

  /**
   * testing the generator generates valid inputs
   */
  @Test
  public void testGeneratedInput() {
    setGenerator();
    ArrayList<Star> inputList
        = new ArrayList<>(generator.generateRandomInputs(MAX_INPUT_SIZE, SPACE_MIN, SPACE_RANGE));
    //check everything is generated in bound
    for (Star s : inputList) {
      for (int i = 0; i < inputList.get(0).numDimensions(); i++) {
        assertTrue(Math.abs(s.getCoordinate(i)) <= Math.abs(SPACE_MIN));
      }
    }
    assertTrue(inputList.size() <= MAX_INPUT_SIZE);
    tearDown();
  }

  /**
   * testing the generator generates valid arguments
   */
  @Test
  public void testGeneratedArgument() {
    setGenerator();
    generator.generateRandomInputs(MAX_INPUT_SIZE, SPACE_MIN, SPACE_RANGE);
    //test radius
    ArrayList<String> rArgs
        = new ArrayList<>(generator.generateRandomArgument(true, SPACE_MIN, SPACE_RANGE));
    StarsHandler handler = new StarsHandler();
    assertTrue(handler.validArguments(true, rArgs));
    //test neighbors
    ArrayList<String> nArgs
        = new ArrayList<>(generator.generateRandomArgument(false, SPACE_MIN, SPACE_RANGE));
    assertTrue(handler.validArguments(false, nArgs));
    tearDown();
  }



}
