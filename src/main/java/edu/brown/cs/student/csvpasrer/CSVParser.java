package edu.brown.cs.student.csvpasrer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class models a generic csv parser that takes in a csv file and
 * processes it into an ArrayList of String[]s, each representing a parsed lines.
 */
public class CSVParser {
  private boolean validFile;
  private final List<String[]> parsedData;

  /**
   * The constructor processes a file's contents into a list of string arrays,
   * such that each represents a parsed line.
   *
   * @param filePath to desired csv.
   * @param expectation number of fields in a tuple
   */
  public CSVParser(String filePath, int expectation) {
    validFile = true;
    parsedData = new ArrayList<>();
    BufferedReader csvReader; //set up csv reader
    try {
      csvReader = new BufferedReader(new FileReader(filePath));
    } catch (Exception e) {
      validFile = false;
      System.out.println("ERROR: invalid filepath.");
      return;
    }
    String line; //read file data.
    try {
      line = csvReader.readLine();
    } catch (Exception e) {
      System.out.println("ERROR: bufferedReader readLine() failed.");
      return;
    }
    //iterate through all lines of file
    while (line != null) {
      String[] parsedLine = line.split(",");
      if (parsedLine.length != expectation) {
        System.out
            .println("ERROR: malformed line in file, expected " + expectation + " but found "
                + parsedLine.length + " fields.");
        validFile = false;
        return;
      }
      //add the parsed lines into the dataCollection
      parsedData.add(parsedLine);
      try {
        line = csvReader.readLine(); //read the next line to continue the loop.
      } catch (Exception e) {
        System.out.println("ERROR: bufferedReader readLine() failed.");
        return;
      }
    }
    //done with reading, close br
    try {
      csvReader.close();
    } catch (Exception e) {
      System.out.println("ERROR: bufferedReader failed to close.");
      return;
    }
  }

  /**
   * Method checking if file invalid.
   *
   * @return boolean.
   */
  public boolean isValidFile() {
    return validFile;
  }

  /**
   * Method returning the header of the csv file.
   *
   * @return header of csv as a string array.
   */
  public String[] getHeader() {
    return parsedData.get(0);
  }

  /**
   * Method returning csv body.
   *
   * @return csv content as string arrays.
   */
  public List<String[]> getBody() {
    return new ArrayList<>(parsedData.subList(1, parsedData.size()));
  }
}
