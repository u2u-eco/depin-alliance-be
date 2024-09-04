package xyz.telegram.depinalliance.services;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
        mission.image="/assets/images/icons/icon-x-gradient.svg";
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
        mission.image="/assets/images/icons/icon-telegram-gradient.svg";
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
        mission.image="/assets/images/icons/icon-x-gradient.svg";
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
        mission.image="/assets/images/icons/icon-x-gradient.svg";
        mission.create();
        mission.persist();
      }
      if (Skill.count() == 0) {
        initSkill();
        initSkillLevel();
        initSkillUser();
//        initSkillPoint();
      }

    }
  }

  public void initSkill() {
    Skill skill = new Skill();
    skill.id = 1L;
    skill.name = "Programming";
    skill.description = "Increase mining power";
    skill.orderDisplay = 1;
    skill.maxLevel = 10L;
    skill.persist();

    skill = new Skill();
    skill.id = 2L;
    skill.name = "Financial";
    skill.description = "Decrease purchase price";
    skill.orderDisplay = 2;
    skill.maxLevel = 10L;
    skill.persist();

    skill = new Skill();
    skill.id = 3L;
    skill.name = "Innovation";
    skill.description = "Decrease countdown time";
    skill.orderDisplay = 3;
    skill.maxLevel = 10L;
    skill.persist();

    skill = new Skill();
    skill.id = 4L;
    skill.name = "Data Analysis";
    skill.description = "Increase high reward rate when claim reward";
    skill.orderDisplay = 4;
    skill.maxLevel = 10L;
    skill.persist();

    skill = new Skill();
    skill.id = 5L;
    skill.name = "Strategic Planning";
    skill.description = "Increase max capacity";
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
      skillLevel.timeWaitUpgrade = i * 3600;
      skillLevel.persist();
    }
    for (long i = 1; i <= 10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill2;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.ratePurchase = new BigDecimal(-0.01);;
      skillLevel.timeWaitUpgrade = i * 3600;
      skillLevel.persist();
    }
    for (long i = 1; i <= 10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill3;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.rateCountDown = new BigDecimal(-0.01);
      skillLevel.timeWaitUpgrade = i * 3600;
      skillLevel.persist();
    }
    for (long i = 1; i <= 10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill4;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.rateReward = new BigDecimal(0.01);
      skillLevel.timeWaitUpgrade = i * 3600;
      skillLevel.persist();
    }
    for (long i = 1; i <= 10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill5;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.rateCapacity = new BigDecimal(0.01);
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

  public void initSkillPoint(){
    if(SkillPoint.count() == 0) {
      List<BigDecimal> datas = Arrays.asList(
        new BigDecimal("1000"),
        new BigDecimal("2000"),
        new BigDecimal("10000"),
        new BigDecimal("32000"),
        new BigDecimal("48000"),
        new BigDecimal("72000"),
        new BigDecimal("108000"),
        new BigDecimal("162000"),
        new BigDecimal("243000"),
        new BigDecimal("364500"),
        new BigDecimal("729000"),
        new BigDecimal("1458000"),
        new BigDecimal("2916000"),
        new BigDecimal("5832000")
      );
      List<Long> times = Arrays.asList(
        30L,
        120L,
        600L,
        3600L,
        3600L*2,
        3600L*4,
        3600L*8,
        3600L*12,
        3600L*20,
        3600L*24
      );
      if(SkillPoint.count() == 0)
        for (long i = 1; i <= 100; i++) {
          SkillPoint skillPoint = new SkillPoint();
          skillPoint.id  = i;
          skillPoint.point = i >= datas.size() ? datas.get(datas.size()-1) : datas.get((int) (i-1));
          skillPoint.upgradeTime = i >= times.size() ? times.get(times.size() - 1) : times.get((int) i - 1);
          skillPoint.persist();
        }
    }
  }
}
