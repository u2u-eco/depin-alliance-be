package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "system_configs")
public class SystemConfig extends PanacheEntityBase {
  @Id
  public Integer id;
  public String value;

  public SystemConfig(Integer id, String value) {
    this.id = id;
    this.value = value;
  }

  public SystemConfig() {
  }

  public static <T> T findByKey(Enums.Config config) {
    SystemConfig systemConfig = findById(config.getType());

    return systemConfig != null ? (T) systemConfig.value : null;
  }
}
