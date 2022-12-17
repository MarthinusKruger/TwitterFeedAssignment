package org.example.service;

import org.example.exception.DataException;
import org.example.utility.Configuration;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for TwitterFeedService class.
 */
public final class TwitterFeedServiceTest {

  private static final String USER_FILE_VALID = "src/test/resources/user.txt";
  private static final String USER_FILE_EMPTY = "src/test/resources/user_empty.txt";

  private static final String TWEET_FILE_VALID = "src/test/resources/tweet.txt";
  private static final String TWEET_FILE_EMPTY = "src/test/resources/tweet_empty.txt";
  private static final String TWEET_FILE_PIET = "src/test/resources/tweet_piet.txt";

  /**
   * Use case for successful processing and Twitter feed.
   * Used real methods for data parsing so that test class does not need to be
   * aware of how it gets populated and focuses on its isolated scope (within the class).
   * <p>
   * Tests if feed returned is same as expected result.
   *
   * @throws Exception
   */
  @Test
  public void testProduceTwitterFeed_HappyPath() throws Exception {
    String expectedTwitterFeed = "Alan\n" +
        "\t@Alan: If you have a procedure with 10 parameters, you probably missed some.\n" +
        "\t@Alan: Random numbers should not be generated with a method chosen at random.\n" +
        "Martin\n" +
        "Ward\n" +
        "\t@Alan: If you have a procedure with 10 parameters, you probably missed some.\n" +
        "\t@Ward: There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors.\n" +
        "\t@Alan: Random numbers should not be generated with a method chosen at random.\n";

    System.setProperty(Configuration.PROPERTY_USER_FILE_PATH, USER_FILE_VALID);
    System.setProperty(Configuration.PROPERTY_TWEET_FILE_PATH, TWEET_FILE_VALID);
    Configuration.init();

    String twitterFeed = TwitterFeedService.produceTwitterFeed();
    Assert.assertNotNull(twitterFeed);
    Assert.assertEquals("Feed does not match expected", expectedTwitterFeed, twitterFeed);
  }

  /**
   * Use case tested where users in input file, but tweet file is empty.
   * Should process as expected and printout list of users
   * in natural ordering (alphabetical order).
   *
   * @throws Exception
   */
  @Test
  public void testProduceTwitterFeed_UsersWithoutTweets() throws Exception {
    final String expectedTwitterFeed = "Alan\n" +
        "Martin\n" +
        "Ward\n";
    System.setProperty(Configuration.PROPERTY_USER_FILE_PATH, USER_FILE_VALID);
    System.setProperty(Configuration.PROPERTY_TWEET_FILE_PATH, TWEET_FILE_EMPTY);
    Configuration.init();


    String twitterFeed = TwitterFeedService.produceTwitterFeed();

    Assert.assertEquals("Twitter feed mismatch", expectedTwitterFeed, twitterFeed);
  }

  /**
   * Use case where no users in input file, but tweets exist so user(s)
   * sourced from there and presented as a feed with own posts.
   *
   * @throws Exception
   */
  @Test
  public void testProduceTwitterFeed_NoUsersWithTweets() throws Exception {
    final String expectedTwitterFeed = "Piet\n" +
        "\t@Piet: Tweet tweet.\n";
    System.setProperty(Configuration.PROPERTY_USER_FILE_PATH, USER_FILE_EMPTY);
    System.setProperty(Configuration.PROPERTY_TWEET_FILE_PATH, TWEET_FILE_PIET);
    Configuration.init();

    String twitterFeed = TwitterFeedService.produceTwitterFeed();

    Assert.assertEquals("Twitter feed mismatch", expectedTwitterFeed, twitterFeed);
  }

  /**
   * If both the user and tweet file is empty and no users extracted then error
   * out as we cannot build feed without any Twitter users.
   *
   * @throws Exception
   */
  @Test(expected = DataException.class)
  public void testProduceTwitterFeed_NoUsersNoTweetsAfterParsing() throws Exception {
    System.setProperty(Configuration.PROPERTY_USER_FILE_PATH, USER_FILE_EMPTY);
    System.setProperty(Configuration.PROPERTY_TWEET_FILE_PATH, TWEET_FILE_EMPTY);
    Configuration.init();

    TwitterFeedService.produceTwitterFeed();
  }
}
