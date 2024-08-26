package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * @author holden on 10-Apr-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceResponse {
  public String symbol;
  public BigDecimal price;
}
