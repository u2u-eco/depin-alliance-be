package xyz.telegram.depinalliance.services;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.response.QuizResponse;
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
  @Inject
  RedisService redisService;

  @Transactional
  void onStart(@Observes StartupEvent event) {
   /* if (LaunchMode.current().isDevOrTest()) {
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

          Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
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
          mission.amount = 1L;
          mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
          mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";
          mission.image = "/assets/images/icons/icon-x-gradient.svg";
          mission.create();
          mission.persist();

          EventMission eventMissionInvite = new EventMission();
          eventMissionInvite.event = new Event(Enums.EventId.CYBER_BOX.getId());
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
          mission.amount = 1L;
          mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
          mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

          mission.create();
          mission.persist();

          eventMissionInvite = new EventMission();
          eventMissionInvite.event = new Event(Enums.EventId.CYBER_BOX.getId());
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
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
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
            mission.amount = 1L;
            mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
            mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

            mission.create();
            mission.persist();
            EventMission eventMissionInvite = new EventMission();
            eventMissionInvite.event = new Event(Enums.EventId.CYBER_BOX.getId());
            eventMissionInvite.mission = mission;
            eventMissionInvite.item = item;
            eventMissionInvite.number = 1;
            eventMissionInvite.create();
            eventMissionInvite.persist();
          }
        }
      }

      Partner partner = Partner.findByName("U2DPN");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "U2DPN";
        partner.description = "Embrace the revolution of the decentralized Internet and monetize your unused bandwidth";
        partner.rewards = "Up to +90,000 points";
        partner.orders = 1;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/u2dpn.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow X Account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow X Account";
        mission.orders = 1;
        mission.url = "https://x.com/u2_dpn";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.isActive = true;
        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Telegram channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join Telegram channel";
        mission.orders = 2;
        mission.referId = "UnicornUltraGlobal";
        mission.url = "https://t.me/UnicornUltraGlobal/120759";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Download App";
        mission.type = Enums.MissionType.DOWNLOAD_APP;
        mission.isFake = true;
        mission.description = "Download App";
        mission.orders = 3;
        mission.url = "https://u2dpn.xyz/?utm_source=DepinApp&utm_campaign=default&utm_medium=ref";
        mission.point = new BigDecimal(20000);
        mission.xp = new BigDecimal(200);
        mission.image = "";
        mission.partner = partner;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.isActive = true;
        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        List<QuizResponse> listQuizResponses = new ArrayList<>();
        QuizResponse quizResponse = new QuizResponse();
        quizResponse.index = 1;
        quizResponse.isMultiple = false;
        quizResponse.question = "What is the primary difference between U2DPN and traditional VPNs?";
        quizResponse.answers.add(new QuizResponse.Answer(1, "U2DPN uses a centralized server", false));
        quizResponse.answers.add(new QuizResponse.Answer(2, "U2DPN offers fewer privacy features", false));
        quizResponse.answers.add(new QuizResponse.Answer(3, "U2DPN utilizes a decentralized network of nodes", true));
        quizResponse.answers.add(new QuizResponse.Answer(4, "U2DPN is less secure than traditional VPNs", false));

        listQuizResponses.add(quizResponse);
        quizResponse = new QuizResponse();
        quizResponse.index = 2;
        quizResponse.question = "Which of the following is NOT a benefit of using U2DPN?";
        quizResponse.answers.add(new QuizResponse.Answer(1, "Enhanced privacy", false));
        quizResponse.answers.add(new QuizResponse.Answer(2, "Increased security", false));
        quizResponse.answers.add(new QuizResponse.Answer(3, "Limited global access", true));
        quizResponse.answers.add(new QuizResponse.Answer(4, "Financial incentives for node operators", false));
        listQuizResponses.add(quizResponse);

        quizResponse = new QuizResponse();
        quizResponse.index = 3;
        quizResponse.question = "How does U2DPN operate?";
        quizResponse.answers.add(new QuizResponse.Answer(1, "Through a centralized server", false));
        quizResponse.answers.add(new QuizResponse.Answer(2, "On a peer-to-peer basis", true));
        quizResponse.answers.add(new QuizResponse.Answer(3, "Exclusively through government-owned nodes", false));
        quizResponse.answers.add(
          new QuizResponse.Answer(4, "Using a proprietary protocol inaccessible to users", false));
        listQuizResponses.add(quizResponse);

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Quiz IQ";
        mission.type = Enums.MissionType.QUIZ;
        mission.isFake = false;
        mission.description = Utils.convertObjectToString(listQuizResponses);
        mission.orders = 4;
        mission.url = "";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.partner = partner;
        mission.image = "/assets/images/icons/icon-quiz-gradient.svg";
        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();
      }

      partner = Partner.findByName("Mizzle");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "Mizzle";
        partner.description = "A DePIN platform empowering developers with No-Code DevOps, limitless cloud compute & storage resources, and unparalleled security through TEEs, eBPF, FHE, and ZK. \n" + "We're Quantum-Ready!";
        partner.rewards = "Up to +50,000 points";
        partner.orders = 2;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/mizzle.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Mizzle on Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Mizzle on Twitter";
        mission.orders = 1;
        mission.url = "https://x.com/Mizzle_io";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.isActive = true;
        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Mizzle's Telegram group";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join Mizzle's Telegram group";
        mission.orders = 2;
        mission.url = "https://t.me/mizzleio";
        mission.referId = "mizzleio";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "RT the following tweet";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "RT the following tweet";
        mission.orders = 3;
        mission.url = "https://x.com/Mizzle_io/status/1833029639930810591?t=SQzXTPEbML0OMLOX8oC55Q&s=19";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

      }

      partner = Partner.findByName("Pindora");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "Pindora";
        partner.description = "We're building a DePIN Aggregator Platform";
        partner.rewards = "Up to +60,000 points";
        partner.orders = 3;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/pindora.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join the Telegram announcement channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join the Telegram announcement channel";
        mission.orders = 1;
        mission.url = "https://t.me/depindora";
        mission.referId = "depindora";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Twitter";
        mission.orders = 2;
        mission.url = "https://x.com/depindora";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.isActive = true;
        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Visit the website";
        mission.type = Enums.MissionType.DOWNLOAD_APP;
        mission.isFake = true;
        mission.description = "Visit the website";
        mission.orders = 3;
        mission.url = "https://www.pindora.io";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Sign up for the waitlist";
        mission.type = Enums.MissionType.DOWNLOAD_APP;
        mission.isFake = true;
        mission.description = "Sign up for the waitlist";
        mission.orders = 4;
        mission.url = "https://forms.gle/aouZNAnAVWsEy3UP9";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.isActive = true;
        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();
      }

      partner = Partner.findByName("ZkAGI");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "ZkAGI";
        partner.description = "ZkAGI is the first privacy AI DePIN, integrating advanced technologies like artificial intelligence (AI) and blockchain";
        partner.rewards = "Up to +50,000 points";
        partner.orders = 4;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/zkagi.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow ZkAGI on Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow ZkAGI on Twitter";
        mission.orders = 1;
        mission.url = "https://x.com/zk_agi";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.isActive = true;
        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join ZkAGI's Telegram group";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join ZkAGI's Telegram group";
        mission.orders = 2;
        mission.referId = "ZkAGI_AI";
        mission.url = "https://t.me/ZkAGI_AI";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Retweet";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Retweet";
        mission.orders = 3;
        mission.url = "https://x.com/zk_agi/status/1834909536420847633?s=46";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

      }

      partner = Partner.findByName("Hermes Alpha Zone");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "Hermes Alpha Zone";
        partner.description = "A go to space for all native crypto alpha, news, airdrops, potential 100x gems and educational contents on DeFi";
        partner.rewards = "Up to +80,000 points";
        partner.orders = 5;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/hermes-alpha-zone.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow our X account";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow our X account";
        mission.orders = 1;
        mission.url = "https://x.com/Vanieofweb3?t=yl2yPFIvXo5gR4ChkL1G7g&s=09";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.isActive = true;
        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join the Telegram Channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join the Telegram Channel";
        mission.orders = 2;
        mission.referId = "Gemjungle";
        mission.url = "https://t.me/Gemjungle";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join the Telegram Channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join the Telegram Channel";
        mission.orders = 3;
        mission.referId = "HermesCryptohub";
        mission.url = "https://t.me/HermesCryptohub";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow our medium page";
        mission.type = Enums.MissionType.DOWNLOAD_APP;
        mission.isFake = true;
        mission.description = "Follow our medium page";
        mission.orders = 4;
        mission.url = "https://medium.com/@Hermesalpha";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();
      }

      partner = Partner.findByName("Dextr");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "Dextr";
        partner.description = "The world’s first Actively Validated Market Maker (AVMM) featuring built-in MEV Insurance";
        partner.rewards = "Up to +50,000 points";
        partner.orders = 6;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/dextr.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Dextr on Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Dextr on Twitter";
        mission.orders = 1;
        mission.url = "https://x.com/Dextr_Exchange";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Dextr's Telegram group";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join Dextr's Telegram group";
        mission.orders = 2;
        mission.referId = "DextrExchange";
        mission.url = "https://t.me/DextrExchange";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "RT a tweet";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Retweet";
        mission.orders = 3;
        mission.url = "https://x.com/madhurprabhakar/status/1835228950441742528";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

      }

      partner = Partner.findByName("aZen Protocol");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "aZen Protocol";
        partner.description = "Empowers DePIN for Universal Computing Tasks";
        partner.rewards = "Up to +50,000 points";
        partner.orders = 7;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/azen.png";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow aZen Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow aZen Twitter";
        mission.orders = 1;
        mission.url = "https://x.com/azen_protocol";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join aZen Community";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join aZen Community";
        mission.orders = 2;
        mission.referId = "azenprotocol";
        mission.url = "https://t.me/azenprotocol";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Start aZen bot";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Start aZen bot";
        mission.orders = 3;
        mission.url = "https://t.me/aZennetwork1_bot/aZEn?startapp=BIAM89";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

      }

      partner = Partner.findByName("Unicorn Kingdom");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "Unicorn Kingdom";
        partner.description = "The ultimate Tap to Earn adventure on Telegram! \uD83E\uDD84✨";
        partner.rewards = "Up to +80,000 points";
        partner.orders = 8;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/unicorn-kingdom.png";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow X";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow X";
        mission.orders = 1;
        mission.url = "https://x.com/UnicornKingdomG";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join UDC Channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Join UDC Channel";
        mission.orders = 2;
        mission.url = "https://t.me/UnicornKingdom_Channel";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join UDC Community chat";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Join UDC Community chat";
        mission.orders = 3;
        mission.url = "https://t.me/UnicornKingdom_Chat";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join UDC bot";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Join UDC Bot";
        mission.orders = 4;
        mission.url = "https://t.me/UnicornKingdomBot?start=xkRnhvIN";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();
      }
      partner = Partner.findByName("Slinky network");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "Slinky network";
        partner.description = "Slinky Web3AI is an infrastructure for the Web3 x AI Creator Economy, secured by Babylon and Bitcoin";
        partner.rewards = "Up to +110,000 points";
        partner.orders = 9;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/slinky.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Twitter";
        mission.orders = 1;
        mission.url = "https://twitter.com/slinky_network";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Discord";
        mission.type = Enums.MissionType.URL;
        mission.isFake = true;
        mission.description = "Join Discord";
        mission.orders = 2;
        mission.url = "https://discord.slinky.network";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Telegram Group";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join Telegram Group";
        mission.orders = 3;
        mission.referId = "slinky_network";
        mission.url = "https://t.me/slinky_network";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Telegram Channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join Telegram Channel";
        mission.orders = 4;
        mission.referId = "slinky_ann";
        mission.url = "https://t.me/slinky_ann";
        mission.point = new BigDecimal(20000);
        mission.xp = new BigDecimal(200);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Subscribe YouTube Channel";
        mission.type = Enums.MissionType.URL;
        mission.isFake = true;
        mission.description = "Subscribe YouTube Channel";
        mission.orders = 5;
        mission.url = "https://youtube.com/@slinky_network?sub_confirmation=1";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        List<QuizResponse> listQuizResponses = new ArrayList<>();
        QuizResponse quizResponse = new QuizResponse();
        quizResponse.index = 1;
        quizResponse.isMultiple = false;
        quizResponse.question = "What is the primary purpose of Slinky Web3AI?";
        quizResponse.answers.add(new QuizResponse.Answer(1, "Trading cryptocurrencies", false));
        quizResponse.answers.add(
          new QuizResponse.Answer(2, "Enabling users to create tokenized AI-powered dApps", true));
        quizResponse.answers.add(new QuizResponse.Answer(3, "Storing NFTs", false));
        quizResponse.answers.add(new QuizResponse.Answer(4, "Managing blockchain security", false));

        listQuizResponses.add(quizResponse);
        quizResponse = new QuizResponse();
        quizResponse.index = 2;
        quizResponse.question = "Slinky's infrastructure derives security from which blockchain?";
        quizResponse.answers.add(new QuizResponse.Answer(1, "Bitcoin, through Babylon", true));
        quizResponse.answers.add(new QuizResponse.Answer(2, "Ethereum", false));
        quizResponse.answers.add(new QuizResponse.Answer(3, "Binance Smart Chain", false));
        quizResponse.answers.add(new QuizResponse.Answer(4, "Solana", false));
        listQuizResponses.add(quizResponse);

        quizResponse = new QuizResponse();
        quizResponse.index = 3;
        quizResponse.question = "Where can you trade AI dApp tokens created with Slinky?";
        quizResponse.answers.add(new QuizResponse.Answer(1, "Uniswap", false));
        quizResponse.answers.add(new QuizResponse.Answer(2, "Binance", false));
        quizResponse.answers.add(new QuizResponse.Answer(3, "Slinky Swap", true));
        quizResponse.answers.add(new QuizResponse.Answer(4, "Kucoin", false));
        listQuizResponses.add(quizResponse);

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Quiz";
        mission.type = Enums.MissionType.QUIZ;
        mission.isFake = false;
        mission.description = Utils.convertObjectToString(listQuizResponses);
        mission.orders = 6;
        mission.url = "";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.partner = partner;
        mission.image = "/assets/images/icons/icon-quiz-gradient.svg";
        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();
      }

      partner = Partner.findByName("MiniTon");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "MiniTon";
        partner.description = "MiniTon is a crypto social esports platform backed by the TON Foundation";
        partner.rewards = "Up to +80,000 points";
        partner.orders = 10;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/mini-ton.png";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join the Telegram Channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join the Telegram Channel";
        mission.orders = 1;
        mission.referId = "MiniTon_Official";
        mission.url = " https://t.me/MiniTon_Official";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Twitter";
        mission.orders = 2;
        mission.url = "https://x.com/RealMiniTon";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Visit the website";
        mission.type = Enums.MissionType.URL;
        mission.isFake = true;
        mission.description = "Visit the website";
        mission.orders = 3;
        mission.url = "https://miniton.mvp.games/";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Play (non- $points) game to get 5000 FREE USDT CHIPS";
        mission.type = Enums.MissionType.PLAY_MINI_TON;
        mission.isFake = false;
        mission.description = "Play game to get 5000 FREE USDT CHIPS";
        mission.orders = 4;
        mission.url = " https://t.me/MiniTonBot/MiniTonGame?startapp=from_U2U";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

      }

      partner = Partner.findByName("Re");
      if (partner == null) {
        Item item = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());
        partner = new Partner();
        partner.name = "Re";
        partner.description = "Re is a real world restaking platform that connects DeFi with reinsurance as a real-world asset (RWA)";
        partner.rewards = "Up to +60,000 points";
        partner.orders = 11;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/re.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Twitter";
        mission.orders = 1;
        mission.url = "https://x.com/re";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";
        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Twitter";
        mission.orders = 2;
        mission.url = "https://x.com/REprotocol";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Discord";
        mission.type = Enums.MissionType.URL;
        mission.isFake = true;
        mission.description = "Join Discord";
        mission.orders = 3;
        mission.url = "https://discord.com/invite/reprotocol";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Telegram channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join Telegram channel";
        mission.orders = 4;
        mission.url = " https://t.me/re_protocol";
        mission.referId = "re_protocol";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";

        mission.create();
        mission.persist();

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = item;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

      }

      partner = Partner.findByName("Openmesh");
      if (partner == null) {
        Item itemOpenmesh = redisService.findItemByCode(Enums.ItemSpecial.OPEN_MESH.name());
        Item itemBox = redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name());

        partner = new Partner();
        partner.name = "Openmesh";
        partner.description = "Building a Decentralized Cloud + Data + Oracle Network for the world, without a middleman!\n" + "\n" + "Giveaway 100,000 OPEN tokens, don't miss out!";
        partner.rewards = "Up to +50,000 points";
        partner.orders = 12;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/open.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow OpenMesh on Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow OpenMesh on Twitter";
        mission.orders = 1;
        mission.url = "https://x.com/OpenmeshNetwork";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.OPEN_MESH;
        mission.rewardImage = itemOpenmesh.image;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join OpenMesh's Telegram";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join OpenMesh's Telegram";
        mission.orders = 2;
        mission.url = " https://t.me/openmesh";
        mission.referId = "openmesh";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.amount = 1L;
        mission.rewardType = Enums.MissionRewardType.CYBER_BOX;
        mission.rewardImage = "/assets/images/upgrade/upgrade-special@2x.png";
        mission.create();
        mission.persist();

        EventMission eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Visit the website";
        mission.type = Enums.MissionType.URL;
        mission.isFake = true;
        mission.description = "Visit the website";
        mission.orders = 3;
        mission.url = "https://www.openmesh.network";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();
      }
    }*/
  }
}