package org.example.exception;

/**
 * Exception class related to any configuration issue such as missing
 * or invalid environment variables.
 */
public class ConfigException extends Exception {

  /**
   * @param errorMessage error message for exception.
   */
  public ConfigException(String errorMessage) {
    super(errorMessage);
  }
}
