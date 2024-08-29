package xyz.telegram.depinalliance.services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.DailyCheckinResponse;
import xyz.telegram.depinalliance.common.models.response.UserMissionResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.DailyCheckin;
import xyz.telegram.depinalliance.entities.Mission;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.entities.UserMission;

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

  public List<DailyCheckinResponse> getListOfDailyCheckin(User user) {
    List<DailyCheckin> dailyCheckins = DailyCheckin.listAll(Sort.ascending("id"));
    Calendar calendar = Utils.getNewDay();
    long today = calendar.getTimeInMillis() / 1000;
    if (user.lastCheckIn == today) {
      calendar.setTimeInMillis(user.startCheckIn * 1000);
      return dailyCheckins.stream().map(dailyCheckin -> {
        long time = calendar.getTimeInMillis() / 1000;
        DailyCheckinResponse dailyCheckinResponse = new DailyCheckinResponse(dailyCheckin.name, time,
          dailyCheckin.point, time <= today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return dailyCheckinResponse;
      }).collect(Collectors.toList());
    }

    if (today - user.lastCheckIn == 86400) {
      calendar.setTimeInMillis(user.startCheckIn * 1000);
      return dailyCheckins.stream().map(dailyCheckin -> {
        long time = calendar.getTimeInMillis() / 1000;
        DailyCheckinResponse dailyCheckinResponse = new DailyCheckinResponse(dailyCheckin.name, time,
          dailyCheckin.point, time < today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return dailyCheckinResponse;
      }).collect(Collectors.toList());
    }

    if (user.startCheckIn == 0 || today - user.lastCheckIn > 86400) {
      return dailyCheckins.stream().map(dailyCheckin -> {
        DailyCheckinResponse dailyCheckinResponse = new DailyCheckinResponse(dailyCheckin.name,
          calendar.getTimeInMillis() / 1000, dailyCheckin.point, false);
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
      User.updateUser("startCheckIn = :startCheckIn, lastCheckIn = :lastCheckIn, point = point + :point where id = :id",
        paramsUser);
      return Utils.stripDecimalZeros(dailyCheckin.point);
    }
    //checkin lien tiep
    if (today - user.lastCheckIn == 86400) {
      long day = ((today - user.startCheckIn) / 86400) + 1;
      DailyCheckin dailyCheckin = DailyCheckin.findById(day);
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("lastCheckIn", today);
      paramsUser.put("point", dailyCheckin.point);
      User.updateUser("lastCheckIn = :lastCheckIn, point = point + :point where id = :id", paramsUser);
      return Utils.stripDecimalZeros(dailyCheckin.point);
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  }

  @Transactional
  public boolean verify(User user, long missionId) throws BusinessException {
    UserMissionResponse check = Mission.findByUserIdAndMissionId(user.id, missionId);
    if (check == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (check.status != null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    UserMission userMission = new UserMission();
    userMission.mission = new Mission(check.id);
    userMission.user = user;
    userMission.status = Enums.MissionStatus.VERIFIED;
    UserMission.create(userMission);
    return true;
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
      return true;
    }
    return false;
  }
}