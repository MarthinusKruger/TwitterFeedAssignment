package org.example.utility;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.example.exception.ConfigException;

/**
 * Utility class to handle the configuration that can be set for this program such
 * as the paths to the input files.
 */
@Log4j2
public class Configuration {

  // Constants
  private static final String ENV_USER_FILE_PATH = "USER_FILE_PATH";
  private static final String ENV_TWEET_FILE_PATH = "TWEET_FILE_PATH";

  // Global Variables
  private static String userFilePath;
  private static String tweetFilePath;

  private Configuration() {
    // Private constructor
  }

  /**
   * Initialise program configuration by extracting environment variables
   * into runtime variables for use by program.
   *
   * @throws ConfigException Occurs when mandatory environment variables aren't set.
   */
  public static void init() throws ConfigException {
    // Extract file paths from environment variables
    userFilePath = parseConfigElement(ENV_USER_FILE_PATH);
    tweetFilePath = parseConfigElement(ENV_TWEET_FILE_PATH);
  }

  /**
   * Parse the environment variable passed and provide the value defined. Method assumes that
   * config field is mandatory (has to be set for program to run).
   *
   * @param envVar The environment variable to lookup.
   * @return The value of the environment variable.
   * @throws ConfigException Occurs when the environment variable is empty or not set.
   */
  private static String parseConfigElement(String envVar) throws ConfigException {
    log.debug("Parsing value for environment variable - " + envVar);
    String envVal = System.getenv(envVar);

    // All values are mandatory so check if empty or wasn't set
    if (StringUtils.isBlank(envVal)) {
      throw new ConfigException("Value not set for environment variable - " + envVar);
    }

    log.debug("Extracted value: " + envVal);
    return envVal;
  }

  /**
   * Get the user input file path
   *
   * @return The absolute file path
   */
  public static String getUserFilePath() {
    return userFilePath;
  }

  /**
   * Get the tweet input file path
   *
   * @return The absolute file path
   */
  public static String getTweetFilePath() {
    return tweetFilePath;
  }
}
