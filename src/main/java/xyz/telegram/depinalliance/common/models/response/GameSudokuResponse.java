package xyz.telegram.depinalliance.common.models.response;

/**
 * @author holden on 04-Oct-2024
 */
public class GameSudokuResponse {
  public Long id;
  public String mission;

  public GameSudokuResponse(Long id, String mission) {
    this.id = id;
    this.mission = mission;
  }
}
