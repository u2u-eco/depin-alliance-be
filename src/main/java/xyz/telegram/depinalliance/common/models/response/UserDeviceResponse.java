package xyz.telegram.depinalliance.common.models.response;

import java.math.BigDecimal;

/**
 * @author holden on 28-Aug-2024
 */
public class UserDeviceResponse {

  public String name;
  public int index;
  public int slotCpuUsed;
  public int slotRamUsed;
  public int slotGpuUsed;
  public int slotStorageUsed;
  public BigDecimal totalMiningPower;

  public UserDeviceResponse(String name, int index, int slotCpuUsed, int slotRamUsed, int slotGpuUsed,
    int slotStorageUsed, BigDecimal totalMiningPower) {
    this.name = name;
    this.index = index;
    this.slotCpuUsed = slotCpuUsed;
    this.slotRamUsed = slotRamUsed;
    this.slotGpuUsed = slotGpuUsed;
    this.slotStorageUsed = slotStorageUsed;
    this.totalMiningPower = totalMiningPower;
  }
}
