package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

/**
 * @author holden on 11-Oct-2024
 */
@Entity
@Table(name = "twitter_retweet")
public class TwitterRetweet extends BaseEntity {
  @Id
  @SequenceGenerator(name = "twitterRetweetSequence", sequenceName = "twitter_retweet_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "twitterRetweetSequence")
  public Long id;
  @Column(name = "tweet_id")
  public String tweetId;
  @Column(name = "user_id")
  public String userId;
  @Column(name = "continuation_token")
  public String continuationToken;
}
