package edu.brown.cs.student.logic;


import edu.brown.cs.student.maps.CheckinThread;
import edu.brown.cs.student.maps.UserCheckin;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that runs the repl.
 */
public class Repl {

  private HashMap<String, Commandable> dictionary;
  private static CheckinThread userCheckin;
  private boolean loadedDB = false; //set initial value of boolean to false
  private boolean checkinCreated = false; //set initial value of boolean to false
  private MapsHandler mapsHandler;

  /**
   * Constructor for Repl class, which instantiates the hashmap.
   */
  public Repl() {
    dictionary = new HashMap<>();
  }

  /**
   * The repl that is run in Main.java.
   * A BufferedReader is instantiated to read from standard in.
   * An infinite while loop waits for input from standard in. When input is received,
   * it's parsed into a list of tokens based on whitespace and quotation marks
   * (should they appear) with a regex. Once the input is parsed, the command (the first token)
   * is hashed into dictionary which maps it to a Callable object based on the command.
   * The command is executed by calling run() on the Callable with a subarray of the arguments.
   * The repl breaks out of the infinite while loop for EOF (or ctrl-D).
   */
  public void runRepl() {
    BufferedReader in = new BufferedReader(new FileReader(FileDescriptor.in));
    while (true) {
      //Run the userCheckinThread because GDPR is hot
//      if (loadedDB){
//        if (!checkinCreated) { //create a single instance
//          userCheckin = new CheckinThread(mapsHandler.getDB());
//          checkinCreated = true;
//          userCheckin.start();
//        }
//        System.out.println("SUCCESS");
//      }
      // reading and error checking the input
      String input;
      try {
        input = in.readLine();
      } catch (Exception IOException) {
        System.err.println("ERROR: REPL read failed");
        return;
      }

      // if it's EOF, break out of while loop
      if (input == null) {
        break;
      }

      // if it's not EOF, parse through the input into a list of tokens
      // regex source: https://bit.ly/36o3ghD
      List<String> parsedBuffer = new ArrayList<String>();
      Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
      while (m.find()) {
        parsedBuffer.add(m.group(1));
      }
      // if the input is just a newline
      if (parsedBuffer.size() == 0) {
        continue;
      }
      String command = parsedBuffer.get(0);
      if (dictionary.containsKey(command)) {
        // loops through input and copies arguments into another array
        List<String> args = new ArrayList<>(parsedBuffer);
        dictionary.get(command).execute(args);
        if (command.equals("map")) {
//          System.out.println("calling maps");
          loadedDB = true;
        }
      } else {
        System.err.println("ERROR: invalid command");
      }
    }
  }

  public void addCommands(String command, Commandable commandable) {
    dictionary.put(command, commandable);
  }
  public void addMapHandler(MapsHandler handler){
    mapsHandler = handler;
  }

  public Map<Double, UserCheckin> getRecentCheckin() {
    return userCheckin.getLatestCheckins();
  }
  public boolean isLoadedDB(){
    return loadedDB;
  }
}















