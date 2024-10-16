package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.RequestToken;
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

  public RequestToken getRequestToken() throws TwitterException {
    return getTwitter().getOAuthRequestToken(twitterConfig.callbackUrl());
  }

  public User verifyCredentials(String oauthToken, String oauthVerifier) throws TwitterException {
    Twitter twitter = getTwitter();
    RequestToken requestToken = new RequestToken(oauthToken, twitterConfig.apiSecretKey());
    twitter.setOAuthAccessToken(twitter.getOAuthAccessToken(requestToken, oauthVerifier));
    return twitter.verifyCredentials();
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

  //  public synchronized boolean isUserRetweets(String userId, String tweetId) {
  //    try {
  //      String continuationToken = "";
  //      while (true) {
  //        TwitterRetweetsResponse res = StringUtils.isBlank(continuationToken) ?
  //          twitterClient.getRetweets(tweetId) :
  //          twitterClient.getRetweetsContinuation(tweetId, continuationToken);
  //        Thread.sleep(twitterConfig.rapidapiSleep());
  //        if (StringUtils.isNotBlank(res.continuationToken)) {
  //          continuationToken = res.continuationToken;
  //        }
  //        if (res.retweets == null || res.retweets.isEmpty()) {
  //          return false;
  //        }
  //
  //        TwitterRetweetsResponse.TwitterRetweets userRetweet = res.retweets.stream()
  //          .filter(user -> user.userId.equalsIgnoreCase(userId)).findFirst().orElse(null);
  //        if (userRetweet != null) {
  //          return true;
  //        }
  //      }
  //    } catch (Exception e) {
  //      return false;
  //    }
  //  }

  //  public synchronized boolean isUserReplies(String userId, String tweetId) {
  //    try {
  //      String continuationToken = "";
  //      while (true) {
  //        TwitterRepliesResponse res = StringUtils.isBlank(continuationToken) ?
  //          twitterClient.getReplies(tweetId) :
  //          twitterClient.getRepliesContinuation(tweetId, continuationToken);
  //        Thread.sleep(twitterConfig.rapidapiSleep());
  //        if (StringUtils.isNotBlank(res.continuationToken)) {
  //          continuationToken = res.continuationToken;
  //        }
  //        if (res.replies == null || res.replies.isEmpty()) {
  //          return false;
  //        }
  //
  //        res.replies.forEach(replies -> {
  //          System.out.println(
  //            "Username " + replies.user.username + " userId " + replies.user.userId + " reply at " + replies.creationDate);
  //        });
  //        TwitterRepliesResponse.TwitterReplies userRetweet = res.replies.stream()
  //          .filter(replies -> replies.user.userId.equalsIgnoreCase(userId)).findFirst().orElse(null);
  //        if (userRetweet != null) {
  //          return true;
  //        }
  //      }
  //    } catch (Exception e) {
  //      return false;
  //    }
  //  }

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
}
