package xyz.telegram.depinalliance.common.constans;

/**
 * @author holden on 13-Aug-2024
 */
public class Enums {

  public enum Config {
    POINT_REF(1), CPU_SLOT(2), GPU_SLOT(3), RAM_SLOT(4), STORAGE_SLOT(5), CPU_DEFAULT(6), GPU_DEFAULT(7), RAM_DEFAULT(
      8), STORAGE_DEFAULT(9), AVATAR_DEFAULT(10), AVATAR_LIST(11), MAX_MINING_POWER_DEFAULT(12), REF_POINT_CLAIM(
      13), ROOT_POINT_CLAIM(14), POINT_BUY_DEVICE(15), BONUS_REWARD_DEFAULT(16);
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

  public enum SkillUpgradeStatus {
    NEW, DONE
  }

  public enum ItemType {
    CPU, GPU, RAM, STORAGE, SPECIAL
  }

  public enum MissionType {
    TELEGRAM, TWITTER, QUIZ, ON_TIME_IN_APP, SHARE_STORY
  }

  public enum MissionRequire {
    LEVEL_5, LEVEL_10, LEVEL_20, LEVEL_35, LEVEL_50, CLAIM_FIRST_10000_POINT, BUY_ANY_DEVICE, LEARN_ANY_SKILL, JOIN_ANY_LEAGUE, CONTRIBUTE_TO_LEAGUE, INVITE_1_FRIEND, INVITE_5_FRIEND, INVITE_10_FRIEND
  }

  public enum MissionStatus {
    VERIFIED, CLAIMED, NOT_VERIFIED
  }

  public enum FolderImage {
    LEAGUE("league");

    private final String folder;

    FolderImage(String folder) {
      this.folder = folder;
    }

    public String getFolder() {
      return folder;
    }
  }

  public enum EventHistoryType {
    MISSION, REF
  }
}
