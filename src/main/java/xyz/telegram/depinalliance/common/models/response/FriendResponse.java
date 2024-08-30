package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

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
    this.pointRef = pointRef.setScale(0);
  }
}
