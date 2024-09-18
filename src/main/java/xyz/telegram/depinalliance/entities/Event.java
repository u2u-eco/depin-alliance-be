package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 06-Sep-2024
 */
@Entity
@Table(name = "events")
public class Event extends BaseEntity {
  @Id
  @SequenceGenerator(name = "eventSequence", sequenceName = "event_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventSequence")
  public Long id;
  public String name;
  public String description;
  @Column(name = "is_active")
  public Boolean isActive;
  public String code;
  @Column(name = "total_usdt", scale = 18, precision = 29)
  public BigDecimal totalUsdt;
  @Column(name = "max_usdt", scale = 18, precision = 29)
  public BigDecimal maxUsdt;

  public Event(Long id) {
    this.id = id;
  }

  public Event() {
  }

  public static boolean updateTotalUsdt(BigDecimal usdt, long id) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("usdt", usdt);
    return update("totalUsdt = totalUsdt + :usdt where id = :id and totalUsdt + :usdt <= maxUsdt", params) > 0;
  }
}
