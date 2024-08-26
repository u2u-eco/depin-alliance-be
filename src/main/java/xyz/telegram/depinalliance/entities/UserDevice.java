package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

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
}
