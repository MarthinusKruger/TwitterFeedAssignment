package org.example.mapper;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.example.exception.DataException;
import org.example.model.TwitterFollowers;
import org.example.model.TwitterTweets;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataMapper class specifically for the input file for Twitter tweets
 */
@Log4j2
public class TweetDataMapper extends AbstractDataMapper implements DataMapper<TwitterTweets> {

  private static final Pattern REGEX_TWEETS = Pattern.compile("^([a-zA-Z]+)>\\s((.){1,139})$");
  private final TwitterFollowers twitterFollowers;

  /**
   * Constructor to pass in list of followers to reference during parsing processing
   *
   * @param twitterFollowers TwitterFollowers POJO object
   */
  public TweetDataMapper(TwitterFollowers twitterFollowers) {
    this.twitterFollowers = twitterFollowers;
  }

  /**
   * Parse the user input data and return in-memory data structure.
   *
   * @param filePath The absolute path to the file to read and parse.
   * @return TwitterTweets POJO object
   * @throws IOException   Error occurs during file reading
   * @throws DataException Error occurs with data such as pattern mismatch.
   */
  @Override
  public TwitterTweets parseData(String filePath) throws IOException, DataException {
    log.debug("Parsing tweet data");
    TwitterTweets twitterTweets = new TwitterTweets();
    List<String> fileData;

    try {
      fileData = readFile(filePath);
    } catch (IOException e) {
      log.error("Failed to parse Twitter tweet file (" + filePath + ")");
      throw e;
    }

    /*
    If there are no tweets then we don't error out.
    It just means users follow one another but no one has posted anything yet.
    Return empty object and continue processing.
     */
    if (fileData.isEmpty()) {
      return twitterTweets;
    }

    // Loop through the file and parse tweets into Twitter Tweet object
    int lineCounter = 0;
    for (String line : fileData) {
      lineCounter++;

      // In the case where empty lines occurs, just skip as it won't affect data, but log warning
      if (StringUtils.isAllBlank(line)) {
        log.warn("Line " + lineCounter + " contains only whitespace/empty line, skipping...");
        continue;
      }

      log.debug("Raw Tweet Record: " + line);

      // Strip any starting and ending spaces before pattern matching (acceptable user content)
      line = StringUtils.strip(line);
      Matcher matcher = REGEX_TWEETS.matcher(line);

      // If data line matches pattern then extract specific values from pattern group
      if (matcher.matches()) {
        /*
        First group is the Twitter user that made the post.
        Second group is the actual Twitter post/tweet/message.
         */
        String user = matcher.group(1);
        String tweet = matcher.group(2);

        log.debug("Record Group 1: " + user);
        log.debug("Record Group 2: " + tweet);


        /*
        If tweet is found that does not have linking user then
        proceed by adding a single 1-1 mapping where the user
        will only see their own tweets as user is following no one
        and no one is following said user (which is a valid use case).
         */
        if (!twitterFollowers.hasFollowers(user)) {
          twitterFollowers.addFollower(user, user);
        }

        /*
        Pass list of followers for user that made tweet to ensure tweet is assigned to
        all users that need to see it in their feed.
         */
        twitterTweets.addTweet(twitterFollowers.getFollowers(user), user, tweet);
      } else {
        //  Strict pattern matching; error out program if malformed data record is found
        throw new DataException("Tweet record on line " + lineCounter + " does not conform to pattern.\nRecord: " + line);
      }
    }

    return twitterTweets;
  }
}
