package xyz.telegram.depinalliance.services;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.entities.Mission;
import xyz.telegram.depinalliance.entities.Partner;

import java.math.BigDecimal;

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
        mission.point = new BigDecimal(1000);
        mission.xp = new BigDecimal(1000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
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
        mission.point = new BigDecimal(2000);
        mission.xp = new BigDecimal(2000);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
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
        mission.point = new BigDecimal(3000);
        mission.xp = new BigDecimal(3000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
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
        mission.point = new BigDecimal(4000);
        mission.xp = new BigDecimal(4000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.create();
        mission.persist();
      }
      Partner partner = Partner.findByName("Clayton");
      if (partner == null) {
        partner = new Partner();
        partner.name = "Clayton";
        partner.description = "This is clayton";
        partner.rewards = "Up to +3000 points";
        partner.orders = 1;
        partner.participants = 0;
        partner.isActive = true;
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 1;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(1000);
        mission.xp = new BigDecimal(1000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our Telegram channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Follow Our Telegram channel";
        mission.orders = 2;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(2000);
        mission.xp = new BigDecimal(2000);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 3;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(3000);
        mission.xp = new BigDecimal(3000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();
      }

      partner = Partner.findByName("Wizzwoods");
      if (partner == null) {
        partner = new Partner();
        partner.name = "Wizzwoods";
        partner.description = "This is Wizzwoods";
        partner.rewards = "Up to +3000 points";
        partner.orders = 1;
        partner.participants = 0;
        partner.isActive = true;
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 1;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(1000);
        mission.xp = new BigDecimal(1000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our Telegram channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Follow Our Telegram channel";
        mission.orders = 2;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(2000);
        mission.xp = new BigDecimal(2000);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 3;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(3000);
        mission.xp = new BigDecimal(3000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();
      }

      partner = Partner.findByName("Simple Coin");
      if (partner == null) {
        partner = new Partner();
        partner.name = "Simple Coin";
        partner.description = "This is Simple Coin";
        partner.rewards = "Up to +3000 points";
        partner.orders = 1;
        partner.participants = 0;
        partner.isActive = true;
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 1;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(1000);
        mission.xp = new BigDecimal(1000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our Telegram channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Follow Our Telegram channel";
        mission.orders = 2;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(2000);
        mission.xp = new BigDecimal(2000);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 3;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(3000);
        mission.xp = new BigDecimal(3000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();
      }

      partner = Partner.findByName("OkX wallets");
      if (partner == null) {
        partner = new Partner();
        partner.name = "OkX wallets";
        partner.description = "This is OkX wallets";
        partner.rewards = "Up to +3000 points";
        partner.orders = 1;
        partner.participants = 0;
        partner.isActive = true;
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 1;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(1000);
        mission.xp = new BigDecimal(1000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our Telegram channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Follow Our Telegram channel";
        mission.orders = 2;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(2000);
        mission.xp = new BigDecimal(2000);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Our X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Our X Account";
        mission.orders = 3;
        mission.url = "https://www.google.com/search?q=";
        mission.point = new BigDecimal(3000);
        mission.xp = new BigDecimal(3000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();
      }

      //Product
      if (Mission.findByMissionRequire(Enums.MissionRequire.CLAIM_FIRST_10000_POINT) == null) {
        Mission mission = new Mission();
        mission.groupMission = "Product";
        mission.name = "Claim points from mining";
        mission.type = Enums.MissionType.ON_TIME_IN_APP;
        mission.isFake = false;
        mission.description = "Claim the first 10,000 points from mining";
        mission.orders = 1000;
        mission.missionRequire = Enums.MissionRequire.CLAIM_FIRST_10000_POINT;
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(1000);
        mission.create();
        mission.persist();
      }
      if (Mission.findByMissionRequire(Enums.MissionRequire.BUY_ANY_DEVICE) == null) {
        Mission mission = new Mission();
        mission.groupMission = "Product";
        mission.name = "Buy any device";
        mission.type = Enums.MissionType.ON_TIME_IN_APP;
        mission.isFake = false;
        mission.description = "Buy any device";
        mission.orders = 1001;
        mission.missionRequire = Enums.MissionRequire.BUY_ANY_DEVICE;
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(500);
        mission.create();
        mission.persist();
      }
      if (Mission.findByMissionRequire(Enums.MissionRequire.LEARN_ANY_SKILL) == null) {
        Mission mission = new Mission();
        mission.groupMission = "Product";
        mission.name = "Learn any skill";
        mission.type = Enums.MissionType.ON_TIME_IN_APP;
        mission.isFake = false;
        mission.description = "Learn any skill";
        mission.orders = 1002;
        mission.missionRequire = Enums.MissionRequire.LEARN_ANY_SKILL;
        mission.point = new BigDecimal(7000);
        mission.xp = new BigDecimal(500);
        mission.create();
        mission.persist();
      }
      if (Mission.findByMissionRequire(Enums.MissionRequire.LEVEL_5) == null) {
        Mission mission = new Mission();
        mission.groupMission = "Product";
        mission.name = "Achieve Level 5";
        mission.type = Enums.MissionType.ON_TIME_IN_APP;
        mission.isFake = false;
        mission.description = "Achieve Level 5";
        mission.orders = 1003;
        mission.missionRequire = Enums.MissionRequire.LEVEL_5;
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(500);
        mission.create();
        mission.persist();
      }
      if (Mission.findByMissionRequire(Enums.MissionRequire.LEVEL_10) == null) {
        Mission mission = new Mission();
        mission.groupMission = "Product";
        mission.name = "Achieve Level 10";
        mission.type = Enums.MissionType.ON_TIME_IN_APP;
        mission.isFake = false;
        mission.description = "Achieve Level 10";
        mission.orders = 1004;
        mission.missionRequire = Enums.MissionRequire.LEVEL_10;
        mission.point = new BigDecimal(100000);
        mission.xp = new BigDecimal(500);
        mission.create();
        mission.persist();
      }
      if (Mission.findByMissionRequire(Enums.MissionRequire.LEVEL_20) == null) {
        Mission mission = new Mission();
        mission.groupMission = "Product";
        mission.name = "Achieve Level 20";
        mission.type = Enums.MissionType.ON_TIME_IN_APP;
        mission.isFake = false;
        mission.description = "Achieve Level 20";
        mission.orders = 1005;
        mission.missionRequire = Enums.MissionRequire.LEVEL_20;
        mission.point = new BigDecimal(500000);
        mission.xp = new BigDecimal(500);
        mission.create();
        mission.persist();
      }
      /*if (Mission.findByMissionRequire(Enums.MissionRequire.LEVEL_35) == null) {
        Mission mission = new Mission();
        mission.groupMission = "Product";
        mission.name = "Achieve Level 35";
        mission.type = Enums.MissionType.ON_TIME_IN_APP;
        mission.isFake = false;
        mission.description = "Achieve Level 35";
        mission.orders = 1006;
        mission.missionRequire = Enums.MissionRequire.LEVEL_35;
        mission.point = new BigDecimal(2000000);
        mission.xp = new BigDecimal(500);
        mission.create();
        mission.persist();
      }

      if (Mission.findByMissionRequire(Enums.MissionRequire.LEVEL_50) == null) {
        Mission mission = new Mission();
        mission.groupMission = "Product";
        mission.name = "Achieve Level 50";
        mission.type = Enums.MissionType.ON_TIME_IN_APP;
        mission.isFake = false;
        mission.description = "Achieve Level 50";
        mission.orders = 1007;
        mission.missionRequire = Enums.MissionRequire.LEVEL_50;
        mission.point = new BigDecimal(5000000);
        mission.xp = new BigDecimal(500);
        mission.create();
        mission.persist();
      }*/

    }
  }
}
