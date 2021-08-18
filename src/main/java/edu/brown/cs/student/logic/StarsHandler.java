package edu.brown.cs.student.logic;

import edu.brown.cs.student.csvpasrer.CSVParser;
import edu.brown.cs.student.kdtree.KDTree;
import edu.brown.cs.student.stars.Star;

import java.math.BigDecimal;
import java.util.*;

public class StarsHandler {

  private ArrayList<Star> starList;
  private final HashMap<String, Commandable> commandMap;
  private static KDTree<Star> kdtree = null;
  private final HashMap<String, Star> starMap;
  private BigDecimal totalDistance;
  private final BigDecimal naiveTotalDistance;
  private ArrayList<Star> returnList;

  public StarsHandler() {
    starList = new ArrayList<>();
    starMap = new HashMap<>();
    commandMap = new HashMap<>();
    this.fillMap();

    //for testing
    totalDistance = BigDecimal.ZERO;
    naiveTotalDistance = BigDecimal.ZERO;
    returnList = new ArrayList<>();
  }

  public void fillMap() {
    commandMap.put("stars", new Stars());
    commandMap.put("naive_radius", new NaiveRadius());
    commandMap.put("naive_neighbors", new NaiveNeighbors());
    commandMap.put("radius", new Radius());
    commandMap.put("neighbors", new Neighbors());
  }

  /**
   * Command Hashmap getter.
   *
   * @return the hashmap used to map strings to commmands
   */
  public HashMap<String, Commandable> getMap() {
    return commandMap;
  }


  public Set<HashMap.Entry<String, Commandable>> getCommands() {
    return commandMap.entrySet();
  }


  /**
   * starList setter.
   * This method is for testing.
   *
   * @param list the new object starList will take
   */
  public void setStarList(ArrayList<Star> list) {
    starList = new ArrayList<>(list);
  }

  /**
   * tree setter.
   * This method is for testing.
   *
   * @param t is now the tree.
   */
  public void setTree(KDTree<Star> t) {
    kdtree = t;
  }

  /**
   * tree getter.
   * This method is for testing.
   *
   * @return the kdtree of the handler
   */
  public KDTree<Star> getTree() {
    return kdtree;
  }

  /**
   * getter for testing.
   *
   * @return a defensive copy of the return list.
   */
  public ArrayList<Star> getReturnList() {
    return returnList;
  }

  /**
   * getter for testing.
   *
   * @return reference to total distance from target calculated during naive search.
   */
  public BigDecimal getNaiveTotalDistance() {
    return naiveTotalDistance;
  }

  /**
   * getter for testing.
   *
   * @return reference to total distance from target calculated during kdtree search.
   */
  public BigDecimal getTotalDistance() {
    return totalDistance;
  }

  /**
   * Passing having to return a list in the execute method. Returns an output array of stars to
   * be displayed by the GUI.
   *
   * @return an array of stars according to specified search
   */
  public ArrayList<Integer> getOutputStars() {
    ArrayList<Integer> starIDsToPrint = new ArrayList<>();
    for (Star s : returnList) {
      starIDsToPrint.add(s.getID());
    }
    return starIDsToPrint;
  }

  /**
   * Helper method checking if input command is valid.
   *
   * @param isRadius
   *      is true for radius and false for neighbors
   * @param argument
   *      list of args
   * @return
   *      True, if args are valid. False otherwise.
   */
  public boolean validArguments(Boolean isRadius, List<String> argument) {
    // check number of arguments
    if (argument.size() != 3 && argument.size() != 5) {
      System.out.println(
        "ERROR: invalid number of arguments, expected 2 or 4 but was "
          + (argument.size() - 1) + ".");
      return false;
    }

    // check if k or r are valid
    // TODO (readability): make regex's constants so it's easier to read?
    if (isRadius) {
      if (!argument.get(1).matches("^[+]?[0-9]\\d*(?:\\.?\\d+)?$")) {
        System.out.println("ERROR: r must be non-negative for command radius.");
        return false;
      }
    } else {
      if (!argument.get(1).matches("^\\d+$|0$")) {
        System.out.println("ERROR: k must be non-negative integer for command neighbors.");
        return false;
      }
    }

    //case 5 arguments (coordinates)
    if (argument.size() == 5) {
      if (!argument.get(2).matches("^[-+]?[0-9]\\d*(?:\\.?\\d+)?|0$")) {
        System.out.println("ERROR: invalid x argument for command radius.");
        return false;
      }
      if (!argument.get(3).matches("^[-+]?[0-9]\\d*(?:\\.?\\d+)?|0$")) {
        System.out.println("ERROR: invalid y argument for command radius.");
        return false;
      }
      if (!argument.get(4).matches("^[-+]?[0-9]\\d*(?:\\.?\\d+)?|0$")) {
        System.out.println("ERROR: invalid z argument for command radius.");
        return false;
      }
      //case 3 arguments (name)
    } else {
      if (!argument.get(2).matches("^\"(\\.|[^\"])*\"$")) {
        System.out.println("ERROR: invalid name argument for command radius.");
        return false;
      }
    }

    return true;
  }

