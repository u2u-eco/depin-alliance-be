package xyz.telegram.depinalliance.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "levels")
public class Level extends BaseEntity {
  @Id
  public Long id;
  public String name;
}
