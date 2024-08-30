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
import java.util.List;

/**
 * @author holden on 26-Aug-2024
 */
public class InitService {

  @Transactional
  void onStart(@Observes StartupEvent event) {
    if (LaunchMode.current().isDevOrTest()) {
      String configPointRef = SystemConfig.findByKey(Enums.Config.POINT_REF);
      if (configPointRef == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.POINT_REF, "200");
        systemConfig.persist();
      }
      String configUserDeviceCpuSlot = SystemConfig.findByKey(Enums.Config.CPU_SLOT);
      if (configUserDeviceCpuSlot == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.CPU_SLOT, "1");
        systemConfig.persist();
      }
      String configUserDeviceGpuSlot = SystemConfig.findByKey(Enums.Config.GPU_SLOT);
      if (configUserDeviceGpuSlot == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.GPU_SLOT, "2");
        systemConfig.persist();
      }
      String configUserDeviceRamSlot = SystemConfig.findByKey(Enums.Config.RAM_SLOT);
      if (configUserDeviceRamSlot == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.RAM_SLOT, "3");
        systemConfig.persist();
      }
      String configUserDeviceStorageSlot = SystemConfig.findByKey(Enums.Config.STORAGE_SLOT);
      if (configUserDeviceStorageSlot == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.STORAGE_SLOT, "1");
        systemConfig.persist();
      }
      String configCpuDefault = SystemConfig.findByKey(Enums.Config.CPU_DEFAULT);
      if (configCpuDefault == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.CPU_DEFAULT, "CPU_1");
        systemConfig.persist();
      }
      String configGpuDefault = SystemConfig.findByKey(Enums.Config.GPU_DEFAULT);
      if (configGpuDefault == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.GPU_DEFAULT, "GPU_1");
        systemConfig.persist();
      }
      String configRamDefault = SystemConfig.findByKey(Enums.Config.RAM_DEFAULT);
      if (configRamDefault == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.RAM_DEFAULT, "RAM_1");
        systemConfig.persist();
      }
      String configStorageDefault = SystemConfig.findByKey(Enums.Config.STORAGE_DEFAULT);
      if (configStorageDefault == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.STORAGE_DEFAULT, "STORAGE_1");
        systemConfig.persist();
      }
      String configAvatarDefault = SystemConfig.findByKey(Enums.Config.AVATAR_DEFAULT);
      if (configAvatarDefault == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.AVATAR_DEFAULT,
          "/assets/images/avatar/avatar-01@2x.png");
        systemConfig.persist();
      }
      String configAvatarList = SystemConfig.findByKey(Enums.Config.AVATAR_LIST);
      if (configAvatarList == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.AVATAR_LIST,
          "/assets/images/avatar/avatar-01@2x.png;/assets/images/avatar/avatar-02@2x.png;/assets/images/avatar/avatar-03@2x.png;/assets/images/avatar/avatar-04@2x.png;/assets/images/avatar/avatar-05@2x.png;/assets/images/avatar/avatar-06@2x.png");
        systemConfig.persist();
      }
      String configMaxMiningPowerDefault = SystemConfig.findByKey(Enums.Config.MAX_MINING_POWER_DEFAULT);
      if (configMaxMiningPowerDefault == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.MAX_MINING_POWER_DEFAULT, "1000");
        systemConfig.persist();
      }
      String configRefPointClaim = SystemConfig.findByKey(Enums.Config.REF_POINT_CLAIM);
      if (configRefPointClaim == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.REF_POINT_CLAIM, "0.05");
        systemConfig.persist();
      }
      String configRootPointClaim = SystemConfig.findByKey(Enums.Config.ROOT_POINT_CLAIM);
      if (configRootPointClaim == null) {
        SystemConfig systemConfig = new SystemConfig(Enums.Config.ROOT_POINT_CLAIM, "338079956");
        systemConfig.persist();
      }

      if (Level.count() == 0) {
        initLevel();
      }
      /*if (DailyCheckin.count() == 0) {
        for (long i = 1; i < 7; i++) {
          DailyCheckin dailyCheckin = new DailyCheckin();
          dailyCheckin.id = i;
          dailyCheckin.name = "Day " + i;
          dailyCheckin.point = new BigDecimal(10 * i);
          dailyCheckin.persist();
        }
      }*/
      /*if (Item.count() == 0) {
        for (int i = 1; i < 31; i++) {
          Item item = Item.find("code", "CPU_" + i).firstResult();
          if (item == null) {
            item = new Item();
            item.code = "CPU_" + i;
            item.price = new BigDecimal(Utils.getRandomNumber(i * 10, i * 15));
            item.miningPower = new BigDecimal(Utils.getRandomNumber(i * 10, i * 15));
            item.type = Enums.ItemType.CPU;
            item.name = "Intel Core i " + i;
            item.point = new BigDecimal(Utils.getRandomNumber(i * 10, i * 15));
            item.create();
            item.persist();
          }
        }

        for (int i = 1; i < 31; i++) {
          Item item = Item.find("code", "GPU_" + i).firstResult();
          if (item == null) {
            item = new Item();
            item.code = "GPU_" + i;
            item.price = new BigDecimal(Utils.getRandomNumber(i * 16, i * 20));
            item.miningPower = new BigDecimal(Utils.getRandomNumber(i * 16, i * 20));
            item.point = new BigDecimal(Utils.getRandomNumber(i * 16, i * 20));
            item.type = Enums.ItemType.GPU;
            item.name = "RTX " + i;
            item.create();
            item.persist();
          }
        }

        for (int i = 1; i < 31; i++) {
          Item item = Item.find("code", "RAM_" + i).firstResult();
          if (item == null) {
            item = new Item();
            item.code = "RAM_" + i;
            item.price = new BigDecimal(Utils.getRandomNumber(i * 5, i * 10));
            item.miningPower = new BigDecimal(Utils.getRandomNumber(i * 5, i * 10));
            item.point = new BigDecimal(Utils.getRandomNumber(i * 5, i * 10));
            item.type = Enums.ItemType.RAM;
            item.name = i + " GB";
            item.create();
            item.persist();
          }
        }

        for (int i = 1; i < 31; i++) {
          Item item = Item.find("code", "STORAGE_" + i).firstResult();
          if (item == null) {
            item = new Item();
            item.code = "STORAGE_" + i;
            item.price = new BigDecimal(Utils.getRandomNumber(i * 8, i * 13));
            item.miningPower = new BigDecimal(Utils.getRandomNumber(i * 8, i * 13));
            item.point = new BigDecimal(Utils.getRandomNumber(i * 8, i * 13));
            item.type = Enums.ItemType.STORAGE;
            item.name = i + " GB";
            item.create();
            item.persist();
          }
        }
      }*/
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
      if(Skill.count() == 0) {
        initSkill();
        initSkillLevel();
        initSkillUser();
      }
    }
  }

  public void initLevel() {
    long id = 1;
    Level level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = BigDecimal.ZERO;
    level.expTo = new BigDecimal(100);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(100);
    level.expTo = new BigDecimal(221);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(221);
    level.expTo = new BigDecimal(354);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(354);
    level.expTo = new BigDecimal(501);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(501);
    level.expTo = new BigDecimal(662);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(662);
    level.expTo = new BigDecimal(839);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(839);
    level.expTo = new BigDecimal(1034);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(1034);
    level.expTo = new BigDecimal(1248);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(1248);
    level.expTo = new BigDecimal(1484);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(1484);
    level.expTo = new BigDecimal(1743);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(1743);
    level.expTo = new BigDecimal(2028);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(2028);
    level.expTo = new BigDecimal(2342);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(2342);
    level.expTo = new BigDecimal(2687);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(2687);
    level.expTo = new BigDecimal(3067);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(3067);
    level.expTo = new BigDecimal(3485);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(3485);
    level.expTo = new BigDecimal(3944);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(3944);
    level.expTo = new BigDecimal(4450);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(4450);
    level.expTo = new BigDecimal(5006);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(5006);
    level.expTo = new BigDecimal(5617);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(5617);
    level.expTo = new BigDecimal(6290);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(6290);
    level.expTo = new BigDecimal(7030);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(7030);
    level.expTo = new BigDecimal(7844);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(7844);
    level.expTo = new BigDecimal(8740);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(8740);
    level.expTo = new BigDecimal(9725);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(9725);
    level.expTo = new BigDecimal(10808);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(10808);
    level.expTo = new BigDecimal(12000);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(12000);
    level.expTo = new BigDecimal(13311);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(13311);
    level.expTo = new BigDecimal(14753);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(14753);
    level.expTo = new BigDecimal(16339);
    level.persist();

    level = new Level();
    level.id = id++;
    level.name = "Level " + level.id;
    level.expFrom = new BigDecimal(16339);
    level.expTo = new BigDecimal(18084);
    level.persist();
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
    skill.name = "Networking";
    skill.orderDisplay = 3;
    skill.maxLevel = 10L;
    skill.persist();
  }
  public void initSkillLevel() {
    Skill skill1 = Skill.findById(1L);
    Skill skill2 = Skill.findById(2L);
    Skill skill3 = Skill.findById(3L);

    for(long i=1; i<=10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill1;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.rateMining = new BigDecimal(0.01);
      skillLevel.ratePurchase = BigDecimal.ZERO;
      skillLevel.rateReward = BigDecimal.ZERO;
      skillLevel.timeWaitUpgrade = i*3600;
      skillLevel.persist();
    }
    for(long i=1; i<=10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill2;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.rateMining = BigDecimal.ZERO;
      skillLevel.ratePurchase = new BigDecimal(-0.01);;
      skillLevel.rateReward = BigDecimal.ZERO;
      skillLevel.timeWaitUpgrade = i*3600;
      skillLevel.persist();
    }
    for(long i=1; i<=10; i++) {
      SkillLevel skillLevel = new SkillLevel();
      skillLevel.skill = skill3;
      skillLevel.level = i;
      skillLevel.feeUpgrade = BigDecimal.ONE;
      skillLevel.rateMining = BigDecimal.ZERO;
      skillLevel.ratePurchase = BigDecimal.ZERO;
      skillLevel.rateReward = new BigDecimal(0.01);
      skillLevel.timeWaitUpgrade = i*3600;
      skillLevel.persist();
    }
  }
  public void initSkillUser() {
    List<Skill> skills = Skill.listAll();
    if(UserSkill.count() == 0) {
      List<User> users = User.findAll().list();
      users.forEach(u -> {
        UserSkill.initUserSkill(u, skills);
      });
    }
  }
}
