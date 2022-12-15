package org.example.logic;

import lombok.extern.log4j.Log4j2;
import org.example.exception.DataException;
import org.example.mapper.TweetDataMapper;
import org.example.mapper.UserDataMapper;
import org.example.model.TwitterFollowers;
import org.example.model.TwitterTweets;
import org.example.utility.Configuration;

import java.io.IOException;
import java.util.List;

/**
 * Service for generating the Twitter feed based on the file inputs.
 */
@Log4j2
public final class TwitterFeedService {

  private TwitterFeedService() {
    // Private constructor
  }

  /**
   * Invoke data mappers to extract data from input files and build Twitter feed per
   * user.
   *
   * @throws DataException Occurs when a data record within input file is invalid such as pattern mismatch.
   * @throws IOException   Occurs when reading the input file fails
   */
  public static void produceTwitterFeed() throws DataException, IOException {
    // Extract input via data mappers into POJO objects
    UserDataMapper userDataMapper = new UserDataMapper();
    TwitterFollowers twitterFollowers = userDataMapper.parseData(Configuration.getUserFilePath());
    TweetDataMapper tweetDataMapper = new TweetDataMapper(twitterFollowers);
    TwitterTweets twitterTweets = tweetDataMapper.parseData(Configuration.getTweetFilePath());

    log.debug("Building Twitter feed output");

    /*
    Loop through Twitter users from user input and extract their tweets
    to be viewed based on who they are followers for.

    Format:
    X
      <tab>@Y:<space>Z

    X - Twitter user
    Y - Twitter user that posted tweet
    Z - Twitter tweet
     */
    StringBuilder twitterFeed = new StringBuilder();
    for (String user : twitterFollowers.getUsers()) {
      twitterFeed.append(user).append("\n");

      List<String> userTweets = twitterTweets.getTweets(user);
      if (userTweets != null && !userTweets.isEmpty()) {
        for (String tweet : userTweets) {
          twitterFeed.append("\t").append(tweet).append("\n");
        }
      }
    }

    log.info("Feed output\n" + twitterFeed);
  }
}
