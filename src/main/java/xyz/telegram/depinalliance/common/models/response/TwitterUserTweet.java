package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author holden on 15-Oct-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterUserTweet {
  @JsonProperty("continuation_token")
  public String continuationToken;
  public List<TwitterTweet> results;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TwitterTweet {
    public long timestamp;
    public boolean retweet;
    @JsonProperty("retweet_tweet_id")
    public String retweetId;
    @JsonProperty("in_reply_to_status_id")
    public String replyId;
    @JsonProperty("quoted_status_id")
    public String quoteId;
  }
}
