package org.example.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * POJO class for storing Twitter users and their list of followers
 * as per the user input file.
 */
public class TwitterFollowers {

  /*
   Map Twitter user to unique set of Twitter followers
   TreeMap impl to ensure natural ordering of user key.

   Structure contains Twitter user as key and set of unique
   followers.
   */
  private final Map<String, Set<String>> followers = new TreeMap<>();

  /**
   * Add/Link a follower to a Twitter user.
   *
   * @param user     The Twitter user being followed.
   * @param follower The Twitter follower (also a user Twitter user).
   */
  public void addFollower(String user, String follower) {
    Set<String> userFollowers = followers.computeIfAbsent(user, k -> new HashSet<>());
    userFollowers.add(follower);
  }

  /**
   * Get list of Twitter followers for a given Twitter user.
   *
   * @param user The Twitter user for lookup.
   * @return Unique set of Twitter followers for a given Twitter user.
   * null is returned if the Twitter user has no Twitter followers.
   */
  public Set<String> getFollowers(String user) {
    return followers.getOrDefault(user, null);
  }

  public boolean hasFollowers(String user) {
    return followers.containsKey(user);
  }

  /**
   * Get unique set of Twitter users.
   *
   * @return Set of Twitter users.
   */
  public Set<String> getUsers() {
    return followers.keySet();
  }
}