  /**
   * Helper method producing target to run search algorithms on.
   *
   * @param argument
   *      input from repl
   * @return
   *      a star to input for the search command.
   */
  public Star findTarget(List<String> argument) {
    Star target = null;
    if (argument.size() == 5) {
      //processing the arguments for readability
      double x = Double.parseDouble(argument.get(2));
      double y = Double.parseDouble(argument.get(3));
      double z = Double.parseDouble(argument.get(4));
      target = new Star(0, "target", x, y, z);
    }
    if (argument.size() == 3) {
      String name = argument.get(2);
      name = name.replace("\"", "");
      if (!starMap.containsKey(name)) {
        System.out.println("ERROR: couldn't find given star name in file.");
      } else {
        target = starMap.get(name);
      }
    }
    return target;
  }


  /**
   * Class Stars implements the commandable interface to execute().
   */
  private final class Stars implements Commandable {
    /**
     * Constructor.
     */
    private Stars() {
    }

    /**
     * Command method reads the given file path using a CSVParser object and creates a list of
     * stars that will be used throughout the program.
     *
     * @param argument passed from the repl
     */
    @Override
    public void execute(List<String> argument) {
      //remember that [0] is the command line
      if (argument.size() != 2) {
        System.out.println(
          "ERROR: invalid argument for command stars.");
        return;
      }
      //reset references
      starList.clear();
      starMap.clear();
      //set up the CSV parser
      CSVParser reader = new CSVParser(argument.get(1), 5);
      //check if file is valid
      if (!reader.isValidFile()) {
        //stop in case file is invalid
        return;
      }
      if (!this.validHeader(reader.getHeader())) {
        System.out.println("ERROR: invalid file header.");
        return;
      }
      if (reader.getBody() == null) {
        System.out.println("ERROR: file contains no stars.");
        return;
      }
      List<String[]> starData = reader.getBody();
      //translate the data into stars
      for (String[] line : starData) {
        //storing the in a star object
        int id = Integer.parseInt(line[0]);
        String name = line[1];
        double x = Double.parseDouble(line[2]);
        double y = Double.parseDouble(line[3]);
        double z = Double.parseDouble(line[4]);
        Star starObject = new Star(id, name, x, y, z);
        //add stars to the starList, pushing reference and name to a map
        starList.add(starObject);
        starMap.put(starObject.getName(), starObject);
      }
      //print out a statement to the REPL.
      System.out.println("Read " + starList.size() + " stars from " + argument.get(1));
      //construct a new instance of your 3DTree
      kdtree = new KDTree<>(starList, 3);
    }

    /**
     * Helper method verifies csv header is valid.
     *
     * @param parsedHeader a string array representing the content of the header
     * @return boolean
     */
    public boolean validHeader(String[] parsedHeader) {
      return (parsedHeader[0].equals("StarID"))
        && (parsedHeader[1].equals("ProperName"))
        && (parsedHeader[2].equals("X"))
        && (parsedHeader[3].equals("Y"))
        && (parsedHeader[4].equals("Z"));
    }
  }

  /**
   * Class Radius implements the commandable interface to execute().
   */
  private final class Radius implements Commandable {
    /**
     * Constructor.
     */
    private Radius() {
    }

    /**
     * Command method runs a radius search on a KDTree to find stars within range to a
     * target star.
     *
     * @param argument typed into the repl
     */
    @Override
    public void execute(List<String> argument) {
      //check if argument string contains valid argument inputs
      if (!validArguments(true, argument)) {
        return;
      }
      //radius search requires a radius and a target to search around
      double r = Double.parseDouble(argument.get(1));
      Star target = findTarget(argument);
      if (target == null) { //return if target is null
        return;
      }
      List<Star> outputList = new ArrayList<>(kdtree.radius(r, target));
      //reset testing references:
      totalDistance = BigDecimal.ZERO;
      ArrayList<Star> output = new ArrayList<>();
      //print the outputs:
      for (Star s : outputList) {
        //update testing references and print results to REPL
        BigDecimal dist = BigDecimal.valueOf(s.euclideanDistance(target));
        totalDistance.add(dist);
        output.add(s);
        System.out.println(s.getID());
      }
      returnList = new ArrayList<>(output);
    }
  }

  /**
   * Class Neighbors implements the commandable interface to execute().
   */
  private final class Neighbors implements Commandable {
    /**
     * Constructor.
     */
    private Neighbors() {
    }

