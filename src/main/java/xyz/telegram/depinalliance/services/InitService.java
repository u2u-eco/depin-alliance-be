package xyz.telegram.depinalliance.services;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.response.QuizResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.Event;
import xyz.telegram.depinalliance.entities.EventMission;
import xyz.telegram.depinalliance.entities.Item;
import xyz.telegram.depinalliance.entities.Mission;

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
        if (true) {
          Mission mission = new Mission();
          mission.groupMission = "Social mission";
          mission.name = "Follow Our X Account ";
          mission.type = Enums.MissionType.TWITTER;
          mission.isFake = true;
          mission.description = "Follow Our X Account";
          mission.orders = 4;
          mission.url = "https://x.com/DePINApp";
          mission.point = new BigDecimal(7000);
          mission.xp = new BigDecimal(300);
          mission.image = "/assets/images/icons/icon-x-gradient.svg";
          mission.create();
          mission.persist();

          mission = new Mission();
          mission.groupMission = "Social mission";
          mission.name = "Join Our Telegram channel";
          mission.type = Enums.MissionType.TELEGRAM;
          mission.isFake = false;
          mission.description = "Join Our Telegram channel";
          mission.orders = 5;
          mission.url = "https://t.me/DePIN_App";
          mission.point = new BigDecimal(7000);
          mission.xp = new BigDecimal(200);
          mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
          mission.referId = "DePIN_App";
          mission.create();
          mission.persist();

          mission = new Mission();
          mission.groupMission = "Social mission";
          mission.name = "Share story";
          mission.type = Enums.MissionType.SHARE_STORY;
          mission.isFake = true;
          mission.description = "Share story";
          mission.orders = 6;
          mission.url = "";
          mission.point = new BigDecimal(7000);
          mission.xp = new BigDecimal(500);
          mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
          mission.create();
          mission.persist();

          Item item = Item.findByCode("CYBER_BOX");
          mission = new Mission();
          mission.groupMission = "Summon DePIN Alliance";
          mission.name = "Follow U2U Network X";
          mission.type = Enums.MissionType.TWITTER;
          mission.isFake = true;
          mission.description = "Follow U2U Network X";
          mission.orders = 1;
          mission.url = "https://x.com/uniultra_xyz";
          mission.point = new BigDecimal(10000);
          mission.xp = new BigDecimal(200);
          mission.box = 1L;
          mission.image = "/assets/images/icons/icon-x-gradient.svg";
          mission.create();
          mission.persist();

          EventMission eventMissionInvite = new EventMission();
          eventMissionInvite.event = new Event(1L);
          eventMissionInvite.mission = mission;
          eventMissionInvite.item = item;
          eventMissionInvite.number = 1;
          eventMissionInvite.create();
          eventMissionInvite.persist();

          mission = new Mission();
          mission.groupMission = "Summon DePIN Alliance";
          mission.name = "Join U2U Network Telegram";
          mission.type = Enums.MissionType.TELEGRAM;
          mission.isFake = false;
          mission.description = "Join U2U Network Telegram";
          mission.orders = 2;
          mission.url = "https://t.me/UnicornUltraGlobal";
          mission.point = new BigDecimal(10000);
          mission.xp = new BigDecimal(200);
          mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
          mission.referId = "UnicornUltraGlobal";
          mission.box = 1L;
          mission.create();
          mission.persist();

          eventMissionInvite = new EventMission();
          eventMissionInvite.event = new Event(1L);
          eventMissionInvite.mission = mission;
          eventMissionInvite.item = item;
          eventMissionInvite.number = 1;
          eventMissionInvite.create();
          eventMissionInvite.persist();
        }
        //Product
        if (Mission.findByMissionRequire(Enums.MissionRequire.CLAIM_FIRST_10000_POINT) == null) {
          Mission mission = new Mission();
          mission.groupMission = "Product";
          mission.name = "Claim the first 10,000 points from mining";
          mission.type = Enums.MissionType.ON_TIME_IN_APP;
          mission.isFake = false;
          mission.description = "Claim the first 10,000 points from mining";
          mission.orders = 1000;
          mission.missionRequire = Enums.MissionRequire.CLAIM_FIRST_10000_POINT;
          mission.point = new BigDecimal(10000);
          mission.xp = new BigDecimal(800);
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
          mission.xp = new BigDecimal(200);
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
          mission.xp = new BigDecimal(300);
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
          mission.xp = new BigDecimal(300);
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
          mission.xp = new BigDecimal(300);
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
          mission.xp = new BigDecimal(300);
          mission.create();
          mission.persist();
        }
        if (Mission.findByMissionRequire(Enums.MissionRequire.INVITE_1) == null) {
          Mission mission = new Mission();
          mission.groupMission = "Product";
          mission.name = "Invite 1 friend";
          mission.type = Enums.MissionType.ON_TIME_IN_APP;
          mission.isFake = false;
          mission.description = "Invite 1 friend";
          mission.orders = 1006;
          mission.missionRequire = Enums.MissionRequire.INVITE_1;
          mission.point = new BigDecimal(20000);
          mission.xp = new BigDecimal(500);
          mission.create();
          mission.persist();
        }

        if (Mission.findByMissionRequire(Enums.MissionRequire.INVITE_5) == null) {
          Mission mission = new Mission();
          mission.groupMission = "Product";
          mission.name = "Invite 5 friend";
          mission.type = Enums.MissionType.ON_TIME_IN_APP;
          mission.isFake = false;
          mission.description = "Invite 5 friend";
          mission.orders = 1007;
          mission.missionRequire = Enums.MissionRequire.INVITE_5;
          mission.point = new BigDecimal(20000);
          mission.xp = new BigDecimal(300);
          mission.create();
          mission.persist();
        }
        if (Mission.findByMissionRequire(Enums.MissionRequire.INVITE_10) == null) {
          Mission mission = new Mission();
          mission.groupMission = "Product";
          mission.name = "Invite 10 friend";
          mission.type = Enums.MissionType.ON_TIME_IN_APP;
          mission.isFake = false;
          mission.description = "Invite 10 friend";
          mission.orders = 1008;
          mission.missionRequire = Enums.MissionRequire.INVITE_10;
          mission.point = new BigDecimal(20000);
          mission.xp = new BigDecimal(400);
          mission.create();
          mission.persist();
        }

        List<Long> rangeInviteEvent = Arrays.asList(3L, 8L, 13L, 18L, 23L, 28L, 33L, 38L, 43L, 48L, 53L, 58L, 63L, 68L,
          73L, 78L, 83L, 88L, 93L, 98L);
        int orders = 3000;
        Item item = Item.findByCode("CYBER_BOX");
        for (Long id : rangeInviteEvent) {
          Enums.MissionRequire require = Enums.MissionRequire.valueOf("EVENT_INVITE_" + id);
          if (Mission.findByMissionRequire(require) == null) {
            Mission mission = new Mission();
            mission.groupMission = "Summon DePIN Alliance";
            mission.name = "Invite " + id + " friend";
            mission.type = Enums.MissionType.ON_TIME_IN_APP;
            mission.isFake = false;
            mission.description = "Invite " + id + " friend";
            mission.orders = orders++;
            mission.missionRequire = require;
            mission.point = new BigDecimal(0);
            mission.xp = new BigDecimal(0);
            mission.box = 1L;
            mission.create();
            mission.persist();
            EventMission eventMissionInvite = new EventMission();
            eventMissionInvite.event = new Event(1L);
            eventMissionInvite.mission = mission;
            eventMissionInvite.item = item;
            eventMissionInvite.number = 1;
            eventMissionInvite.create();
            eventMissionInvite.persist();
          }
        }
      }

