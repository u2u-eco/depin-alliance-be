package xyz.telegram.depinalliance.services;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;

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
        SystemConfig systemConfig = new SystemConfig(Enums.Config.ROOT_POINT_CLAIM, "0.05");
        systemConfig.persist();
      }

      if (Level.count() == 0) {
        for (long i = 1; i < 100; i++) {
          Level level = new Level();
          level.id = i;
          level.name = "Level " + i;
          level.exp = new BigDecimal(10 * i);
          level.point = new BigDecimal(100 * i);
          level.persist();
        }
      }
      if (DailyCheckin.count() == 0) {
        for (long i = 1; i < 7; i++) {
          DailyCheckin dailyCheckin = new DailyCheckin();
          dailyCheckin.id = i;
          dailyCheckin.name = "Day " + i;
          dailyCheckin.point = new BigDecimal(10 * i);
          dailyCheckin.persist();
        }
      }
      if (Item.count() == 0) {
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
      }
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
    }
  }
}
