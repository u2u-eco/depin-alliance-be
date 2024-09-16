package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "system_configs")
public class SystemConfig extends PanacheEntityBase {
  @Id
  public Integer id;
  @Column(length = 1000)
  public String value;

  public SystemConfig(Enums.Config config, String value) {
    this.id = config.getType();
    this.value = value;
  }

  public SystemConfig() {
  }

//  public static String findByKey(Enums.Config config) {
//    SystemConfig systemConfig = findById(config.getType());
//    return systemConfig != null ? systemConfig.value : null;
//  }
//
//  public static String findByKey(Enums.Config config, String defaultValue) {
//    String value = findByKey(config);
//    return StringUtils.isNotBlank(value) ? value : defaultValue;
//  }
}
