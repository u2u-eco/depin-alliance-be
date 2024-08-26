package xyz.telegram.depinalliance.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

/**
 * @author holden on 25-Jul-2024
 */
@MappedSuperclass
public class BaseEntity extends PanacheEntityBase implements Serializable {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "createdAt")
  @Column(name = "created_at")
  public long createdAt;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "updatedAt")
  @Column(name = "updated_at")
  public long updatedAt;

  public BaseEntity create() {
    long time = System.currentTimeMillis();
    createdAt = time;
    updatedAt = time;
    return this;
  }

  public BaseEntity update() {
    updatedAt = System.currentTimeMillis();
    return this;
  }
}