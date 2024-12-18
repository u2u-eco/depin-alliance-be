package xyz.telegram.depinalliance.common.models.response;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author holden on 29-Aug-2024
 */
public class FriendResponse {
  public String username;
  public String avatar;
  public BigDecimal pointRef;

  public FriendResponse(String username, String avatar, BigDecimal pointRef) {
    this.username = username;
    this.avatar = avatar;
    this.pointRef = pointRef.setScale(0, RoundingMode.FLOOR);
  }
}
