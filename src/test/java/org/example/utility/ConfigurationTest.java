package org.example.utility;

import org.example.exception.ConfigException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for Configuration utility class
 */
public class ConfigurationTest {

  @Before
  public void setup() {
    System.clearProperty(Configuration.PROPERTY_USER_FILE_PATH);
    System.clearProperty(Configuration.PROPERTY_TWEET_FILE_PATH);
  }

  /**
   * Test use case where no mandatory properties are set that is
   * required for the program to run.
   *
   * @throws ConfigException
   */
  @Test(expected = ConfigException.class)
  public void testInit_NoConfigSet() throws ConfigException {
    Configuration.init();
  }

  /**
   * Test use case where properties are correctly defined and ensure the values
   * passed are also verbatim returned for getter methods.
   *
   * @throws ConfigException
   */
  @Test
  public void testInit_AllConfigSet() throws ConfigException {
    final String dummyUserPath = "/path/to/file/user";
    final String dummyTweetPath = "/path/to/file/tweet";

    System.setProperty(Configuration.PROPERTY_USER_FILE_PATH, dummyUserPath);
    System.setProperty(Configuration.PROPERTY_TWEET_FILE_PATH, dummyTweetPath);
    Configuration.init();

    assertEquals(Configuration.getUserFilePath(), dummyUserPath);
    assertEquals(Configuration.getTweetFilePath(), dummyTweetPath);
  }
}
