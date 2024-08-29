package xyz.telegram.depinalliance.schedule;

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

//    @Scheduled(every = "5s", identity = "task-job")
    void schedule() {
        System.out.println("Job run after 5s");
        List<HistoryUpgradeSkill> skillsUpgrade = HistoryUpgradeSkill.getPending(Utils.getCalendar().getTimeInMillis());
        if(skillsUpgrade.size() > 0) {
            skillsUpgrade.forEach(up -> {
                try {
                    userService.updateSkillLevelForUser(up);
                }catch (Exception e) {
                    logger.errorv("ID [{}]: Update level {} skill {} for user {} error ",
                            up.id, up.levelUpgrade, up.skillId, up.userId);
                    logger.error(Utils.printLogStackTrace(e));
                }
            });
        }
    }
}
