package xyz.telegram.depinalliance.common.models.response;

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
  public int slotCpuMax;
  public int slotGpuMax;
  public int slotRamMax;
  public int slotStorageMax;

  public UserDeviceResponse(String name, int index, int slotCpuUsed, int slotRamUsed, int slotGpuUsed,
    int slotStorageUsed) {
    this.name = name;
    this.index = index;
    this.slotCpuUsed = slotCpuUsed;
    this.slotRamUsed = slotRamUsed;
    this.slotGpuUsed = slotGpuUsed;
    this.slotStorageUsed = slotStorageUsed;
  }
}
