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
    //  void onSonStart() {
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
          mission.referId = "@DePIN_App";
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

          Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
          mission.referId = "@UnicornUltraGlobal";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
        partner = new Partner();
        partner.name = "U2DPN";
        partner.description = "Embrace the revolution of the decentralized Internet and monetize your unused bandwidth";
        partner.rewards = "Up to +90,000 points";
        partner.orders = 0;
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
        mission.referId = "@UnicornUltraGlobal";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        mission.referId = "@mizzleio";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        mission.referId = "@depindora";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        mission.referId = "@ZkAGI_AI";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        mission.referId = "@Gemjungle";
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
        mission.referId = "@HermesCryptohub";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        mission.referId = "@DextrExchange";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        mission.referId = "@azenprotocol";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        mission.referId = "@slinky_network";
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
        mission.referId = "@slinky_ann";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        mission.referId = "@MiniTon_Official";
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
        Item item = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
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
        mission.referId = "@re_protocol";
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
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();

        partner = new Partner();
        partner.name = "Openmesh";
        partner.description = "Building a Decentralized Cloud + Data + Oracle Network for the world, without a middleman!\n" + "\n" + "<p class=\"text-white font-semibold\">Giveaway 100,000 OPEN tokens, don't miss out!</p>";
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
        mission.referId = "@openmesh";
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

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Retweet";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Retweet";
        mission.orders = 4;
        mission.url = "https://x.com/OpenmeshNetwork/status/1838514412207894861?t=BzTdlKeUf02MucycwBGD8Q&s=19";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();
      }

      partner = Partner.findByName("Timpi");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();

        partner = new Partner();
        partner.name = "Timpi";
        partner.description = "Timpi is the first decentralized web index, and one of the largest databases in the world. With various products and real world use cases, Timpi’s mission is to democratize the world’s access to information. \n" + "\n" + "<p class=\"text-white font-semibold\">Giveaway 17,500 NTMPI tokens, don't miss out!</p>";
        partner.rewards = "Up to +80,000 points";
        partner.orders = 13;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/timpi.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = "Follow Twitter";
        mission.orders = 1;
        mission.url = "https://x.com/Timpi_TheNewWay";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Community";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join Community";
        mission.orders = 2;
        mission.url = " https://t.me/TimpiMe";
        mission.referId = "@TimpiMe";
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
        mission.url = "https://timpi.io";
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
        quizResponse.question = "What is the name and ticker symbol of Timpi's native token?";
        quizResponse.answers.add(new QuizResponse.Answer(1, "Timpi Coin ($TPC)", false));
        quizResponse.answers.add(new QuizResponse.Answer(2, "Neutaro Token ($NTMPI)", true));
        quizResponse.answers.add(new QuizResponse.Answer(3, "Timpi Dollar ($TMD)", false));
        quizResponse.answers.add(new QuizResponse.Answer(4, "Timpi Credit ($TMC)", false));
        listQuizResponses.add(quizResponse);

        quizResponse = new QuizResponse();
        quizResponse.index = 2;
        quizResponse.question = "Which of the following best describes the utility of the $NTMPI token within the Timpi ecosystem?";
        quizResponse.answers.add(new QuizResponse.Answer(1,
          "It’s used for governance, staking, node rewards, accessing ad credits and node NFTs, and for buybacks based on revenue generated by Timpi’s products",
          true));
        quizResponse.answers.add(
          new QuizResponse.Answer(2, "It is solely a speculative investment with no practical utility", false));
        quizResponse.answers.add(new QuizResponse.Answer(3,
          "It is a stablecoin pegged to major world currencies for international transactions", false));
        quizResponse.answers.add(
          new QuizResponse.Answer(4, "It is used exclusively for purchasing Timpi-branded merchandise", false));
        listQuizResponses.add(quizResponse);

        quizResponse = new QuizResponse();
        quizResponse.index = 3;
        quizResponse.question = "Where can you acquire Timpi's tokens?";
        quizResponse.answers.add(
          new QuizResponse.Answer(1, "By purchasing them on traditional stock exchanges", false));
        quizResponse.answers.add(
          new QuizResponse.Answer(2, "Through BitMart Centralized exchange and Cosmos based Decentralized exchange",
            true));
        quizResponse.answers.add(new QuizResponse.Answer(3, "From the shady guy in the corner of your street", false));
        quizResponse.answers.add(
          new QuizResponse.Answer(4, "By earning them exclusively through referral programs", false));
        listQuizResponses.add(quizResponse);

        quizResponse = new QuizResponse();
        quizResponse.index = 4;
        quizResponse.question = "Which of the following best describes the primary function of Timpi's Guardian Nodes in the TAP network?";
        quizResponse.answers.add(
          new QuizResponse.Answer(1, "Storing web data securely in a decentralized manner", true));
        quizResponse.answers.add(
          new QuizResponse.Answer(2, "Connecting to GeoCore nodes to perform web crawling operations", false));
        quizResponse.answers.add(
          new QuizResponse.Answer(3, "Handling search requests and separating the network from users", false));
        quizResponse.answers.add(new QuizResponse.Answer(4, "Managing user accounts and personal preferences", false));
        listQuizResponses.add(quizResponse);

        quizResponse = new QuizResponse();
        quizResponse.index = 5;
        quizResponse.question = "What is the name of Timpi's referral program?";
        quizResponse.answers.add(new QuizResponse.Answer(1, "Knight Timplars", true));
        quizResponse.answers.add(new QuizResponse.Answer(2, "Timpi Ambassadors", false));
        quizResponse.answers.add(new QuizResponse.Answer(3, "Guardians Guild", false));
        quizResponse.answers.add(new QuizResponse.Answer(4, "Search Crusaders", false));
        listQuizResponses.add(quizResponse);

        quizResponse = new QuizResponse();
        quizResponse.index = 6;
        quizResponse.question = "What has Timpi built?";
        quizResponse.answers.add(new QuizResponse.Answer(1,
          "The world's first non-custodial exchange specializing in floating point RWA assets", false));
        quizResponse.answers.add(new QuizResponse.Answer(2,
          "The world’s first decentralized web index, a structured database of the internet, on par with e,.g. Bing",
          true));
        quizResponse.answers.add(new QuizResponse.Answer(3,
          "The first protocol optimizing user yields by automatically arbitraging the underlying assets across multiple chains",
          false));
        quizResponse.answers.add(new QuizResponse.Answer(4,
          "The first decentralized oracle network enabling real world data acquisition from local resource markets",
          false));
        listQuizResponses.add(quizResponse);

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "IQ Quiz";
        mission.type = Enums.MissionType.QUIZ;
        mission.isFake = false;
        mission.description = Utils.convertObjectToString(listQuizResponses);
        mission.orders = 4;
        mission.url = "";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.rewardType = Enums.MissionRewardType.TIMPI;
        mission.rewardImage = "/assets/images/workspace/timpi.png";
        mission.partner = partner;
        mission.image = "/assets/images/icons/icon-quiz-gradient.svg";
        mission.create();
        mission.persist();
        redisService.clearCacheByPrefix("MISSION_REWARD_NOT_ONE_TIME_");
      }

      partner = Partner.findByName("Ton AI");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();

        partner = new Partner();
        partner.name = "Ton AI";
        partner.description = "Ton AI provides the best growth solutions for Telegram ecosystem projects, driving targeted traffic rapidly for projects.";
        partner.rewards = "Up to +40,000 points";
        partner.orders = 14;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/ton-ai.png";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Play Ton AI and Earn 10 USDT";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = "Play Ton AI and Earn 10 USDT";
        mission.orders = 1;
        mission.url = "http://t.me/PeaAIBot/CashRally?startapp=cid-66b20324baee0f0035a5b4d8_ch-u2u";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Ton AI Channel and Earn";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = "Join Ton AI Channel and Earn";
        mission.orders = 2;
        mission.url = " https://t.me/+UGViuMymlixmNTk9";
        mission.referId = "@Ton_AI_News";
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
      }

      partner = Partner.findByName("Resolv");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();

        partner = new Partner();
        partner.name = "Resolv";
        partner.description = "A protocol that puts stolen crypto back in your wallet";
        partner.rewards = "Up to +60,000 points";
        partner.orders = 15;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/resolv.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://x.com/resolvcrypto";
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
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Connect your wallet to get $250,000 in free protection";
        mission.type = Enums.MissionType.URL;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 2;
        mission.url = " https://resolv.finance?utm_source=Telegram+Minigames&utm_medium=minigames&utm_campaign=DePin+Alliance";
        mission.point = new BigDecimal(20000);
        mission.xp = new BigDecimal(200);
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Founder’s Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 3;
        mission.url = "https://x.com/DaKingLawson";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Like this post";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 4;
        mission.url = "https://x.com/resolvcrypto/status/1831425859204600316";
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

        eventMission = new EventMission();
        eventMission.event = new Event(Enums.EventId.CYBER_BOX.getId());
        eventMission.mission = mission;
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Retweet this post";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 5;
        mission.url = "https://x.com/resolvcrypto/status/1831425859204600316";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();
      }

      partner = Partner.findByName("Openloot");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();

        partner = new Partner();
        partner.name = "Openloot";
        partner.description = "A protocol that puts stolen crypto back in your wallet";
        partner.rewards = "Up to +10,000 points";
        partner.orders = 16;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/openloot.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Clockie Chaos app";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://t.me/ClockieChaosBot/ClockieChaos?startapp=source_U2U";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
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
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();
      }

      partner = Partner.findByName("TelegaMAT Community");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
        partner = new Partner();
        partner.name = "TelegaMAT Community";
        partner.description = "TelegaMAT Community Is a Ukrainian community providing airdrop and crypto market news with nearly 2.5M subscribers";
        partner.rewards = "Up to +40,000 points";
        partner.orders = 17;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/telega-mat.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Bullish Farm Bot";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://t.me/BullishFarmBot/start?startapp=r_7041788989";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
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
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join TelegaMAT Community";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = mission.name;
        mission.orders = 2;
        mission.url = "https://t.me/kriptovalyuta_airdrop";
        mission.referId = "@kriptovalyuta_airdrop";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();
      }

      partner = Partner.findByName("Mhaya");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
        partner = new Partner();
        partner.name = "Mhaya";
        partner.description = "Mhaya is transforming the classic game of Monopoly into a groundbreaking \"Monopoly-GameFi\" blockchain experience. Dive into our urban simulation game where you can build, stake, and earn rewards in a decentralized world. With 40 game board blocks and 12 event types, players roll dice to navigate their city, upgrade buildings, and earn HAYA and MAYA tokens. Experience gaming and earning like never before!";
        partner.rewards = "Up to +10,000 points";
        partner.orders = 18;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/mhaya.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Mhaya Play & Spin to win";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://t.me/mhaya_bot?start=28ABuxL1YEC";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
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
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();
      }

      partner = Partner.findByName("PUNKY");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
        partner = new Partner();
        partner.name = "PUNKY";
        partner.description = "I am PUNKY. An Ai-driven Creative Discord Moderator. I utilize machine learning and seamless automation through intelligent Web3 community growth";
        partner.rewards = "Up to +70,000 points";
        partner.orders = 19;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/punky.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://x.com/PunkyAi";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Retweet this post";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 2;
        mission.url = "https://x.com/PunkyAi/status/1838223162414112965";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Reply \"PUNK’S NOT DEAD\" to this post";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 3;
        mission.url = "https://x.com/PunkyAi/status/1838223162414112965";
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
        mission.description = mission.name;
        mission.orders = 4;
        mission.url = "https://discord.gg/PUNKY";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Telegram";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = mission.name;
        mission.orders = 5;
        mission.url = "https://t.me/punkyai";
        mission.referId = "@punkyai";
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
      }

      partner = Partner.findByName("TenEx");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
        partner = new Partner();
        partner.name = "TenEx";
        partner.description = "TENEX is a cutting-edge decentralized finance (DeFi) platform that's reimagining how we interact with digital assets. It's designed to make cryptocurrency trading and investment more accessible, efficient, and\n" + " rewarding for everyone – from crypto enthusiasts to traditional finance professionals";
        partner.rewards = "Up to +60,000 points";
        partner.orders = 20;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/ten-ex.png";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Telegram";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://t.me/+Bq6Csh-d7pw1NzM9";
        mission.referId = "-1002167477745";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 2;
        mission.url = "https://x.com/tenex_official?s=21";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-x-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Visit Website";
        mission.type = Enums.MissionType.URL;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 3;
        mission.url = "https://tenex.finance/";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
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
        mission.name = "Join Discord";
        mission.type = Enums.MissionType.URL;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 4;
        mission.url = "http://discord.gg/VcrzxkYV6y";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();
      }

      partner = Partner.findByName("PeckS");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
        partner = new Partner();
        partner.name = "PeckS";
        partner.description = "The native Telegram and TikTok meme coin \uD83E\uDEB5 Airdrop for the OGs! \uD83E\uDD11";
        partner.rewards = "Up to +50,000 points";
        partner.orders = 1;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/pecks.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow Twitter";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://x.com/realPecksHouse";
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
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Telegram Channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = false;
        mission.description = mission.name;
        mission.orders = 2;
        mission.url = "https://t.me/realPeckS/";
        mission.referId = "@realPeckS";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join Bot";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 3;
        mission.url = "https://t.me/realpecks_bot";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();
      }

      partner = Partner.findByName("VOVO");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
        partner = new Partner();
        partner.name = "VOVO";
        partner.description = "Vovo is an innovative project centered on creating a rewarding experience within the blockchain ecosystem";
        partner.rewards = "Up to +10,000 points";
        partner.orders = 22;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/vovo.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Play Granny VOVO Bot";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://t.me/VovoTeleBot/OneApp?startapp=5916134027";
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
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();
      }
      partner = Partner.findByName("PANIE");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
        partner = new Partner();
        partner.name = "PANIE";
        partner.description = "PANIE is a decentralised DeFi application on the TON Blockchain, committed to promoting growth and decentralisation within the TON Ecosystem. It offers a secure on-chain experience, seamlessly integrating with Telegram to enable users to mine PANIE tokens and take part in various activities";
        partner.rewards = "Up to +10,000 points";
        partner.orders = 23;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/public/mission/panie.jpg";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join PANIE app";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://t.me/Panieapp_bot/launch?startapp=DePIN_Alliance_Bot";
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
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();
      }

      partner = Partner.findByName("FACABY");
      if (partner == null) {
        Item itemBox = Item.find("code", Enums.ItemSpecial.CYBER_BOX.name()).firstResult();
        partner = new Partner();
        partner.name = "FACABY";
        partner.description = "FACABY, the ultimate TON-based airdrop and real-money game!\n" + "As a meme game, FACABY rewards user based on the Telegram account's age and activity and join the lucky wheel daily to win $TON and $FACABY reward points! \n" + "Facaby is building the own meme world for community and going to conquer the gamefi space!";
        partner.rewards = "Up to +50,000 points";
        partner.orders = 24;
        partner.participants = 0;
        partner.isActive = true;
        partner.image = "https://depintele.s3.ap-southeast-1.amazonaws.com/test/mission/FACABY.png";
        partner.create();
        partner.persist();

        Mission mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Follow FACABY";
        mission.type = Enums.MissionType.TWITTER;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 1;
        mission.url = "https://x.com/Facabyonton";
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
        eventMission.item = itemBox;
        eventMission.number = 1;
        eventMission.create();
        eventMission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join FACABY Channel";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 2;
        mission.url = "https://t.me/facabyofficial";
        mission.referId = "facabyofficial";
        mission.point = new BigDecimal(30000);
        mission.xp = new BigDecimal(500);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();

        mission = new Mission();
        mission.groupMission = partner.name;
        mission.name = "Join FACABY Game";
        mission.type = Enums.MissionType.TELEGRAM;
        mission.isFake = true;
        mission.description = mission.name;
        mission.orders = 3;
        mission.url = "https://t.me/facaby_bot/facaby?startapp=GI59tE13E";
        mission.point = new BigDecimal(10000);
        mission.xp = new BigDecimal(100);
        mission.image = "/assets/images/icons/icon-telegram-gradient.svg";
        mission.partner = partner;
        mission.isActive = true;
        mission.create();
        mission.persist();
      }
    }
  }
}