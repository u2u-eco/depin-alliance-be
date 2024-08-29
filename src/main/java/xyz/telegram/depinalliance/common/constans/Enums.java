package xyz.telegram.depinalliance.common.constans;

/**
 * @author holden on 13-Aug-2024
 */
public class Enums {

  public enum Config {
    POINT_REF(1), CPU_SLOT(2), GPU_SLOT(3), RAM_SLOT(4), STORAGE_SLOT(5), CPU_DEFAULT(6), GPU_DEFAULT(7), RAM_DEFAULT(
      8), STORAGE_DEFAULT(9), AVATAR_DEFAULT(10), AVATAR_LIST(11), MAX_MINING_POWER_DEFAULT(12), REF_POINT_CLAIM(
      13), ROOT_POINT_CLAIM(14);
    private final int type;

    Config(int type) {
      this.type = type;
    }

    public int getType() {
      return this.type;
    }
  }

  public enum UserStatus {
    STARTED, DETECTED_DEVICE_INFO, CLAIMED, MINING
  }

  public enum ItemType {
    CPU, GPU, RAM, STORAGE
  }

  public enum MissionType {
    TELEGRAM, TWITTER, QUIZ
  }

  public enum MissionStatus {
    VERIFIED, CLAIMED
  }
}
