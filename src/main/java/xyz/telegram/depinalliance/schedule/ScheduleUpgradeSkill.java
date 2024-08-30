package xyz.telegram.depinalliance.schedule;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.HistoryUpgradeSkill;
import xyz.telegram.depinalliance.services.UserService;

import java.util.List;

@ApplicationScoped
public class ScheduleUpgradeSkill {
  @Inject
  UserService userService;
  @Inject
  Logger logger;

  @Scheduled(every = "5s", identity = "task-job")
  void schedule() {
    List<HistoryUpgradeSkill> skillsUpgrade = HistoryUpgradeSkill.getPending(Utils.getCalendar().getTimeInMillis());
    if (!skillsUpgrade.isEmpty()) {
      skillsUpgrade.forEach(up -> {
        try {
          userService.updateSkillLevelForUser(up);
          logger.infov("Upgrade skill {} for user {} from lv {} to {} success", up.skillId, up.userId, up.levelCurrent,
            up.levelUpgrade);
        } catch (Exception e) {
          logger.errorv("ID [{}]: Update level {} skill {} for user {} error ", up.id, up.levelUpgrade, up.skillId,
            up.userId);
          logger.error(Utils.printLogStackTrace(e));
        }
      });
    }
  }
}
