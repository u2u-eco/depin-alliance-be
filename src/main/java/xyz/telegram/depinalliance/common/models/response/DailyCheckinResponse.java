package xyz.telegram.depinalliance.common.models.response;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
public class DailyCheckinResponse {
  public String name;
  public long time;
  public BigDecimal point;
  public boolean isChecked;

  public DailyCheckinResponse(String name, long time, BigDecimal point, boolean isChecked) {
    this.name = name;
    this.time = time;
    this.point = point;
    this.isChecked = isChecked;
  }
}
