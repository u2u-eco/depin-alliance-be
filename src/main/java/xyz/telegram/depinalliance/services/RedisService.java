package xyz.telegram.depinalliance.services;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.*;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;
import xyz.telegram.depinalliance.entities.GameDaily;
import xyz.telegram.depinalliance.entities.GameItem;
import xyz.telegram.depinalliance.entities.GameResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author holden on 26-Aug-2024
 */
@ApplicationScoped
public class RedisService {

  @Inject
  RedissonClient redissonClient;
  @Inject
  Logger logger;

  @ConfigProperty(name = "redis.time-out", defaultValue = "300")
  long timeOut;

  public int getSystemConfigInt(Enums.Config config) {
    return Integer.parseInt(Objects.requireNonNull(findConfigByKey(config)));
  }

  public String findConfigByKey(Enums.Config config) {
    try {
      String redisKey = "CONFIG_" + config.name();
      RBucket<String> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache system config " + config.name() + " ttl 1 day ");
      SystemConfig systemConfig = SystemConfig.findById(config.getType());
      String valueStr = systemConfig != null ? systemConfig.value : null;
      value.setAsync(valueStr, 1, TimeUnit.DAYS);
      return valueStr;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding system config " + config.name());
    }
    SystemConfig systemConfig = SystemConfig.findById(config.getType());
    return systemConfig != null ? systemConfig.value : null;
  }

  public Item findItemByCode(String code) {
    try {
      String redisKey = "ITEM_CODE_" + code.toUpperCase();
      RBucket<Item> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache item code " + code.toUpperCase() + " ttl : 1 day");
      Item item = Item.find("code", code.toUpperCase()).firstResult();
      if (item != null) {
        value.setAsync(item, 1, TimeUnit.DAYS);
      }
      return item;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding item code " + code.toUpperCase());
    }
    return Item.find("code", code.toUpperCase()).firstResult();
  }

  public Item findItemById(Long id) {
    try {
      String redisKey = "ITEM_ID_" + id;
      RBucket<Item> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache item id " + id + " ttl : 1 day");
      Item item = Item.findById(id);
      if (item != null) {
        value.setAsync(item, 1, TimeUnit.DAYS);
      }
      return item;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding item id " + id);
    }
    return Item.findById(id);
  }

  public Level findLevelById(Long id) {
    try {
      String redisKey = "LEVEL_ID_" + id;
      RBucket<Level> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache level id " + id + " ttl : 1 day");
      Level object = Level.findById(id);
      value.setAsync(object, 1, TimeUnit.DAYS);
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding level id " + id);
    }
    return Level.findById(id);
  }

  public Skill findSkillById(Long id) {
    try {
      String redisKey = "SKILL_ID_" + id;
      RBucket<Skill> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache skill id " + id + " ttl : 1 day");
      Skill object = Skill.findById(id);
      value.setAsync(object, 1, TimeUnit.DAYS);
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding skill id " + id);
    }
    return Skill.findById(id);
  }

  public Long findMaxLevel() {
    try {
      String redisKey = "LEVEL_MAX";
      RBucket<Long> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache max level ttl : 1 day");
      Long object = Level.maxLevel();
      value.setAsync(object, 1, TimeUnit.DAYS);
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding level");
    }
    return Level.maxLevel();
  }

  public League findLeagueById(Long id, boolean isUseCache) {
    if (isUseCache) {
      try {
        String redisKey = "LEAGUE_ID_" + id;
        RBucket<LeagueRedis> value = redissonClient.getBucket(redisKey);
        if (value.isExists()) {
          return value.get().getLeague();
        }
        logger.info("Get from db and set cache league id " + id + " ttl : " + timeOut);
        League object = League.findById(id);
        if (object != null) {
          value.setAsync(new LeagueRedis(object.id, object.user.id, object.code), timeOut, TimeUnit.SECONDS);
        }
        return object;
      } catch (Exception e) {
        logger.errorv(e, "Error while finding league id " + id);
      }
    }
    return League.findById(id);
  }

  public League findLeagueByCode(String code, boolean isUseCache) {
    if (isUseCache) {
      try {
        String redisKey = "LEAGUE_CODE_" + code;
        RBucket<LeagueRedis> value = redissonClient.getBucket(redisKey);
        if (value.isExists()) {
          return value.get().getLeague();
        }
        logger.info("Get from db and set cache league code " + code + " ttl : " + timeOut);
        League object = League.findByCode(code);
        if (object != null) {
          value.setAsync(new LeagueRedis(object.id, object.user.id, object.code), timeOut, TimeUnit.SECONDS);
        }
        return object;
      } catch (Exception e) {
        logger.errorv(e, "Error while finding league code " + code);
      }
    }
    return League.findByCode(code);
  }

