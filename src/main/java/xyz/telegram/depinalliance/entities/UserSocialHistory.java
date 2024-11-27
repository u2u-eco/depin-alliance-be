package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

/**
 * @author holden on 09-Oct-2024
 */
@Entity
@Table(name = "user_social_histories", uniqueConstraints = { @UniqueConstraint(columnNames = { "id", "user_id" }) })
public class UserSocialHistory extends BaseEntity {
  @Id
  @Column(name = "id")
  public Long twitterUid;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
}
