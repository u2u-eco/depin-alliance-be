package xyz.telegram.depinalliance.entities;

import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.models.response.UserDeviceResponse;

import java.util.List;
import java.util.Map;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "user_devices")
public class UserDevice extends BaseEntity {
  @Id
  @SequenceGenerator(name = "userDeviceSequence", sequenceName = "user_device_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userDeviceSequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  public String name;
  public int index;
  @Column(name = "slot_cpu_used")
  public int slotCpuUsed = 0;
  @Column(name = "slot_ram_used")
  public int slotRamUsed = 0;
  @Column(name = "slot_gpu_used")
  public int slotGpuUsed = 0;
  @Column(name = "slot_storage_used")
  public int slotStorageUsed = 0;

  public static UserDevice create(UserDevice userDevice) {
    userDevice.create();
    userDevice.persist();
    return userDevice;
  }

  public static int updateObject(String query, Map<String, Object> params) {
    return update(query, params);
  }

  public static UserDevice findByUserAndIndex(long userId, int index) {
    return find("user.id = ?1 and index = ?2", userId, index).firstResult();
  }

  public static List<UserDeviceResponse> findByUser(long userId) {
    return find("user.id", Sort.ascending("index"), userId).project(UserDeviceResponse.class).list();
  }
}
