package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author holden on 04-Oct-2024
 */
@Entity
@Table(name = "game_sudoku")
public class GameSudoku extends PanacheEntityBase {
  @Id
  public Long id;
  public String mission;
  @Column(unique = true)
  public String solution;
}
