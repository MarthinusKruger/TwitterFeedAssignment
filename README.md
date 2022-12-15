# Twitter Feed Assignment

## Description

Please write a program to simulate a twitter-like feed. Your program will receive two seven-bit ASCII files. The first
file contains a list of users and their
followers. The second file contains tweets. Given the users, followers and tweets, the objective is to display a
simulated twitter feed for each user to the
console. You will receive sample text files to use as input.

The program should be well designed, handle errors, have unit tests and should be of sufficient quality to run on a
production system. Indicate all
assumptions made in completing the assignment.

Each line of a well-formed user file contains a user, followed by the word 'follows' and then a comma separated list of
users they follow. Where there is
more than one entry for a user, consider the union of all these entries to determine the users they follow.

Lines of the tweet file contain a user, followed by greater than, space and then a tweet that may be at most 140
characters in length. The tweets are
considered to be posted by each user in the order they are found in this file.

Your program needs to write console output as follows. For each user / follower (in alphabetical order) output their
name on a line. Then for each tweet,
emit a line with the following format: _<tab>@user: <space>message_

**Here is an example. Given user file named user.txt:**

```
Ward follows Alan
Alan follows Martin
Ward follows Martin, Alan
```

**And given tweet file named tweet.txt:**

```
Alan> If you have a procedure with 10 parameters, you probably missed some.
Ward> There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors.
Alan> Random numbers should not be generated with a method chosen at random.
```

So invoking your program with user.txt and tweet.txt as arguments should produce the following console output:

```
Alan
    @Alan: If you have a procedure with 10 parameters, you probably missed some.
    @Alan: Random numbers should not be generated with a method chosen at random.
Martin
Ward
    @Alan: If you have a procedure with 10 parameters, you probably missed some.
    @Ward: There are only two hard things in Computer Science: cache invalidation, naming things and off-by-1 errors.
    @Alan: Random numbers should not be generated with a method chosen at random.
```

## Assumptions

- Format for user.txt and tweet.txt is strict; assuming file is generated by a system.
- For user.txt if multiple followed by user it explicitly needs to be separated by a comma and a space.
- A user cannot follow themselves as part of the user.txt content (even though they see their own feed in the output)
- If multiple entries for a user then the "follow" group is appended to list for user (i.e. keep unique list of users
  being followed)
- If users.txt is empty then per logic there can be no tweets so program will error out. (if no users then no Twitter
  feed to produce)
- As per description, 7-bit ASCII files so assumption is not to catered for extended codes such as Twitter users
  containing umlaut etc.
- In the user input file, if a user follows themselves then continue processing as Twitter feed per user also includes
  own posts/tweets.
- User can be defined across multiple lines in the user input file; any unique set of users will just be appended to
  existing data structure.
- No limit is enforced on list of users as part of second group Y (X follows Y) in user input file.
- Use case is valid where users exist but no tweets (no one has posted anything yet)
- If any data lines in either user.txt and tweet.txt does not match pattern then assume malformed data and error out.
- tweet.txt is ordered from newest to earliest tweets.
- Twitter feed per user will ensure ordered from newest to oldest tweet.
- Additional logging is added to console output for debugging purposes.

## How to Run

TODO