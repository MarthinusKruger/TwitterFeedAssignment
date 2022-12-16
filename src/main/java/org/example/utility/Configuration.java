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
  public static final String PROPERTY_USER_FILE_PATH = "path.file.user";
  public static final String PROPERTY_TWEET_FILE_PATH = "path.file.tweet";

  // Global Variables
  private static String userFilePath;
  private static String tweetFilePath;

  private Configuration() {
    // Private constructor
  }

  /**
   * Initialise program configuration by extracting properties
   * into runtime variables for use by program.
   *
   * @throws ConfigException Occurs when mandatory properties aren't set.
   */
  public static void init() throws ConfigException {
    // Extract input file paths
    userFilePath = parseConfigElement(PROPERTY_USER_FILE_PATH);
    tweetFilePath = parseConfigElement(PROPERTY_TWEET_FILE_PATH);
  }

  /**
   * Parse the property name passed and provide the value defined. Method assumes that
   * config field is mandatory (has to be set for program to run).
   *
   * @param envVar The property name to lookup.
   * @return The value of the property.
   * @throws ConfigException Occurs when the property is empty or not set.
   */
  private static String parseConfigElement(String envVar) throws ConfigException {
    log.debug("Parsing value for property - " + envVar);
    String envVal = System.getProperty(envVar);

    // All values are mandatory so check if empty or wasn't set
    if (StringUtils.isBlank(envVal)) {
      throw new ConfigException("Value not set for property - " + envVar);
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
