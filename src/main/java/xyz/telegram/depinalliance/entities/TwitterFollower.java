package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

/**
 * @author holden on 11-Oct-2024
 */
@Entity
@Table(name = "twitter_followers")
public class TwitterFollower extends BaseEntity {
  @Id
  @SequenceGenerator(name = "twitterFollowerSequence", sequenceName = "twitter_follower_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "twitterFollowerSequence")
  public Long id;
  @Column(name = "twitter_id")
  public String twitterId;
  @Column(name = "user_id")
  public String userId;
  @Column(name = "continuation_token")
  public String continuationToken;
}
