package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

/**
 * @author holden on 09-Oct-2024
 */
@Entity
@Table(name = "user_social")
public class UserSocial extends PanacheEntityBase {
  @Id
  @Column(name = "id")
  public Long userId;
  @Column(name = "twitter_name")
  public String twitterName;
  @Column(name = "twitter_uid", unique = true)
  public Long twitterUid;
  @Column(name = "twitter_username")
  public String twitterUsername;
  @Column(name = "twitter_token")
  public String twitterToken;
}
