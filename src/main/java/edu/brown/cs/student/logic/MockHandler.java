package edu.brown.cs.student.logic;

import edu.brown.cs.student.mock.MockPerson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class MockHandler {
  private final HashMap<String, Commandable> commandMap;

  public MockHandler() {
    commandMap = new HashMap<>();
    this.fillMap();
  }

  private void fillMap() {
    commandMap.put("mock", new Mock());
  }

  public HashMap<String, Commandable> getMap() {
    return commandMap;
  }

  public Set<HashMap.Entry<String, Commandable>> getCommands() {
    return commandMap.entrySet();
  }

  /**
   * Class Mock implements the commandable interface to execute().
   */
  private final class Mock implements Commandable {
    private final ArrayList<MockPerson> mockList;

    /**
     * Constructor.
     */
    private Mock() {
      mockList = new ArrayList<>();
    }

    /**
     * Command method.
     *
     * @param argument passed in repl
     */
    @Override
    public void execute(List<String> argument) {
      BufferedReader csvReader;
      //remember that [0] is the command line, the argument is at index [1] then
      if (argument.size() != 2) {
        System.out.println(
          "ERROR: invalid argument for command mock.");
        return;
      }
      String filePath = argument.get(1);
      //set up csv reader
      try {
        csvReader = new BufferedReader(new FileReader(filePath));
      } catch (Exception e) {
        System.out.println("ERROR: invalid filepath for command mock.");
        return;
      }
      String header;
      try {
        header = csvReader.readLine();
      } catch (Exception e) {
        System.out.println("ERROR: bufferedReader readLine() failed for file header.");
        return;
      }
      //check if file is valid
      if (this.validHeader(header)) {
        String line;
        try {
          line = csvReader.readLine();
        } catch (Exception e) {
          System.out.println("ERROR: bufferedReader readLine() failed for file body.");
          return;
        }
        while (line != null) {
          String[] parsedLine = line.split(",");
          MockPerson person = new MockPerson("N/A", "N/A", "N/A", "N/A", "N/A", "N/A");
          if (parsedLine.length == 1) {
            person.setDatetime(check(parsedLine[0]));
          }
          if (parsedLine.length == 2) {
            person.setDatetime(check(parsedLine[0]));
            person.setFirstname(check(parsedLine[1]));
          }
          if (parsedLine.length == 3) {
            person.setDatetime(check(parsedLine[0]));
            person.setFirstname(check(parsedLine[1]));
            person.setLastname(check(parsedLine[2]));
          }
          if (parsedLine.length == 4) {
            person.setDatetime(check(parsedLine[0]));
            person.setFirstname(check(parsedLine[1]));
            person.setLastname(check(parsedLine[2]));
            person.setEmail(check(parsedLine[3]));
          }
          if (parsedLine.length == 5) {
            person.setDatetime(check(parsedLine[0]));
            person.setFirstname(check(parsedLine[1]));
            person.setLastname(check(parsedLine[2]));
            person.setEmail(check(parsedLine[3]));
            person.setGender(check(parsedLine[4]));
          }
          if (parsedLine.length == 6) {
            person.setDatetime(check(parsedLine[0]));
            person.setFirstname(check(parsedLine[1]));
            person.setLastname(check(parsedLine[2]));
            person.setEmail(check(parsedLine[3]));
            person.setGender(check(parsedLine[4]));
            person.setStreetAddress(check(parsedLine[5]));
          }
          mockList.add(person);

          try {
            line = csvReader.readLine();
          } catch (Exception e) {
            System.out.println("ERROR: bufferedReader readLine() failed for file body.");
            return;
          }
        }
        //manipulate the data to check if valid
        for (MockPerson p : mockList) {
          System.out.println("Datetime: " + p.getDatetime() + ", First Name: " + p.getFirstname()
            + ", Last Name: " + p.getLastname() + ", Email: " + p.getEmail() + ", Gender: "
            + p.getGender() + ", Street Adress: " + p.getStreetAddress());
        }
        try {
          csvReader.close();
        } catch (Exception e) {
          System.out.println("ERROR: bufferedReader failed to close.");
        }
      } else { //could potentially extract this segment of the code to the valid method
        System.out.println("ERROR: invalid file header.");
      }
    }

    /**
     * Helper method.
     *
     * @param str checking if the string is empty
     * @return a placeholder string
     */
    public String check(String str) {
      //if the string is empty put a placeholder
      if (str.equals("")) {
        str = "N/A";
      }
      return str;
    }

    /**
     * This method ensures that the file is a valid input.
     *
     * @param str header string
     * @return boolean
     */
    public boolean validHeader(String str) {
      String[] parsedHeader = str.split(",");
      if (parsedHeader.length != 6) {
        System.out.println("Error: expected header to have 6 fields, not " + parsedHeader.length);
        return false;
      }
      return (parsedHeader[0].equals("datetime"))
        && (parsedHeader[1].equals("first_name"))
        && (parsedHeader[2].equals("last_name"))
        && (parsedHeader[3].equals("email"))
        && (parsedHeader[4].equals("gender"))
        && (parsedHeader[5].equals("street_address"));
    }
  }



}
