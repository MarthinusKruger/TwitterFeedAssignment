package org.example.mapper;

import org.example.exception.DataException;
import org.example.model.TwitterFollowers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Test class for the UserDataMapper class
 */
public class UserDataMapperTest {

  private static final String PATH = "path/to/file/user.txt";

  private final UserDataMapper userDataMapper = Mockito.spy(new UserDataMapper());

  /**
   * Happy path for this method to ensure data is parsed and returned
   * as intended.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test
  public void testParseData_HappyPath() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Ward follows Alan");
    userData.add("Alan follows Martin");
    userData.add("Ward follows Martin, Alan");

    TwitterFollowers twitterFollowers = invokeParseData(userData);

    Assert.assertNotNull(twitterFollowers);
    Assert.assertEquals("User count mismatch", 3, twitterFollowers.getUsers().size());
    Assert.assertEquals("Follower count mismatch for Alan", 2, twitterFollowers.getFollowers("Alan").size());
    Assert.assertEquals("Follower count mismatch for Martin", 2, twitterFollowers.getFollowers("Martin").size());
    Assert.assertEquals("Follower count mismatch for Ward", 1, twitterFollowers.getFollowers("Ward").size());
  }

  /**
   * Use where file couldn't be read; method should error out.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test(expected = IOException.class)
  public void testParseData_FileReadError() throws IOException, DataException {
    Mockito.doThrow(new IOException()).when(userDataMapper).readFile(Mockito.anyString());
    userDataMapper.parseData(PATH);
  }

  /**
   * Use case where data read from file is empty.
   * Processing should continue without exception
   * as we might still see users from tweet.txt file.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test
  public void testParseData_EmptyUserContent() throws IOException, DataException {
    TwitterFollowers twitterFollowers = invokeParseData(new LinkedList<>());
    Assert.assertNotNull(twitterFollowers);
    Assert.assertEquals("No users expected", 0, twitterFollowers.getUsers().size());
  }

  /**
   * Use case where user record pattern does not contain the word "follows"
   * and should result in exception thrown.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternNoFollows() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("Alan Ward");

    invokeParseData(userData);
  }

  /**
   * Use case where user record does not contain n User on the right hand side
   * (after follows) to link/reference to; exception to be thrown.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternNoRightSideUser() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("Alan follows ");

    invokeParseData(userData);
  }

  /**
   * Use case where no User defined left of "follows"; exception to be
   * thrown.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternNoLeftSideUser() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("follows Rick");

    invokeParseData(userData);
  }

  /**
   * Use case where valid user records with empty whitespaces in-between.
   * Should still process records correctly and ignore empty lines.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test
  public void testParseData_ValidUsersEmptyLinesInBetween() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("    ");
    userData.add("Piet follows Rick");

    TwitterFollowers twitterFollowers = invokeParseData(userData);

    Assert.assertNotNull(twitterFollowers);
    Assert.assertEquals("User size mismatch", 3, twitterFollowers.getUsers().size());
  }

  /**
   * Use case where multiple users specified but ends on a comma so
   * should result in exception for malformed data record.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternRightSideEndWithComma() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("Piet follows Rick,");

    invokeParseData(userData);
  }

  /**
   * Use case where user contains numbers which is invalid per pattern;
   * exception to be thrown.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternUserContainsNumbers() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("Piet123 follows Rick,");

    invokeParseData(userData);
  }

  /**
   * Use case where there is no spaces as part of right-side user
   * delimeter; should result in exception thrown.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test(expected = DataException.class)
  public void testParseData_PatternRightSideUsersNoSpaces() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("Piet follows Rick,Alan,David");

    invokeParseData(userData);
  }

  /**
   * Use case where spaces at start and end of user record line.
   * Should not cause issues and processing should continue as normal.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test
  public void testParseData_PatternSpacesBeforeAndAfterData() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("      Piet follows Rick     ");

    TwitterFollowers twitterFollowers = invokeParseData(userData);

    Assert.assertNotNull(twitterFollowers);
    Assert.assertEquals("User size mismatch", 3, twitterFollowers.getUsers().size());
    Assert.assertTrue("User not trimmed", twitterFollowers.getUsers().contains("Rick"));
  }

  /**
   * Yse case where file contains list whitespace lines and results in no users processed.
   * Should continue as users could be added as part of tweet parsing.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test
  public void testParseData_EmptyLinesNoUsers() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("    ");
    userData.add("    ");
    userData.add("    ");

    TwitterFollowers twitterFollowers = invokeParseData(userData);
    
    Assert.assertNotNull(twitterFollowers);
    Assert.assertEquals("No users expected", 0, twitterFollowers.getUsers().size());
  }

  /**
   * Use case where duplicate users on right-side of user record.
   * Should not affect processing.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test
  public void testParseData_ValidUsersWithDuplicates() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("Piet follows Rick, Rick, Rick");

    TwitterFollowers twitterFollowers = invokeParseData(userData);

    Assert.assertNotNull(twitterFollowers);
    Assert.assertEquals("User size mismatch", 3, twitterFollowers.getUsers().size());
  }

  /**
   * Use case where user is specified multiple times and should
   * cause the union of followers built up for said user.
   *
   * @throws IOException
   * @throws DataException
   */
  @Test
  public void testParseData_ValidMultipleUsersUnionFollowers() throws IOException, DataException {
    List<String> userData = new LinkedList<>();
    userData.add("Rick follows Alex");
    userData.add("Piet follows Rick");
    userData.add("Rick follows Alan");

    TwitterFollowers twitterFollowers = invokeParseData(userData);

    Assert.assertNotNull(twitterFollowers);
    Assert.assertEquals("User size mismatch", 4, twitterFollowers.getUsers().size());
    Assert.assertEquals("Followers size mismatch", 2, twitterFollowers.getFollowers("Rick").size());
  }

  /**
   * Utility method to assist with invoking the parseData method and passing a specific set of user records
   *
   * @param data
   * @return
   * @throws DataException
   * @throws IOException
   */
  private TwitterFollowers invokeParseData(List<String> data) throws DataException, IOException {
    Mockito.doReturn(data).when(userDataMapper).readFile(Mockito.anyString());
    return userDataMapper.parseData(PATH);
  }
}
