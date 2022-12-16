package org.example.model;

import java.util.*;

/**
 * POJO class for the Twitter tweets linked to the Twitter follower.
 */
public class TwitterTweets {

  private static final String TWEET_FORMAT = "@%s: %s";
  private final Map<String, List<String>> tweets = new HashMap<>();

  /**
   * Add a Twitter tweet to a set of followers that are following the owner/poster
   * of passed tweet.
   *
   * @param followers  Set of followers that will consume the tweet.
   * @param tweetOwner The owner of the tweet
   * @param tweet      The actual tweet/post
   */
  public void addTweet(Set<String> followers, String tweetOwner, String tweet) {
    // Ensure if any followers that require to see this tweet
    if (followers != null) {
      // For each follower add the tweet in correct feed format
      followers.forEach(follower -> {
        // Add tweets to LinkedList impl to ensure correct order during feed printout
        List<String> tweetList = tweets.computeIfAbsent(follower, v -> new LinkedList<>());
        tweetList.add(String.format(TWEET_FORMAT, tweetOwner, tweet));
      });
    }
  }

  /**
   * Get list of tweets for passed Twitter follower
   *
   * @param follower Twitter follower
   * @return List of tweets that the Twitter follower should see on their feed
   */
  public List<String> getTweets(String follower) {
    return tweets.getOrDefault(follower, new LinkedList<>());
  }
}
