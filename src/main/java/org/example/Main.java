package org.example;

import lombok.extern.log4j.Log4j2;
import org.example.logic.TwitterFeedService;
import org.example.utility.Configuration;

/**
 * Main class and method for the program to produce a Twitter
 * feed based on the input provided in a user and tweet text file.
 */
@Log4j2
public class Main {

  public static void main(String[] args) {
    try {
      log.info("Generating Twitter feed");

      /*
      Initialise configuration by extracting environment variables
      into variables such as the paths to the input files.
       */
      Configuration.init();

      /*
      Read the input files, build the required data structures
      and output the Twitter feed per user in documented format.
       */
      TwitterFeedService.produceTwitterFeed();

      log.info("Twitter feed generated successfully!");
    } catch (Exception e) {
      /*
      If an error occurs in the program then send failure
      exit code.
       */
      log.error("Failed to generate Twitter feed", e);
      System.exit(1);
    }
  }
}