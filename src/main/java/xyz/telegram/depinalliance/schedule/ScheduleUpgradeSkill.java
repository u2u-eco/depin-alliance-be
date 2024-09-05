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

  @Scheduled(every = "${upgrade.every.expr}", identity = "task-job")
  void schedule() {
    List<HistoryUpgradeSkill> skillsUpgrade = HistoryUpgradeSkill.getPending(Utils.getCalendar().getTimeInMillis());
    if (!skillsUpgrade.isEmpty()) {
      skillsUpgrade.forEach(up -> {
        try {
          userService.updateSkillLevelForUser(up);
          logger.infov("Upgrade skill {0} for user {1} from lv {2} to {3} success",
                  up.skillId, up.userId, up.levelCurrent, up.levelUpgrade);
        } catch (Exception e) {
          logger.errorv("ID [{0}]: Update level {1} skill {2} for user {3} error ",
                  up.id, up.levelUpgrade, up.skillId, up.userId);
          logger.error(Utils.printLogStackTrace(e));
        }
      });
    }
  }
}
