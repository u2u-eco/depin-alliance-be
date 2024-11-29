package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import xyz.telegram.depinalliance.common.configs.TwitterConfig;
import xyz.telegram.depinalliance.common.models.response.TwitterFollowResponse;
import xyz.telegram.depinalliance.common.models.response.TwitterUserTweet;

/**
 * @author holden on 10-Oct-2024
 */
@ApplicationScoped
public class TwitterService {
  @Inject
  TwitterConfig twitterConfig;
  @Inject
  TwitterFactory twitterFactory;
  @RestClient
  TwitterClient twitterClient;

  @RestClient

  public Twitter getTwitter() {
    return twitterFactory.getInstance();
  }

  public synchronized boolean isUserFollowing(String userId, String followingUserId) {
    String continuationToken = "";
    while (true) {
      try {
        TwitterFollowResponse res = StringUtils.isBlank(continuationToken) ?
          twitterClient.getFollowing(userId) :
          twitterClient.getFollowingContinuation(userId, continuationToken);
        Thread.sleep(twitterConfig.rapidapiSleep());
        if (StringUtils.isNotBlank(res.continuationToken)) {
          continuationToken = res.continuationToken;
        }
        if (res.results == null || res.results.isEmpty()) {
          return false;
        }

        TwitterFollowResponse.TwitterUser userFollowing = res.results.stream()
          .filter(user -> user.userId.equalsIgnoreCase(followingUserId)).findFirst().orElse(null);
        if (userFollowing != null) {
          return true;
        }
      } catch (Exception e) {
        return false;
      }
    }
  }

  public boolean isUserRetweet(String userId, String tweetId, long date) {
    try {
      String continuationToken = "";
      while (true) {
        TwitterUserTweet res = StringUtils.isBlank(continuationToken) ?
          twitterClient.getUserTweet(userId, false) :
          twitterClient.getUserTweetContinuation(userId, false, continuationToken);

        if (StringUtils.isNotBlank(res.continuationToken)) {
          continuationToken = res.continuationToken;
        }
        if (res.results == null || res.results.isEmpty()) {
          return false;
        }

        TwitterUserTweet.TwitterTweet userRetweet = res.results.stream()
          .filter(replies -> replies.retweet && replies.retweetId.equalsIgnoreCase(tweetId)).findFirst().orElse(null);
        if (userRetweet != null) {
          return true;
        }
        if (res.results.get(res.results.size() - 1).timestamp < date) {
          return false;
        }
      }
    } catch (Exception e) {
      try {
        Thread.sleep(twitterConfig.rapidapiSleep());
      } catch (InterruptedException ex) {
      }
      return false;
    }
  }

  public boolean isUserReply(String userId, String tweetId, long date) {
    try {
      String continuationToken = "";
      while (true) {
        TwitterUserTweet res = StringUtils.isBlank(continuationToken) ?
          twitterClient.getUserTweet(userId, true) :
          twitterClient.getUserTweetContinuation(userId, true, continuationToken);

        if (StringUtils.isNotBlank(res.continuationToken)) {
          continuationToken = res.continuationToken;
        }
        if (res.results == null || res.results.isEmpty()) {
          return false;
        }

        TwitterUserTweet.TwitterTweet userRetweet = res.results.stream()
          .filter(replies -> !replies.retweet && replies.replyId.equalsIgnoreCase(tweetId)).findFirst().orElse(null);
        if (userRetweet != null) {
          return true;
        }
        if (res.results.get(res.results.size() - 1).timestamp < date) {
          return false;
        }
      }
    } catch (Exception e) {
      try {
        Thread.sleep(twitterConfig.rapidapiSleep());
      } catch (InterruptedException ex) {
      }
      return false;
    }
  }

  public boolean isUserQuote(String userId, String tweetId, long date) {
    try {
      String continuationToken = "";
      while (true) {
        TwitterUserTweet res = StringUtils.isBlank(continuationToken) ?
          twitterClient.getUserTweet(userId, false) :
          twitterClient.getUserTweetContinuation(userId, false, continuationToken);

        if (StringUtils.isNotBlank(res.continuationToken)) {
          continuationToken = res.continuationToken;
        }
        if (res.results == null || res.results.isEmpty()) {
          return false;
        }

        TwitterUserTweet.TwitterTweet userRetweet = res.results.stream()
          .filter(replies -> StringUtils.isNotBlank(replies.quoteId) && replies.quoteId.equalsIgnoreCase(tweetId))
          .findFirst().orElse(null);
        if (userRetweet != null) {
          return true;
        }
        if (res.results.get(res.results.size() - 1).timestamp < date) {
          return false;
        }
      }
    } catch (Exception e) {
      try {
        Thread.sleep(twitterConfig.rapidapiSleep());
      } catch (InterruptedException ex) {
      }
      return false;
    }
  }
}