  public List<Long> findListAdminLeagueByRoleAndLeague(Long leagueId, Enums.LeagueRole role) {
    String redisKey = "ADMIN_LEAGUE_" + leagueId + "_" + role;
    try {
      RBucket<List<Long>> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache list " + redisKey + " ttl : " + timeOut);
      List<Long> object = LeagueMember.find(
        "select user.id from LeagueMember where league.id = :leagueId and leagueRole like :role",
        Parameters.with("leagueId", leagueId).and("role", "%" + role.name() + "%")).project(Long.class).list();
      if (object != null) {
        value.setAsync(object, timeOut, TimeUnit.SECONDS);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding " + redisKey);
    }
    return LeagueMember.find("select user.id from LeagueMember where league.id = :leagueId and leagueRole like :role",
      Parameters.with("leagueId", leagueId).and("role", "%" + role.name() + "%")).project(Long.class).list();
  }

  public LeagueMember findLeagueMemberByUserId(long userId) {
    String redisKey = "LEAGUE_MEMBER_" + userId;
    try {
      RBucket<LeagueMemberRedis> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get().getLeagueMember();
      }
      logger.info("Get from db and set cache " + redisKey + " ttl : " + timeOut);
      LeagueMember object = LeagueMember.find("user.id = ?1", userId).firstResult();
      if (object != null) {
        value.setAsync(
          new LeagueMemberRedis(object.id, object.user.id, object.league.id, object.isAdmin, object.leagueRole,
            object.pointFunding, object.contributeProfit), timeOut, TimeUnit.SECONDS);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding " + redisKey);
    }
    return LeagueMember.find("user.id = ?1", userId).firstResult();
  }

  public BigDecimal findRefPercentClaimById(Long id, boolean isUseCache) {
    if (isUseCache) {
      try {
        String redisKey = "REF_PERCENT_CLAIM_ID_" + id;
        RBucket<BigDecimal> value = redissonClient.getBucket(redisKey);
        if (value.isExists()) {
          return value.get();
        }
        logger.info("Get from db and set cache ref percent claim by id  " + id + " ttl : " + timeOut);
        User object = User.findById(id);
        if (object != null) {
          value.setAsync(object.refPercentClaim, timeOut, TimeUnit.SECONDS);
          return object.refPercentClaim;
        }
        return BigDecimal.ZERO;
      } catch (Exception e) {
        logger.errorv(e, "Error while finding ref percent claim by id " + id);
      }
    }
    return BigDecimal.ZERO;
  }

  public List<DailyCheckin> findFirstCheckin() {
    try {
      String redisKey = "DAILY_CHECKIN_LIST_FIRST";
      RBucket<List<DailyCheckin>> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache daily checkin list first");
      List<DailyCheckin> object = DailyCheckin.findAll(Sort.ascending("id")).page(0, 8).list();
      if (object != null) {
        value.setAsync(object);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding daily checkin list first");
    }
    return DailyCheckin.findAll(Sort.ascending("id")).page(0, 8).list();
  }

  public List<DailyCheckin> findListDailyCheckinByDay(long dayCheckin) {
    try {
      String redisKey = "DAILY_CHECKIN_LIST_" + dayCheckin;
      RBucket<List<DailyCheckin>> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache daily checkin list by day " + dayCheckin);
      List<DailyCheckin> object = DailyCheckin.list("id >= ?1 and id <= ?2", Sort.ascending("id"), dayCheckin - 2,
        dayCheckin + 5);
      if (object != null) {
        value.setAsync(object);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding daily checkin by day " + dayCheckin);
    }
    return DailyCheckin.list("id >= ?1 and id <= ?2", Sort.ascending("id"), dayCheckin - 2, dayCheckin + 5);
  }

  public DailyCheckin findDailyCheckinByDay(long dayCheckin) {
    try {
      String redisKey = "DAILY_CHECKIN_" + dayCheckin;
      RBucket<DailyCheckin> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache daily checkin by day " + dayCheckin);
      DailyCheckin object = DailyCheckin.findById(dayCheckin);
      if (object != null) {
        value.setAsync(object);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding daily by day " + dayCheckin);
    }
    return DailyCheckin.findById(dayCheckin);
  }

  public long findDailyCheckinCount() {
    try {
      String redisKey = "DAILY_CHECKIN_COUNT";
      RBucket<Long> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache daily checkin count ");
      Long object = DailyCheckin.count();
      if (object != null) {
        value.setAsync(object);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding daily count ");
    }
    return DailyCheckin.count();
  }

  public List<UserMissionResponse> findMissionRewardNotOneTime(long userId, boolean isPartner) {
    try {
      String redisKey = "MISSION_REWARD_NOT_ONE_TIME_" + userId + "_" + isPartner;
      RBucket<List<UserMissionResponse>> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info(
        "Get from db and set cache mission reward not one time " + userId + " isPartner " + isPartner + " ttl : 1 days");
      List<UserMissionResponse> object = Mission.findByUserId(userId, isPartner);
      if (object != null) {
        value.setAsync(object, 1, TimeUnit.DAYS);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding mission reward not one time " + userId + " isPartner " + isPartner);
    }
    return Mission.findByUserId(userId, isPartner);
  }

  public List<PartnerResponse> findPartner() {
    String redisKey = "MISSION_PARTNER";
    try {
      RBucket<List<PartnerResponse>> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache " + redisKey + " ttl : 5 minutes");
      List<PartnerResponse> object = Partner.findAllPartner();
      if (object != null) {
        value.setAsync(object, 5, TimeUnit.MINUTES);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding " + redisKey);
    }
    return Partner.findAllPartner();
  }

  public List<UserMissionResponse> findMissionRewardOneTime(long userId) {
    try {
      String redisKey = "MISSION_REWARD_ONE_TIME_" + userId;
      RBucket<List<UserMissionResponse>> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache mission reward one time " + userId + " ttl : 1 days");
      List<UserMissionResponse> object = Mission.findTypeOnTimeInAppByUserId(userId);
      if (object != null) {
        value.setAsync(object, 1, TimeUnit.DAYS);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding mission reward one time " + userId);
    }
    return Mission.findTypeOnTimeInAppByUserId(userId);
  }

  public void clearMissionUser(String type, long userId) {
    String redisKey = "";
    switch (type.toUpperCase()) {
    case "REWARD":
      redisKey = "MISSION_REWARD_NOT_ONE_TIME_" + userId + "_false";
      break;
    case "REWARD_ONE_TIME":
      redisKey = "MISSION_REWARD_ONE_TIME_" + userId;
      break;
    case "PARTNER":
      redisKey = "MISSION_REWARD_NOT_ONE_TIME_" + userId + "_true";
      break;
    }
    RBucket<List<UserMissionResponse>> value = redissonClient.getBucket(redisKey);
    if (value.isExists()) {
      value.deleteAsync();
    }
  }

  public ResponsePage<ItemResponse> findListItemInShop(PagingParameters pageable, String type) {
    String redisKey = "ITEM_IN_SHOP_" + pageable.sortBy + "_" + pageable.sortAscending + "_" + pageable.page + "_" + pageable.size + "_" + type;
    try {
      RBucket<ResponsePage<ItemResponse>> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache list item in shop " + redisKey + " ttl : 1 days");
      ResponsePage<ItemResponse> object = Item.findByTypeAndPaging(pageable, type);
      value.setAsync(object, 1, TimeUnit.DAYS);
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding list item in shop " + redisKey);
    }
    return Item.findByTypeAndPaging(pageable, type);
  }

  public List<Level> findNextLevel(long levelId) {
    String redisKey = "NEXT_LEVEL_" + levelId;
    try {
      RBucket<List<Level>> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache list next level " + levelId + " ttl : 1 days");
      List<Level> object = Level.find("id > ?1", Sort.ascending("id"), levelId).page(0, 2).list();
      if (object != null) {
        value.setAsync(object, 1, TimeUnit.DAYS);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding list next level " + redisKey);
    }
    return Level.find("id > ?1", Sort.ascending("id"), levelId).page(0, 2).list();
  }

  public SettingResponse findSettingUserById(long userId) {
    String redisKey = "SETTING_USER_" + userId;
    try {
      RBucket<SettingResponse> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache " + redisKey + " ttl : 1 days");
      SettingResponse object = User.find("id = ?1", userId).project(SettingResponse.class).firstResult();
      if (object != null) {
        value.setAsync(object, 1, TimeUnit.DAYS);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding " + redisKey);
    }
    return User.find("id = ?1", userId).project(SettingResponse.class).firstResult();
  }

  public GameDailyResponse findGameDaily(long userId) {
    long dateTime = Utils.getNewDay().getTimeInMillis() / 1000;
    String redisKey = "GAME_DAILY_" + userId + "_" + dateTime;
    try {
      RBucket<GameDailyResponse> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      GameDaily gameDaily = GameDaily.find("user.id = ?1 and date = ?2", userId, dateTime).firstResult();
      if (gameDaily != null) {
        GameDailyResponse gameDailyResponse = new GameDailyResponse();
        gameDailyResponse.reward = gameDaily.reward;
        gameDailyResponse.agency = new GameItemResponse(findGameItemById(gameDaily.agency.id));
        gameDailyResponse.tool = new GameItemResponse(findGameItemById(gameDaily.tool.id));
        gameDailyResponse.continent = new GameItemResponse(findGameItemById(gameDaily.continent.id));
        gameDailyResponse.date = gameDaily.date;
        gameDailyResponse.time = gameDaily.time;
        gameDailyResponse.isCompleted = gameDaily.isCompleted;
        List<GameResult> gameResults = GameResult.list("gameDaily.id", gameDaily.id);
        gameResults.forEach(gameResult -> gameDailyResponse.results.add(new GameResultResponse(gameResult)));
        value.setAsync(gameDailyResponse, 1, TimeUnit.DAYS);
        return gameDailyResponse;
      }

    } catch (Exception e) {
      logger.errorv(e, "Error while finding " + redisKey);
    }
    return null;
  }

  public GameItem findGameItemByCode(String code) {
    String redisKey = "GAME_ITEM_CODE_" + code;
    try {
      RBucket<GameItem> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache " + redisKey + " ttl : 1 days");
      GameItem object = GameItem.find("code = ?1", code).firstResult();
      if (object != null) {
        value.setAsync(object, 1, TimeUnit.DAYS);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding " + redisKey);
    }
    return GameItem.find("code = ?1", code).firstResult();
  }

  public GameItem findGameItemById(long id) {
    String redisKey = "GAME_ITEM_ID_" + id;
    try {
      RBucket<GameItem> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache " + redisKey + " ttl : 1 days");
      GameItem object = GameItem.find("id = ?1", id).firstResult();
      if (object != null) {
        value.setAsync(object, 1, TimeUnit.DAYS);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding " + redisKey);
    }
    return GameItem.find("id = ?1", id).firstResult();
  }

  public List<GameItemResponse> findListGameItemByType(Enums.GameItemType type) {
    String redisKey = "GAME_ITEM_LIST_TYPE_" + type;
    try {
      RBucket<List<GameItemResponse>> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache " + redisKey + " ttl : 1 days");
      List<GameItemResponse> object = GameItem.find("type = ?1", type).project(GameItemResponse.class).list();
      if (object != null) {
        value.setAsync(object, 1, TimeUnit.DAYS);
      }
      return object;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding " + redisKey);
    }
    return GameItem.find("type = ?1", type).project(GameItemResponse.class).list();
  }

  public void clearCacheByPrefix(String prefix) {
    redissonClient.getKeys().deleteByPattern(prefix + "*");
  }

  public static class LeagueMemberRedis {
    public String id;
    public long userid;
    public long leagueId;
    public boolean isAdmin;
    public String leagueRole;
    public BigDecimal pointFunding;
    public BigDecimal contributeProfit;

    public LeagueMemberRedis(String id, long userid, long leagueId, boolean isAdmin, String leagueRole,
      BigDecimal pointFunding, BigDecimal contributeProfit) {
      this.id = id;
      this.userid = userid;
      this.leagueId = leagueId;
      this.isAdmin = isAdmin;
      this.leagueRole = leagueRole;
      this.pointFunding = pointFunding;
      this.contributeProfit = contributeProfit;
    }

    public LeagueMember getLeagueMember() {
      LeagueMember leagueMember = new LeagueMember();
      leagueMember.id = this.id;
      leagueMember.league = new League(this.leagueId);
      leagueMember.user = new User(this.userid);
      leagueMember.isAdmin = this.isAdmin;
      leagueMember.leagueRole = this.leagueRole;
      leagueMember.pointFunding = this.pointFunding;
      leagueMember.contributeProfit = this.contributeProfit;
      return leagueMember;
    }
  }

  public static class LeagueRedis {
    public Long id;
    public Long userId;
    public String code;

    public LeagueRedis() {
    }

    public LeagueRedis(Long id, Long userId, String code) {
      this.id = id;
      this.userId = userId;
      this.code = code;
    }

    public League getLeague() {
      League league = new League();
      league.id = this.id;
      league.code = this.code;
      league.user = new User(userId);
      return league;
    }
  }
}
