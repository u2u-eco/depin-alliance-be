package xyz.telegram.depinalliance.services;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author holden on 26-Aug-2024
 */
public class InitService {

  @Transactional
  void onStart(@Observes StartupEvent event) {
    if (LaunchMode.current().isDevOrTest()) {
      if (Mission.count() == 0) {
        Mission mission = new Mission();
        mission.groupMission = "Social mission";
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 1;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(Utils.getRandomNumber(100, 200));
        mission.xp = new BigDecimal(Utils.getRandomNumber(20, 50));
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = "Social mission";
        mission.name = "Follow Our Telegram channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Follow Our Telegram channel";
        mission.orders = 2;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(Utils.getRandomNumber(100, 200));
        mission.xp = new BigDecimal(Utils.getRandomNumber(20, 50));
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = "OKX Partner";
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 3;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(Utils.getRandomNumber(100, 200));
        mission.xp = new BigDecimal(Utils.getRandomNumber(20, 50));
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = "OKX partner";
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 4;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(Utils.getRandomNumber(100, 200));
        mission.xp = new BigDecimal(Utils.getRandomNumber(20, 50));
        mission.create();
        mission.persist();
      }
      if (Skill.count() == 0) {
        initSkill();
        initSkillLevel();
        initSkillUser();
      }
    }
  }

  public void initSkill() {
    Skill skill = new Skill();
    skill.id = 1L;
    skill.name = "Programming";
    skill.orderDisplay = 1;
    skill.maxLevel = 10L;
    skill.persist();

    skill = new Skill();
    skill.id = 2L;
    skill.name = "Financial";
    skill.orderDisplay = 2;
    skill.maxLevel = 10L;
    skill.persist();

    skill = new Skill();
    skill.id = 3L;
    skill.name = "Innovation";
    skill.orderDisplay = 3;
    skill.maxLevel = 10L;
    skill.persist();

    skill = new Skill();
    skill.id = 4L;
    skill.name = "Data Analysis";
    skill.orderDisplay = 4;
    skill.maxLevel = 10L;
    skill.persist();

    skill = new Skill();
    skill.id = 5L;
    skill.name = "Strategic Planning";
    skill.orderDisplay = 5;
    skill.maxLevel = 10L;
    skill.persist();
  }

  public void initSkillLevel() {
    Skill skill1 = Skill.findById(1L);
    Skill skill2 = Skill.findById(2L);
    Skill skill3 = Skill.findById(3L);
    Skill skill4 = Skill.findById(4L);
    Skill skill5 = Skill.findById(5L);

    for (long i = 1; i <= 10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill1;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.rateMining = new BigDecimal(0.01);
      skillLevel.ratePurchase = BigDecimal.ZERO;
      skillLevel.rateReward = BigDecimal.ZERO;
      skillLevel.timeWaitUpgrade = i * 3600;
      skillLevel.persist();
    }
    for (long i = 1; i <= 10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill2;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.rateMining = BigDecimal.ZERO;
      skillLevel.ratePurchase = new BigDecimal(-0.01);
      ;
      skillLevel.rateReward = BigDecimal.ZERO;
      skillLevel.timeWaitUpgrade = i * 3600;
      skillLevel.persist();
    }
    for (long i = 1; i <= 10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill3;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.rateMining = BigDecimal.ZERO;
      skillLevel.ratePurchase = BigDecimal.ZERO;
      skillLevel.rateReward = new BigDecimal(0.01);
      skillLevel.timeWaitUpgrade = i * 3600;
      skillLevel.persist();
    }
  }

  public void initSkillUser() {
    List<Skill> skills = Skill.listAll();
    if (UserSkill.count() == 0) {
      List<User> users = User.findAll().list();
      users.forEach(u -> {
        UserSkill.initUserSkill(u, skills);
      });
    }
  }
}