    /**
     * Command method runs neighbors search on a kdtree to find the k nearest neighbors to a
     * given target, randomize selection of tied neighbors.
     *
     * @param argument passed in from the repl
     */
    @Override
    public void execute(List<String> argument) {
      //check if argument string contains valid argument inputs
      if (!validArguments(false, argument)) {
        return;
      }
      //neighbors requires k neighbors objective and a target.
      int k = Integer.parseInt(argument.get(1));
      Star target = findTarget(argument);
      if (target == null) {
        return;
      }
      List<Star> neighborsList;
      if (argument.size() == 3) {
        neighborsList = kdtree.neighbors(k, target, true);
      } else {
        neighborsList = kdtree.neighbors(k, target, false);
      }
      totalDistance = BigDecimal.ZERO;
      ArrayList<Star> output = new ArrayList<>();
      //this stage I have r and I have target
      for (Star s : neighborsList) {
        BigDecimal dist = BigDecimal.valueOf(s.euclideanDistance(target));
        totalDistance.add(dist);
        output.add(s);
        System.out.println(s.getID());
      }
      returnList = new ArrayList<>(output);
    }
  }

  /**
   * Class NaiveRadius implements the commandable interface to execute().
   */
  private final class NaiveRadius implements Commandable {
    /**
     * Constructor.
     */
    private NaiveRadius() {
    }

    /**
     * Command method.
     *
     * @param argument passed in from the repl
     */
    @Override
    public void execute(List<String> argument) {
      //check if argument string contains valid argument inputs
      if (!validArguments(true, argument)) {
        return;
      }
      //find radius
      double r = Double.parseDouble(argument.get(1));
      //find target
      Star target = findTarget(argument);
      if (target == null) {
        return;
      }
      ArrayList<Star> copyList = new ArrayList<>(starList);
      copyList.sort(new Comparator<>() {
        @Override
        public int compare(Star a, Star b) {
          if (a.euclideanDistance(target) < b.euclideanDistance(target)) {
            return -1;
          } else if (a.euclideanDistance(target) > b.euclideanDistance(target)) {
            return 1;
          } else {
            return 0;
          }
        }
      });
      if (argument.size() == 3) {
        copyList.remove(0);
      }
      //reset testing references:
      totalDistance = BigDecimal.ZERO;
      returnList.clear();
      //iterates through sorted starList
      ArrayList<Star> output = new ArrayList<>();
      int index = 0;
      while (target.euclideanDistance(copyList.get(index)) <= r) {
        BigDecimal dis = BigDecimal.valueOf(target.euclideanDistance(copyList.get(index)));
        totalDistance.add(dis); //incrementing total distance
        output.add(copyList.get(index));
        //print IDs
        System.out.println(copyList.get(index).getID());
        index++;
        if (index >= copyList.size()) {
          break;
          //to avoid index out of bounds
        }
      }
      returnList = new ArrayList<>(output);
    }
  }

  /**
   * Class NaiveNeighbors implements the commandable interface to execute().
   */

  private final class NaiveNeighbors implements Commandable {
    /**
     * Constructor.
     */
    private NaiveNeighbors() {
    }

    /**
     * Command method.
     *
     * @param argument passed in from repl
     */
    @Override
    public void execute(List<String> argument) {
      //check if argument string contains valid argument inputs
      if (!validArguments(false, argument)) {
        return;
      }
      //find number of neighbors
      int k = Integer.parseInt(argument.get(1));
      //find target
      Star target = findTarget(argument);
      if (target == null) {
        return;
      }
      //sorting using an anon comparator
      starList.sort((a, b) -> {
        if (a.euclideanDistance(target) < b.euclideanDistance(target)) {
          return -1;
        } else if (a.euclideanDistance(target) > b.euclideanDistance(target)) {
          return 1;
        } else {
          return 0;
        }
      });
      //reset testing references:
      totalDistance = BigDecimal.ZERO;
      returnList.clear();
      int start = 0;
      int finish = k;
      if (starList.get(0).getName().equals(target.getName())) {
        start = 1;
        finish = k + 1;
      }
      //edge case
      if ((starList.get(0).getName().equals(target.getName()))
        && (starList.size() == 1)) {
        return;
      }
      ArrayList<Star> outputlist = new ArrayList<>();
      //randomize equidistant neighbors
      for (int i = start; i < finish; i++) {
        // so long as the star is not the name and there is more than one star.
        ArrayList<Star> container = new ArrayList<>();
        container.add(starList.get(i));
        int j = i;
        //avoid java.lang.IndexOutOfBoundsException
        while ((j + 1 <= starList.size() - 1)
          && (target.euclideanDistance(starList.get(j))
          == target.euclideanDistance(starList.get(j + 1)))) {
          container.add(starList.get(j + 1));
          j++;
        }
        Star output = container.get(this.randomIndex(container.size() - 1));
        container.clear(); // reset the container structure for next use
        System.out.println(output.getID());
        BigDecimal dis = BigDecimal.valueOf(target.euclideanDistance(output));
        totalDistance.add(dis); //incrementing total distance
        outputlist.add(output);
      }
      returnList = new ArrayList<>(outputlist);
    }

    /**
     * Random index generator.
     *
     * @param bound of the desired random number
     * @return random int
     */
    private int randomIndex(int bound) {
      if (0 > bound) {
        System.out.println("ERROR: bound should be greater than 0.");
      }
      return new Random().nextInt(bound + 1);
    }
  }





}
