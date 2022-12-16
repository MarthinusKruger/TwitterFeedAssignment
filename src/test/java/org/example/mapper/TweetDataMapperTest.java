package org.example.mapper;

import lombok.extern.log4j.Log4j2;
import org.example.exception.DataException;
import org.example.model.TwitterFollowers;
import org.example.model.TwitterTweets;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Test class for the TweetDataMapper class
 */
@Log4j2
public class TweetDataMapperTest {

  private static final String PATH = "path/to/file/tweet.txt";
  private static TweetDataMapper tweetDataMapper = null;

  @BeforeClass
  public static void setupBeforeClass() throws DataException, IOException {
    try {
      UserDataMapper userDataMapper = new UserDataMapper();
      TwitterFollowers twitterFollowers = userDataMapper.parseData("src/test/resources/user.txt");

      tweetDataMapper = Mockito.spy(new TweetDataMapper(twitterFollowers));
    } catch (IOException | DataException e) {
      log.error("Failed to setup test cases for TweetDataMapperTest class");
      throw e;
    }
  }

  /**
   * Test happy path with correct data and ensure amount of tweets per user is correct linked.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test
  public void testParseData_HappyPath() throws IOException, DataException {
    List<String> tweetData = new LinkedList<>();
    tweetData.add("Alan> If you have a procedure with 10 parameters, you probably missed some.");
    tweetData.add("Ward> There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors.");
    tweetData.add("Alan> Random numbers should not be generated with a method chosen at random.");

    TwitterTweets twitterTweets = invokeParseData(tweetData);

    Assert.assertNotNull(twitterTweets);
    Assert.assertEquals("Alan Tweet count mismatch", 2, twitterTweets.getTweets("Alan").size());
    Assert.assertEquals("Ward Tweet count mismatch", 3, twitterTweets.getTweets("Ward").size());
    Assert.assertEquals("Martin Tweet count mismatch", 0, twitterTweets.getTweets("Martin").size());
  }

  /**
   * Use where file couldn't be read; method should error out.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test(expected = IOException.class)
  public void testParseData_FileReadError() throws IOException, DataException {
    Mockito.doThrow(new IOException()).when(tweetDataMapper).readFile(Mockito.anyString());
    tweetDataMapper.parseData(PATH);
  }

  /**
   * Use case where data read from file is empty.
   * Should return empty list of tweets and continue processing.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test
  public void testParseData_EmptyUserContent() throws IOException, DataException {
    TwitterTweets twitterTweets = invokeParseData(new LinkedList<>());

    Assert.assertNotNull(twitterTweets);
    Assert.assertEquals("Alan Tweet count mismatch", 0, twitterTweets.getTweets("Alan").size());
    Assert.assertEquals("Ward Tweet count mismatch", 0, twitterTweets.getTweets("Ward").size());
  }

  /**
   * Use case where no user on left side of delimiter.
   * Expect error to be thrown.
   *
   * @throws DataException
   * @throws IOException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternNoUser() throws DataException, IOException {
    List<String> tweetData = new LinkedList<>();
    tweetData.add("Alan> If you have a procedure with 10 parameters, you probably missed some.");
    tweetData.add("> There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors.");

    invokeParseData(tweetData);
  }

  /**
   * Use case where no greater than sign and only space after user.
   * Expect error to be thrown.
   *
   * @throws DataException
   * @throws IOException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternNoDelimiterGreaterThan() throws DataException, IOException {
    List<String> tweetData = new LinkedList<>();
    tweetData.add("Alan> If you have a procedure with 10 parameters, you probably missed some.");
    tweetData.add("Ward There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors.");

    invokeParseData(tweetData);
  }

  /**
   * Use case where no space after greater than sign for delimiter.
   * Expect error to be thrown.
   *
   * @throws DataException
   * @throws IOException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternNoDelimiterSpace() throws DataException, IOException {
    List<String> tweetData = new LinkedList<>();
    tweetData.add("Alan> If you have a procedure with 10 parameters, you probably missed some.");
    tweetData.add("Ward>There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors.");

    invokeParseData(tweetData);
  }

  /**
   * Use case where no tweet is posted (only whitespace).
   * Expect error to be thrown.
   *
   * @throws DataException
   * @throws IOException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternNoTweet() throws DataException, IOException {
    List<String> tweetData = new LinkedList<>();
    tweetData.add("Alan> If you have a procedure with 10 parameters, you probably missed some.");
    tweetData.add("Ward>  ");

    invokeParseData(tweetData);
  }

  /**
   * Use case where tweet is longer than 140 characters.
   * Expect error to be thrown.
   *
   * @throws DataException
   * @throws IOException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternTooManyCharacters() throws DataException, IOException {
    List<String> tweetData = new LinkedList<>();
    tweetData.add("Alan> If you have a procedure with 10 parameters, you probably missed some.");
    tweetData.add("Ward> There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors. " +
        "If you have a procedure with 10 parameters, you probably missed some.");

    invokeParseData(tweetData);
  }

  /**
   * Use case where whitespace at start and end of tweet record.
   * Should process as normal.
   *
   * @throws DataException
   * @throws IOException
   */
  @Test
  public void testParseData_PatternWhitespace() throws DataException, IOException {
    List<String> tweetData = new LinkedList<>();
    tweetData.add("Alan> If you have a procedure with 10 parameters, you probably missed some.");
    tweetData.add("    Ward> There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors.     ");

    TwitterTweets twitterTweets = invokeParseData(tweetData);

    Assert.assertNotNull(twitterTweets);
    Assert.assertEquals("Alan Tweet count mismatch", 1, twitterTweets.getTweets("Alan").size());
    Assert.assertEquals("Ward Tweet count mismatch", 2, twitterTweets.getTweets("Ward").size());
    Assert.assertEquals("Ward Tweet count mismatch", 0, twitterTweets.getTweets("Martin").size());
  }

