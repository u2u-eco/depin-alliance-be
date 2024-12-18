package xyz.telegram.depinalliance.common.constans;

/**
 * @author holden on 13-Aug-2024
 */
public class Enums {

  public enum Config {
    POINT_REF(1), CPU_SLOT(2), GPU_SLOT(3), RAM_SLOT(4), STORAGE_SLOT(5), CPU_DEFAULT(6), GPU_DEFAULT(7), RAM_DEFAULT(
      8), STORAGE_DEFAULT(9), AVATAR_DEFAULT(10), AVATAR_LIST(11), MAX_MINING_POWER_DEFAULT(12), REF_POINT_CLAIM(
      13), ROOT_POINT_CLAIM(14), POINT_BUY_DEVICE(15), BONUS_REWARD_DEFAULT(16), RANDOM_PERCENT_TIMPI(
      17), RANDOM_PERCENT_FLASHBACK(18), WORLD_MAP_BASE_REWARD_POINT(19), WORLD_MAP_DAILY_COMBO_POINT(
      20), WORLD_MAP_BONUS_CONTINENT_PERCENT(21), WORLD_MAP_BONUS_AGENCY_PERCENT(22), WORLD_MAP_BONUS_TOOL_PERCENT(
      23), RANDOM_PERCENT_VENTURE_MIND_AI(24);
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
    CPU, GPU, RAM, STORAGE, SPECIAL
  }

  public enum ItemSpecial {
    CYBER_BOX, OPEN_MESH, NTMPI, FLASHBACK, USDT_5
  }

  public enum MissionType {
    TELEGRAM, TWITTER, QUIZ, ON_TIME_IN_APP, SHARE_STORY, DOWNLOAD_APP, URL, PLAY_MINI_TON, RETWEETS, TWEET_REPLIES, CONNECT_X, FOLLOW_TWITTER, LIKE_TWITTER, CONNECT_OKX_WALLET_EVM, CONNECT_OKX_WALLET_TON, TWEET_QUOTE
  }

  public enum MissionRequire {
    LEVEL_5, LEVEL_10, LEVEL_20, LEVEL_35, LEVEL_50, CLAIM_FIRST_10000_POINT, BUY_ANY_DEVICE, LEARN_ANY_SKILL, JOIN_ANY_LEAGUE, CONTRIBUTE_TO_LEAGUE, INVITE_1, INVITE_5, INVITE_10, EVENT_INVITE_3, EVENT_INVITE_8, EVENT_INVITE_13, EVENT_INVITE_18, EVENT_INVITE_23, EVENT_INVITE_28, EVENT_INVITE_33, EVENT_INVITE_38, EVENT_INVITE_43, EVENT_INVITE_48, EVENT_INVITE_53, EVENT_INVITE_58, EVENT_INVITE_63, EVENT_INVITE_68, EVENT_INVITE_73, EVENT_INVITE_78, EVENT_INVITE_83, EVENT_INVITE_88, EVENT_INVITE_93, EVENT_INVITE_98
  }

  public enum MissionStatus {
    VERIFIED, CLAIMED, NOT_VERIFIED, VERIFYING
  }

  public enum MissionRewardType {
    CYBER_BOX, OPEN_MESH, TIMPI, FLASHBACK, VENTURE_MIND_AI
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

  public enum EventTableType {
    POINT, USDT, DEVICE
  }

  public enum LeagueJoinRequestStatus {
    PENDING, APPROVED, REJECTED, CANCELLED
  }

  public enum LeagueMemberType {
    CREATE, JOIN, KICK, LEAVE, TURN_ON_ADMIN_REQUEST, TURN_OFF_ADMIN_REQUEST, TURN_ON_ADMIN_KICK, TURN_OFF_ADMIN_KICK, CHANGE_ADMIN
  }

  public enum LeagueRole {
    ADMIN_REQUEST, ADMIN_KICK
  }

  public enum UserSettings {
    NOTIFICATION, MUSIC_THEME, SOUND_EFFECT
  }

  public enum EventId {
    CYBER_BOX(1), OPEN_MESH(2), TIMPI(3), FLASHBACK(4), VENTURE_MIND_AI(5);
    private final long id;

    EventId(long id) {
      this.id = id;
    }

    public long getId() {
      return this.id;
    }
  }

  public enum WorldMapItemType {
    AGENCY, TOOL, CONTINENT
  }

  public enum WorldMapType {
    SUDOKU, PUZZLE, MONSTER, SOLVE_MATH, TAP_RAT
  }
}
