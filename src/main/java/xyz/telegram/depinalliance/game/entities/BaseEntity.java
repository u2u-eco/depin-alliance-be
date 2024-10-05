package xyz.telegram.depinalliance.game.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import xyz.telegram.depinalliance.common.utils.Utils;

import java.io.Serializable;

/**
 * @author holden on 25-Jul-2024
 */
@MappedSuperclass
public class BaseEntity extends PanacheEntityBase implements Serializable {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "createdAt")
  @Column(name = "created_at")
  public Long createdAt;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "updatedAt")
  @Column(name = "updated_at")
  public Long updatedAt;

  public BaseEntity create() {
    long time = Utils.getCalendar().getTimeInMillis();
    createdAt = time;
    updatedAt = time;
    return this;
  }

  public BaseEntity update() {
    updatedAt = Utils.getCalendar().getTimeInMillis();
    return this;
  }
}