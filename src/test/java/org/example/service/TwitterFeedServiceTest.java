package org.example.service;

import lombok.extern.log4j.Log4j2;
import org.example.exception.DataException;
import org.example.mapper.TweetDataMapper;
import org.example.mapper.UserDataMapper;
import org.example.model.TwitterFollowers;
import org.example.model.TwitterTweets;
import org.example.utility.Configuration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

/**
 * Test class for TwitterFeedService class.
 */
@Log4j2
@RunWith(PowerMockRunner.class)
@PrepareForTest(TwitterFeedService.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*",
    "org.w3c.dom.*", "org.apache.logging.*", "java.nio.*"})
public final class TwitterFeedServiceTest {

  private static final String TWITTER_FEED = "Alan\n" +
      "\t@Alan: If you have a procedure with 10 parameters, you probably missed some.\n" +
      "\t@Alan: Random numbers should not be generated with a method chosen at random.\n" +
      "Martin\n" +
      "Ward\n" +
      "\t@Alan: If you have a procedure with 10 parameters, you probably missed some.\n" +
      "\t@Ward: There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors.\n" +
      "\t@Alan: Random numbers should not be generated with a method chosen at random.\n";

  private static final UserDataMapper mockUserDataMapper = Mockito.mock(UserDataMapper.class);
  private static final TweetDataMapper mockTweetDataMapper = Mockito.mock(TweetDataMapper.class);

  @BeforeClass
  public static void setup() throws Exception {
    PowerMockito.whenNew(UserDataMapper.class).withNoArguments().thenReturn(mockUserDataMapper);
    PowerMockito.whenNew(TweetDataMapper.class).withAnyArguments().thenReturn(mockTweetDataMapper);
  }

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
    System.setProperty(Configuration.PROPERTY_USER_FILE_PATH, "src/test/resources/user.txt");
    System.setProperty(Configuration.PROPERTY_TWEET_FILE_PATH, "src/test/resources/tweet.txt");
    Configuration.init();

    String twitterFeed = TwitterFeedService.produceTwitterFeed();
    Assert.assertNotNull(twitterFeed);
    Assert.assertEquals("Feed does not match expected", TWITTER_FEED, twitterFeed);
  }

  /**
   * Test use case where data mapper throws exception that should result in program
   * erroring out.
   *
   * @throws Exception
   */
  @Test(expected = IOException.class)
  public void testProduceTwitterFeed_IOException() throws Exception {
    mockDataMappers();
    Mockito.doThrow(new IOException()).when(mockUserDataMapper).parseData(Mockito.anyString());
    TwitterFeedService.produceTwitterFeed();
  }

  /**
   * Use case where data mapper errors out due to data being malformed, should
   * also result in error being thrown.
   */
  @Test(expected = DataException.class)
  public void testProduceTwitterFeed_DataException() throws Exception {
    mockDataMappers();
    Mockito.doThrow(new DataException("Dummy message")).when(mockUserDataMapper).parseData(Mockito.anyString());
    TwitterFeedService.produceTwitterFeed();
  }

  @Test(expected = DataException.class)
  public void testProduceTwitterFeed_NoUsers() throws Exception {
    mockDataMappers();
    Mockito.when(mockUserDataMapper.parseData(Mockito.anyString())).thenReturn(new TwitterFollowers());
//    Mockito.doReturn(new TwitterFollowers()).when(mockUserDataMapper).parseData(Mockito.anyString());
    TwitterFeedService.produceTwitterFeed();
  }

  @Test
  public void testProduceTwitterFeed_UsersWithoutTweets() throws Exception {
    final String expectedTwitterFeed = "Alan\n" +
        "Martin\n" +
        "Ward\n";
    TwitterFollowers twitterFollowers = new UserDataMapper().parseData("src/test/resources/user.txt");

    mockDataMappers();
    Mockito.when(mockUserDataMapper.parseData(Mockito.anyString())).thenReturn(twitterFollowers);
    Mockito.when(mockTweetDataMapper.parseData(Mockito.anyString())).thenReturn(new TwitterTweets());
//    Mockito.doReturn(twitterFollowers).when(mockUserDataMapper).parseData(Mockito.anyString());
//    Mockito.doReturn(new TwitterTweets()).when(mockTweetDataMapper).parseData(Mockito.anyString());
    String twitterFeed = TwitterFeedService.produceTwitterFeed();

    Assert.assertEquals("Twitter feed mismatch", expectedTwitterFeed, twitterFeed);
  }

  /**
   * Utility method to return mocked data mapper objects to test different use cases.
   *
   * @throws Exception
   */
  private void mockDataMappers() throws Exception {
    PowerMockito.whenNew(UserDataMapper.class).withNoArguments().thenReturn(mockUserDataMapper);
    PowerMockito.whenNew(TweetDataMapper.class).withAnyArguments().thenReturn(mockTweetDataMapper);
  }
}
