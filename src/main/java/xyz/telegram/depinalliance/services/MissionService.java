package xyz.telegram.depinalliance.services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.DailyCheckinResponse;
import xyz.telegram.depinalliance.common.models.response.UserMissionResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author holden on 26-Aug-2024
 */
@ApplicationScoped
public class MissionService {

  @Inject
  UserService userService;
  @Inject
  LeagueService leagueService;

  public List<DailyCheckinResponse> getListOfDailyCheckin(User user) {
    List<DailyCheckin> dailyCheckins = DailyCheckin.listAll(Sort.ascending("id"));
    Calendar calendar = Utils.getNewDay();
    long today = calendar.getTimeInMillis() / 1000;
    if (user.lastCheckIn == today) {
      calendar.setTimeInMillis(user.startCheckIn * 1000);
      return dailyCheckins.stream().map(dailyCheckin -> {
        long time = calendar.getTimeInMillis() / 1000;
        DailyCheckinResponse dailyCheckinResponse = new DailyCheckinResponse(dailyCheckin.name, time,
          dailyCheckin.point, dailyCheckin.xp, time <= today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return dailyCheckinResponse;
      }).collect(Collectors.toList());
    }

    if (today - user.lastCheckIn == 86400) {
      if ((today - user.startCheckIn) / 86400 == dailyCheckins.size()) {
        calendar.setTimeInMillis(today * 1000);
      } else {
        calendar.setTimeInMillis(user.startCheckIn * 1000);
      }
      return dailyCheckins.stream().map(dailyCheckin -> {
        long time = calendar.getTimeInMillis() / 1000;
        DailyCheckinResponse dailyCheckinResponse = new DailyCheckinResponse(dailyCheckin.name, time,
          dailyCheckin.point, dailyCheckin.xp, time < today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return dailyCheckinResponse;
      }).collect(Collectors.toList());
    }

    if (user.startCheckIn == 0 || today - user.lastCheckIn > 86400) {
      return dailyCheckins.stream().map(dailyCheckin -> {
        DailyCheckinResponse dailyCheckinResponse = new DailyCheckinResponse(dailyCheckin.name,
          calendar.getTimeInMillis() / 1000, dailyCheckin.point, dailyCheckin.xp, false);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return dailyCheckinResponse;
      }).collect(Collectors.toList());
    }
    return null;
  }

  @Transactional
  public BigDecimal checkin(User user) throws BusinessException {
    Calendar calendar = Utils.getNewDay();
    long today = calendar.getTimeInMillis() / 1000;
    if (user.lastCheckIn == today) {
      throw new BusinessException(ResponseMessageConstants.MISSION_CANNOT_CHECKIN_TODAY);
    }
    //chua checkin or miss
    if (user.startCheckIn == 0 || today - user.lastCheckIn > 86400) {
      DailyCheckin dailyCheckin = DailyCheckin.findById(1);
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("startCheckIn", today);
      paramsUser.put("lastCheckIn", today);
      paramsUser.put("point", dailyCheckin.point);
      paramsUser.put("xp", dailyCheckin.xp);
      User.updateUser(
        "startCheckIn = :startCheckIn, lastCheckIn = :lastCheckIn, point = point + :point, xp = xp + :xp where id = :id",
        paramsUser);
      if (dailyCheckin.xp != null && dailyCheckin.xp.compareTo(BigDecimal.ZERO) > 0) {
        userService.updateLevelByExp(user.id);
        leagueService.updateXp(user, dailyCheckin.xp);
      }
      return Utils.stripDecimalZeros(dailyCheckin.point);
    }
    //checkin lien tiep
    if (today - user.lastCheckIn == 86400) {
      long day = ((today - user.startCheckIn) / 86400) + 1;
      long countDays = DailyCheckin.count();
      String sql = "";
      DailyCheckin dailyCheckin;
      Map<String, Object> paramsUser = new HashMap<>();
      if (day == countDays) {
        dailyCheckin = DailyCheckin.findById(1);
        paramsUser.put("startCheckIn", today);
        sql += "startCheckIn = :startCheckIn";
      } else {
        dailyCheckin = DailyCheckin.findById(day);
      }

      paramsUser.put("id", user.id);
      paramsUser.put("lastCheckIn", today);
      paramsUser.put("point", dailyCheckin.point);
      paramsUser.put("xp", dailyCheckin.xp);
      User.updateUser(sql + "lastCheckIn = :lastCheckIn, point = point + :point, xp = xp + :xp where id = :id",
        paramsUser);
      if (dailyCheckin.xp != null && dailyCheckin.xp.compareTo(BigDecimal.ZERO) > 0) {
        userService.updateLevelByExp(user.id);
        leagueService.updateXp(user, dailyCheckin.xp);
      }
      return Utils.stripDecimalZeros(dailyCheckin.point);
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  }

  @Transactional
  public boolean verify(User user, long missionId) throws BusinessException {
    UserMissionResponse check = Mission.findByUserIdAndMissionId(user.id, missionId);
    if (check == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (check.status != null && check.status != Enums.MissionStatus.NOT_VERIFIED) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    boolean isChecked = false;
    if (check.isFake) {
      isChecked = true;
    } else {
      if (check.type == Enums.MissionType.ON_TIME_IN_APP) {
        switch (check.missionRequire) {
        case CLAIM_FIRST_10000_POINT:
          if (user.pointClaimed.compareTo(new BigDecimal("10000")) >= 0) {
            isChecked = true;
          }
          break;
        case BUY_ANY_DEVICE:
          if (UserItemTradeHistory.countBuy(user.id) > 0) {
            isChecked = true;
          }
          break;
        case LEARN_ANY_SKILL:
          if (HistoryUpgradeSkill.countUpgradeSkillByUserId(user.id) > 0) {
            isChecked = true;
          }
          break;
        }
      }
    }
    if (isChecked) {
      UserMission userMission = new UserMission();
      userMission.mission = new Mission(check.id);
      userMission.user = user;
      userMission.status = Enums.MissionStatus.VERIFIED;
      UserMission.create(userMission);
      return true;
    }
    return false;
  }

  @Transactional
  public boolean claim(User user, long missionId) throws BusinessException {
    UserMissionResponse check = Mission.findByUserIdAndMissionId(user.id, missionId);
    if (check == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (check.status != Enums.MissionStatus.VERIFIED) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("missionId", missionId);
    params.put("userId", user.id);
    params.put("status", Enums.MissionStatus.CLAIMED);
    if (UserMission.updateObject("status = :status where user.id = :userId and mission.id = :missionId", params) > 0) {
      User.updatePointAndXpUser(user.id, check.point, check.xp);
      if (check.xp != null && check.xp.compareTo(BigDecimal.ZERO) > 0) {
        userService.updateLevelByExp(user.id);
        leagueService.updateXp(user, check.xp);
      }
      return true;
    }
    return false;
  }
}
