package edu.brown.cs.student.stars;

import java.util.ArrayList;
import java.util.Random;

/**
 * This random input generator object will help feed my Junit tests with random inputs.
 */
public class RandomInputGenerator {
  private ArrayList<Star> inputListReference;
  private static final int NAME_LENGTH = 7;

  /**
   * constructor.
   */
  public RandomInputGenerator() {
    inputListReference = new ArrayList<>();
  }

  /**
   * Generates a random input list of arbitrary stars.
   *
   * @param listSize the maximum size of a desired input list
   * @param min of space bound
   * @param range of space bound
   * @return an array list of arbitrary stars.
   */
//  public ArrayList<Star> generateRandomInputs(int listSize, int min, int range) {
//    ArrayList<Star> randomInputList = new ArrayList<>();
//    ArrayList<String> usedNamesList = new ArrayList<>();
//    int inputSize = new Random().nextInt(listSize);
//    //an empty list is handled by the csv parser
//    if (inputSize == 0) {
//      inputSize = 1;
//    }
//    //create random stars.
//    for (int i = 0; i < inputSize; i++) {
//      //next generate a unique name
//      //reference = (https://stackoverflow.com/questions/5025651/java-randomly-generate-distinct-names)
//      StringBuilder builder = new StringBuilder();
//      String starName = null;
//      int nameLength = new Random().nextInt(NAME_LENGTH);
//      if (nameLength == 0) {
//        builder.append(""); //empty string
//        starName = builder.toString();
//      }
//      if (nameLength > 0) {
//        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        builder.append(uppercase.charAt(new Random().nextInt(uppercase.length())));
//        for (int j = 1; j < nameLength; j++) {
//          String lowercase = "abcdefghijklmnopqrstuvwxyz";
//          builder.append(lowercase.charAt(new Random().nextInt(lowercase.length())));
//        }
//        String name = builder.toString();
//        if (!(usedNamesList.contains(name))) {
//          usedNamesList.add(name);
//          starName = name; //store unique name
//        } else {
//          starName = "";
//        }
//      }
//      double x = (Math.random() * range) + min;
//      double y = (Math.random() * range) + min;
//      double z = (Math.random() * range) + min;
//      Star randomStar = new Star(i, starName, x, y, z);
//      randomInputList.add(randomStar);
//    }
//    inputListReference = new ArrayList<>(randomInputList);
//    return randomInputList;
//  }
  public ArrayList<Star> generateRandomInputs(int listSize, int min, int range) {
    ArrayList<Star> randomInputList = new ArrayList<>();
    ArrayList<String> usedNamesList = new ArrayList<>();
    int inputSize = new Random().nextInt(listSize);
    //an empty list is handled by the csv parser
    if (inputSize == 0) {
      inputSize = 1;
    }
    //create random stars.
    for (int i = 0; i < inputSize; i++) {
      //next generate a unique name
      //reference = (https://stackoverflow.com/questions/5025651/java-randomly-generate-distinct-names)
      StringBuilder builder = new StringBuilder();
      String starName = null;
      int nameLength = new Random().nextInt(NAME_LENGTH);
      if (nameLength == 0) {
        builder.append(""); //empty string
        starName = builder.toString();
      }
      if (nameLength > 0) {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        builder.append(uppercase.charAt(new Random().nextInt(uppercase.length())));
        for (int j = 1; j < nameLength; j++) {
          String lowercase = "abcdefghijklmnopqrstuvwxyz";
          builder.append(lowercase.charAt(new Random().nextInt(lowercase.length())));
        }
        String name = builder.toString();
        if (!(usedNamesList.contains(name))) {
          usedNamesList.add(name);
          starName = name; //store unique name
        } else {
          starName = "";
        }
      }
      double x = (int) (Math.random() * range) + min;
      double y = (int) (Math.random() * range) + min;
      double z = (int) (Math.random() * range) + min;
      Star randomStar = new Star(i, starName, x, y, z);
      randomInputList.add(randomStar);
    }
    inputListReference = new ArrayList<>(randomInputList);
    return randomInputList;
  }

  /**
   * Generates a random argument based on the random input list generated.
   *
   * @param radiusCommand a boolean differentiating between radius and neighbors calls
   * @param min of bound
   * @param range of bound
   * @return string list
   */
  public ArrayList<String> generateRandomArgument(Boolean radiusCommand, int min, int range) {
    ArrayList<String> args = new ArrayList<>();
    int bound = 1;
    if (inputListReference != null) {
      bound = inputListReference.size();
    }
    args.add("mock argument");
    if (!radiusCommand) {
      args.add(String.valueOf(new Random().nextInt(bound))); //k
    } else {
      args.add(String.valueOf((Math.random() * range))); //r
    }
    while (args.size() < 5) {
      args.add(String.valueOf((Math.random() * range) + min));
    }
    return args;
  }


}
