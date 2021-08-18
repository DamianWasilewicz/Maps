package edu.brown.cs.student.logic;


import java.util.List;

/**
 * This interfaces defines a command that can be run in the REPL.
 */
public interface Commandable {

  /**
   * All commands will implement this method to run.
   *
   * @param argument
   *      input from repl
   */
  void execute(List<String> argument);
}
