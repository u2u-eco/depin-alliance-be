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
    try {
      String continuationToken = "";
      while (true) {
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
      }
    } catch (Exception e) {
      return false;
    }
  }
}