//      List<QuizResponse> listQuizResponses = new ArrayList<>();
//      QuizResponse quizResponse = new QuizResponse();
//      quizResponse.index = 1;
//      quizResponse.question = "Ai la leader team";
//      quizResponse.answers.add(new QuizResponse.Answer(1, "Frankie", false));
//      quizResponse.answers.add(new QuizResponse.Answer(2, "Holden", true));
//      quizResponse.answers.add(new QuizResponse.Answer(3, "ChinhTm", false));
//      listQuizResponses.add(quizResponse);
//      quizResponse = new QuizResponse();
//      quizResponse.index = 2;
//      quizResponse.question = "Ai cao nhat team";
//      quizResponse.answers.add(new QuizResponse.Answer(1, "Frankie", false));
//      quizResponse.answers.add(new QuizResponse.Answer(2, "Huong", false));
//      quizResponse.answers.add(new QuizResponse.Answer(3, "Tuan", false));
//      quizResponse.answers.add(new QuizResponse.Answer(4, "Hung cui bap", true));
//      listQuizResponses.add(quizResponse);
////
//      Mission mission = new Mission();
//      mission.groupMission = "Quiz mission";
//      mission.name = "Complete IQ Quiz";
//      mission.type = Enums.MissionType.QUIZ;
//      mission.isFake = false;
//      mission.description = Utils.convertObjectToString(listQuizResponses);
//      mission.orders = 1;
//      mission.url = "";
//      mission.point = new BigDecimal(7000);
//      mission.xp = new BigDecimal(300);
//      mission.image = "/assets/images/icons/icon-quiz-gradient.svg";
//      mission.create();
//      mission.persist();

      //      if (Event.findById(1L) == null) {
      //        Event event1 = new Event();
      //        event1.name = "Event 1";
      //        event1.code = "EVENT_1";
      //        event1.isActive = true;
      //        event1.create();
      //        event1.persist();

      //        Mission mission1 = new Mission();
      //        mission1.groupMission = event1.name;
      //        mission1.name = "Follow AAAAAA";
      //        mission1.type = Enums.MissionType.TWITTER;
      //        mission1.isFake = true;
      //        mission1.description = "Follow AAAAAAAA";
      //        mission1.orders = 2000;
      //        mission1.url = "https://www.google.com";
      //        mission1.point = new BigDecimal(0);
      //        mission1.xp = new BigDecimal(0);
      //        mission1.box = 1L;
      //        mission1.image = "/assets/images/icons/icon-x-gradient.svg";
      //        mission1.create();
      //        mission1.persist();

      //        EventMission eventMission = new EventMission();
      //        eventMission.event = event1;
      //        eventMission.mission = mission1;
      //        eventMission.item = item;
      //        eventMission.number = 1;
      //        eventMission.create();
      //        eventMission.persist();
      //
      //        mission1 = new Mission();
      //        mission1.groupMission = event1.name;
      //        mission1.name = "Follow BBBBBBBBBBB";
      //        mission1.type = Enums.MissionType.TELEGRAM;
      //        mission1.isFake = true;
      //        mission1.description = "Follow BBBBBBBBBBB";
      //        mission1.orders = 1;
      //        mission1.url = "https://www.google.com";
      //        mission1.point = new BigDecimal(0);
      //        mission1.xp = new BigDecimal(0);
      //        mission1.box = 2L;
      //        mission1.image = "/assets/images/icons/icon-x-gradient.svg";
      //        mission1.create();
      //        mission1.persist();
      //
      //        eventMission = new EventMission();
      //        eventMission.event = event1;
      //        eventMission.mission = mission1;
      //        eventMission.item = item;
      //        eventMission.number = 2;
      //        eventMission.create();
      //        eventMission.persist();
      //
      //        mission1 = new Mission();
      //        mission1.groupMission = event1.name;
      //        mission1.name = "Follow CCCCCC";
      //        mission1.type = Enums.MissionType.TELEGRAM;
      //        mission1.isFake = true;
      //        mission1.description = "Follow CCCCCCC";
      //        mission1.orders = 1;
      //        mission1.url = "https://www.google.com";
      //        mission1.point = new BigDecimal(0);
      //        mission1.xp = new BigDecimal(0);
      //        mission1.box = 3L;
      //        mission1.image = "/assets/images/icons/icon-x-gradient.svg";
      //        mission1.create();
      //        mission1.persist();
      //
      //        eventMission = new EventMission();
      //        eventMission.event = event1;
      //        eventMission.mission = mission1;
      //        eventMission.item = item;
      //        eventMission.number = 3;
      //        eventMission.create();
      //        eventMission.persist();
      //
      //        mission1 = new Mission();
      //        mission1.groupMission = event1.name;
      //        mission1.name = "Follow DDDDDDDDD";
      //        mission1.type = Enums.MissionType.TELEGRAM;
      //        mission1.isFake = true;
      //        mission1.description = "Follow DDDDDDDDDD";
      //        mission1.orders = 1;
      //        mission1.url = "https://www.google.com";
      //        mission1.point = new BigDecimal(0);
      //        mission1.xp = new BigDecimal(0);
      //        mission1.box = 4L;
      //        mission1.image = "/assets/images/icons/icon-x-gradient.svg";
      //        mission1.create();
      //        mission1.persist();
      //
      //        eventMission = new EventMission();
      //        eventMission.event = event1;
      //        eventMission.mission = mission1;
      //        eventMission.item = item;
      //        eventMission.number = 4;
      //        eventMission.create();
      //        eventMission.persist();

      //        for (int i = 1; i < 100; i++) {
      //          EventBoxPoint eventBoxPoint = new EventBoxPoint();
      //          eventBoxPoint.create();
      //          eventBoxPoint.event = event1;
      //          eventBoxPoint.item = item;
      //          eventBoxPoint.pointRequired = new BigDecimal(i);
      //          eventBoxPoint.indexBox = i;
      //          if (i == 1) {
      //            eventBoxPoint.rewardTable = "1";
      //          } else if (i > 1 && i <= 9) {
      //            eventBoxPoint.rewardTable = "2";
      //          } else {
      //            eventBoxPoint.rewardTable = "3";
      //          }
      //          eventBoxPoint.persistAndFlush();
      //        }

      //      }

     /* Partner partner = Partner.findByName("Clayton");
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
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
        mission.url = "https://www.google.com";
        mission.point = new BigDecimal(3000);
        mission.xp = new BigDecimal(3000);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.create();
        mission.persist();
      }

<<<<<<< HEAD

=======
>>>>>>> origin/staging



<<<<<<< HEAD
        Partner partnerEvent = new Partner();
        partnerEvent.name = "Wizzwoods";
        partnerEvent.description = "This is Wizzwoods";
        partnerEvent.rewards = "Up to +3000 points";
        partnerEvent.orders = 1;
        partnerEvent.participants = 0;
        partnerEvent.isActive = true;
        partnerEvent.create();
        partnerEvent.persist();

        Mission mission1 = new Mission();
        mission1.groupMission = partnerEvent.name;
        mission1.name = "Follow AAAAAA";
        mission1.type = Enums.MissionType.TWITTER;
        mission1.isFake = true;
        mission1.description = "Follow AAAAAAAA";
        mission1.orders = 1;
        mission1.url = "https://www.google.com";
        mission1.point = new BigDecimal(1000);
        mission1.xp = new BigDecimal(1000);
        mission1.image = "/assets/images/icons/icon-x-gradient.svg";
        mission1.partner = partnerEvent;
        mission1.create();
        mission1.persist();

        Mission mission2 = new Mission();
        mission2.groupMission = partnerEvent.name;
        mission2.name = "Follow BBBBBBBB";
        mission2.type = Enums.MissionType.TELEGRAM;
        mission2.isFake = true;
        mission2.description = "Follow BBBBBB";
        mission2.orders = 3;
        mission2.url = "https://www.google.com";
        mission2.point = new BigDecimal(3000);
        mission2.xp = new BigDecimal(3000);
        mission2.image = "/assets/images/icons/icon-x-gradient.svg";
        mission2.partner = partnerEvent;
        mission2.create();
        mission2.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = event1;
        eventMission.mission = mission1;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.persist();

        eventMission = new EventMission();
        eventMission.event = event1;
        eventMission.mission = mission2;
        eventMission.item = item;
        eventMission.number = 2;
        eventMission.persist();
      }
=======
>>>>>>> origin/staging
      *//*if (Mission.findByMissionRequire(Enums.MissionRequire.LEVEL_35) == null) {
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
      }*//**/

    }
  }
}
