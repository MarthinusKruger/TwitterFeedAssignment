package org.example.exception;

/**
 * Exception class related to any data issues within the input files such
 * as pattern mismatch, or no user data.
 */
public class DataException extends Exception {

  /**
   * @param errorMessage error message for exception.
   */
  public DataException(String errorMessage) {
    super(errorMessage);
  }
}
