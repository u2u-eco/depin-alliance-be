package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import xyz.telegram.depinalliance.common.configs.TwitterConfig;

@ApplicationScoped
public class TwitterProducer {
  @Inject
  TwitterConfig twitterConfig;

  @Produces
  public TwitterFactory produceTwitterFactory() {
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setOAuthConsumerKey(twitterConfig.apiKey()).setOAuthConsumerSecret(twitterConfig.apiSecretKey());
    //    cb.setOAuthAccessToken(twitterConfig.accessToken()).setOAuthAccessTokenSecret(twitterConfig.accessTokenSecret());
    return new TwitterFactory(cb.build());
  }

}