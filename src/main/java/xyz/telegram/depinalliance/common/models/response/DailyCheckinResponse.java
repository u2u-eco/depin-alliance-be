package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
public class DailyCheckinResponse {
  public String name;
  public long time;
  public BigDecimal point;
  public BigDecimal xp;
  public boolean isChecked;

  public DailyCheckinResponse(String name, long time, BigDecimal point, BigDecimal xp, boolean isChecked) {
    this.name = name;
    this.time = time;
    this.point = Utils.stripDecimalZeros(point);
    this.xp = Utils.stripDecimalZeros(xp);
    this.isChecked = isChecked;
  }
}
