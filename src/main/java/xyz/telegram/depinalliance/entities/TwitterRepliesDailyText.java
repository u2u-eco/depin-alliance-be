package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author holden on 30-Oct-2024
 */
@Entity
@Table(name = "twitter_replies_daily_text")
public class TwitterRepliesDailyText extends PanacheEntityBase {
  @Id
  public Long id;
  public String text;
}
