package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * @author holden on 09-Sep-2024
 */
@Entity
@Table(name = "device_point")
public class DevicePoint extends PanacheEntityBase {
  @Id
  @SequenceGenerator(name = "devicePointSequence", sequenceName = "device_point_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devicePointSequence")
  public Long id;
  public String name;
  //  @Column(name = "name_normalize")
  //  public String nameNormalize;
  public String platform;
  public BigDecimal point;

}