  /**
   * Use case where no tweets in input file, only whitespace lines.
   * Should process as normal with no data to show.
   *
   * @throws DataException
   * @throws IOException
   */
  @Test
  public void testParseData_EmptyRecordsNoTweets() throws DataException, IOException {
    List<String> tweetData = new LinkedList<>();
    tweetData.add("    ");
    tweetData.add("    ");

    TwitterTweets twitterTweets = invokeParseData(tweetData);

    Assert.assertNotNull(twitterTweets);
    Assert.assertEquals("Alan Tweet count mismatch", 0, twitterTweets.getTweets("Alan").size());
    Assert.assertEquals("Ward Tweet count mismatch", 0, twitterTweets.getTweets("Ward").size());
    Assert.assertEquals("Ward Tweet count mismatch", 0, twitterTweets.getTweets("Martin").size());
  }

  /**
   * Use case where tweet record contains user not part of user.txt file.
   * Should process as normal and ensure that new user can at least
   * see their own set of tweets.
   * <p>
   * Expect error to be thrown.
   *
   * @throws DataException
   * @throws IOException
   */
  @Test
  public void testParseData_TweetNoLinkingUser() throws DataException, IOException {
    List<String> tweetData = new LinkedList<>();
    tweetData.add("Alan> If you have a procedure with 10 parameters, you probably missed some.");
    tweetData.add("Piet> Example tweet linking to non-existing user.");

    TwitterTweets twitterTweets = invokeParseData(tweetData);

    Assert.assertNotNull(twitterTweets);
    Assert.assertEquals("Alan Tweet count mismatch", 1, twitterTweets.getTweets("Alan").size());
    Assert.assertEquals("Piet Tweet count mismatch", 1, twitterTweets.getTweets("Piet").size());
  }

  /**
   * Utility method to assist with invoking the parseData method and passing a specific set of tweet records
   *
   * @param data
   * @return
   * @throws DataException
   * @throws IOException
   */
  private TwitterTweets invokeParseData(List<String> data) throws DataException, IOException {
    Mockito.doReturn(data).when(tweetDataMapper).readFile(Mockito.anyString());
    return tweetDataMapper.parseData(PATH);
  }
}
