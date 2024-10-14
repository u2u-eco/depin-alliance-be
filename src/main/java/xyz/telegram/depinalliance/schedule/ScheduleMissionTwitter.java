package xyz.telegram.depinalliance.schedule;

import io.quarkus.panache.common.Sort;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import xyz.telegram.depinalliance.common.models.response.TwitterFollowResponse;
import xyz.telegram.depinalliance.common.models.response.TwitterRetweetsResponse;
import xyz.telegram.depinalliance.entities.TwitterFollower;
import xyz.telegram.depinalliance.entities.TwitterRetweet;
import xyz.telegram.depinalliance.services.TwitterClient;

import java.util.List;

@ApplicationScoped
public class ScheduleMissionTwitter {
  @Inject
  Logger logger;
  @RestClient
  TwitterClient twitterClient;

  //  @Scheduled(cron = "${expr.every.twitter}", identity = "task-twitter")
  @Scheduled(cron = "00 47 11 * * ?", identity = "task-twitter")
  void schedule() {
    //    crawlFollowers("1577973357773225984");
    crawlRetweet("1844328764903391596");
  }

  public void crawlFollowers(String twitterId) {
    TwitterFollower twitterFollower = TwitterFollower.find("twitterId = ?1 ", Sort.descending("createdAt"), twitterId)
      .firstResult();
    String continuationToken = "";
    if (twitterFollower != null) {
      continuationToken = twitterFollower.continuationToken;
    }
    while (true) {
      try {
        TwitterFollowResponse res = StringUtils.isBlank(continuationToken) ?
          twitterClient.getFollower(twitterId) :
          twitterClient.getFollowerContinuation(twitterId, continuationToken);
        if (StringUtils.isNotBlank(res.continuationToken)) {
          continuationToken = res.continuationToken;
        }
        if (res.results == null || res.results.isEmpty()) {
          break;
        }
        createTwitterFollower(res.results, twitterId, continuationToken);
      } catch (Exception e) {
        logger.error("Error follower " + continuationToken, e);
      }
    }
  }

  public void crawlRetweet(String tweetId) {
    TwitterRetweet twitterRetweet = TwitterRetweet.find("tweetId = ?1 ", Sort.descending("createdAt"), tweetId)
      .firstResult();
    String continuationToken = "";
    if (twitterRetweet != null) {
      continuationToken = twitterRetweet.continuationToken;
    }
    while (true) {
      try {
        TwitterRetweetsResponse res = StringUtils.isBlank(continuationToken) ?
          twitterClient.getRetweets(tweetId) :
          twitterClient.getRetweetsContinuation(tweetId, continuationToken);
        if (StringUtils.isNotBlank(res.continuationToken)) {
          continuationToken = res.continuationToken;
        }
        if (res.retweets == null || res.retweets.isEmpty()) {
          break;
        }
        createTwitterRetweet(res.retweets, tweetId, continuationToken);
      } catch (Exception e) {
        logger.error("Error Retweet " + continuationToken, e);
      }
    }
  }

  @Transactional
  public void createTwitterFollower(List<TwitterFollowResponse.TwitterUser> results, String twitterId,
    String continuationToken) {
    for (TwitterFollowResponse.TwitterUser user : results) {
      try {
        TwitterFollower twitterFollower = new TwitterFollower();
        twitterFollower.twitterId = twitterId;
        twitterFollower.userId = user.userId;
        twitterFollower.continuationToken = continuationToken;
        twitterFollower.create();
        twitterFollower.persistAndFlush();
      } catch (Exception e) {
        logger.error("Error save " + twitterId + " " + continuationToken, e);
      }
    }
    logger.info("Save success Follower continuationToken " + continuationToken);
  }

  @Transactional
  public void createTwitterRetweet(List<TwitterRetweetsResponse.TwitterRetweets> retweets, String tweetId,
    String continuationToken) {
    for (TwitterRetweetsResponse.TwitterRetweets retweet : retweets) {
      try {
        TwitterRetweet twitterRetweet = new TwitterRetweet();
        twitterRetweet.tweetId = tweetId;
        twitterRetweet.userId = retweet.userId;
        twitterRetweet.continuationToken = continuationToken;
        twitterRetweet.create();
        twitterRetweet.persistAndFlush();
      } catch (Exception e) {
        logger.error("Error save " + tweetId + " " + continuationToken, e);
      }
    }
    logger.info("Save success Retweet continuationToken " + continuationToken);
  }
}
