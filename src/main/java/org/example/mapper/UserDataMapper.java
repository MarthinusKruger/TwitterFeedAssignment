package org.example.mapper;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.example.exception.DataException;
import org.example.model.TwitterFollowers;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataMapper class specifically for the input file for Twitter users
 */
@Log4j2
public class UserDataMapper extends AbstractDataMapper implements DataMapper<TwitterFollowers> {

  private static final String DELIMITER_USER_FOLLOWER = ", ";
  private static final Pattern REGEX_USER_FOLLOWERS = Pattern.compile("^([a-zA-Z]+)\\sfollows\\s(([a-zA-Z]+)(,\\s[a-zA-Z]+)*)$");

  /**
   * Parse the user input data and return in-memory data structure.
   *
   * @param filePath The absolute path to the file to read and parse.
   * @return TwitterFollowers POJO object
   * @throws IOException   Error occurs during file reading
   * @throws DataException Error occurs with data such as pattern mismatch or if no users in input file.
   */
  @Override
  public TwitterFollowers parseData(String filePath) throws IOException, DataException {
    log.debug("Parsing user data");
    List<String> fileData;

    // Read input file and get lines as data output
    try {
      fileData = readFile(filePath);
    } catch (IOException e) {
      log.error("Failed to parse Twitter user file (" + filePath + ")");
      throw e;
    }

    // If there are no users then the feed would be empty, thus throw error
    if (fileData.isEmpty()) {
      throw new DataException("No users exist in file " + filePath + ". No tweets can therefore exist to build Twitter feed.");
    }

    return parseTwitterFollowers(fileData);
  }

  /**
   * Parse the input data from file as Twitter followers based on defined pattern
   *
   * @param fileData The data file's contents.
   * @return TwitterFollowers POJO
   * @throws DataException Error occurs with data such as pattern mismatch or if no users in input file.
   */
  private TwitterFollowers parseTwitterFollowers(List<String> fileData) throws DataException {
    TwitterFollowers twitterFollowers = new TwitterFollowers();

    // Loop through the file and parse users into Twitter followers object
    int lineCounter = 1;
    for (String line : fileData) {

      // In the case where empty lines occurs, just skip as it won't affect data, but log warning
      if (StringUtils.isAllBlank(line)) {
        log.warn("Line " + lineCounter + " contains only whitespace/empty line, skipping...");
        continue;
      }

      log.debug("Raw User Record: " + line);

      // Strip any starting and ending spaces before pattern matching (acceptable user content)
      line = StringUtils.strip(line);
      Matcher matcher = REGEX_USER_FOLLOWERS.matcher(line);

      // If data line matches pattern then extract specific values from pattern group
      if (matcher.matches()) {
        /*
        First group is Twitter user that "follows" another (follower).
        Second group is the Twitter user(s) being followed by said Twitter user.

        X follows Y
        X being a Twitter follower to Y that can be a single or multiple Twitter users
         */
        String follower = matcher.group(1);
        String users = matcher.group(2);

        log.debug("Record Group 1: " + follower);
        log.debug("Record Group 2: " + users);

        // Loop through list of users in second group and add follower
        for (String user : users.split(DELIMITER_USER_FOLLOWER)) {
          twitterFollowers.addFollower(user, follower);
        }

        // User will also be their own follower (i.e. see their own posts)
        twitterFollowers.addFollower(follower, follower);
      } else {
        //  Strict pattern matching; error out program if malformed data record is found
        throw new DataException("User record on line " + lineCounter + " does not conform to pattern.\nRecord: " + line);
      }

      lineCounter++;
    }

    /*
    If the file is all whitespace and no user data ensure we check if we parsed any data.
    Similar to above, if no users, then there is no Twitter feed to build.
     */
    if (twitterFollowers.getUsers() != null && twitterFollowers.getUsers().isEmpty()) {
      throw new DataException("No user records found. No tweets can therefore exist to build Twitter feed.");
    }

    return twitterFollowers;
  }
}
