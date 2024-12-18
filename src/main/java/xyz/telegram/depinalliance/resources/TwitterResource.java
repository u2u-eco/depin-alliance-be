package xyz.telegram.depinalliance.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.telegram.depinalliance.common.configs.TwitterConfig;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.models.response.TwitterAccessTokenResponse;
import xyz.telegram.depinalliance.common.models.response.TwitterErrorResponse;
import xyz.telegram.depinalliance.common.models.response.TwitterUserResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;
import xyz.telegram.depinalliance.services.RedisService;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author holden on 09-Oct-2024
 */
@Path("/twitter")
public class TwitterResource extends BaseResource {

  private static final Logger log = LoggerFactory.getLogger(TwitterResource.class);
  @Inject
  RedisService redisService;
  @Inject
  TwitterConfig twitterConfig;

  @GET
  @Path("/info")
  public ResponseData<?> info() {
    UserSocial userSocial = redisService.findUserSocial(getTelegramId());
    UserSocialResponse userSocialResponse = new UserSocialResponse();
    if (userSocial != null) {
      userSocialResponse.twitterName = userSocial.twitterName;
      userSocialResponse.twitterUsername = userSocial.twitterUsername;
    }
    return ResponseData.ok(userSocialResponse);
  }

  @GET
  @Path("/login")
  @Transactional
  public ResponseData<?> login() {
    UserSocial userSocial = redisService.findUserSocial(getTelegramId());
    if (userSocial != null && userSocial.twitterUid != null && userSocial.twitterUid > 0) {
      UserSocialResponse userSocialResponse = new UserSocialResponse();
      userSocialResponse.twitterName = userSocial.twitterName;
      userSocialResponse.twitterUsername = userSocial.twitterUsername;
      return ResponseData.ok(userSocialResponse);
    }
    String state = DigestUtils.md5Hex(getTelegramId() + "");
    if (userSocial != null) {
      Map<String, Object> params = new HashMap<>();
      params.put("id", userSocial.userId);
      params.put("twitterToken", state);
      UserSocial.update("twitterToken = :twitterToken where id = :id", params);
    } else {
      userSocial = new UserSocial();
      userSocial.userId = getTelegramId();
      userSocial.twitterToken = state;
      userSocial.persist();
    }
    redisService.clear("USER_SOCIAL_" + getTelegramId());
    UriBuilder uriBuilder = UriBuilder.fromUri("https://x.com/i/oauth2/authorize").queryParam("response_type", "code")
      .queryParam("client_id", twitterConfig.clientId()).queryParam("redirect_uri", twitterConfig.callbackUrl())
      .queryParam("scope", "users.read follows.read tweet.read like.read offline.access").queryParam("state", state)
      .queryParam("code_challenge", DigestUtils.md5Hex(state)).queryParam("code_challenge_method", "plain");
    return ResponseData.ok(uriBuilder.build().toString());
  }

  @GET
  @Path("/url")
  public ResponseData<?> getUrlTwitter() {
    String state = DigestUtils.md5Hex(getTelegramId() + "");
    UriBuilder uriBuilder = UriBuilder.fromUri("https://x.com/i/oauth2/authorize").queryParam("response_type", "code")
      .queryParam("client_id", twitterConfig.clientId()).queryParam("redirect_uri", twitterConfig.callbackUrl())
      .queryParam("scope", "users.read follows.read tweet.read like.read offline.access").queryParam("state", state)
      .queryParam("code_challenge", DigestUtils.md5Hex(state)).queryParam("code_challenge_method", "plain");
    return ResponseData.ok(uriBuilder.build().toString());
  }

  @GET
  @Path("/change-account")
  @Transactional
  public ResponseData<?> changeAccount() {
    UserSocial userSocial = redisService.findUserSocial(getTelegramId());
    String state = DigestUtils.md5Hex(getTelegramId() + "");
    if (userSocial != null) {
      Map<String, Object> params = new HashMap<>();
      params.put("id", userSocial.userId);
      params.put("twitterToken", state);
      UserSocial.update("twitterToken = :twitterToken where id = :id", params);
    } else {
      userSocial = new UserSocial();
      userSocial.userId = getTelegramId();
      userSocial.twitterToken = state;
      userSocial.persist();
    }
    redisService.clear("USER_SOCIAL_" + getTelegramId());
    return ResponseData.ok();
  }

  @GET
  @Path("/callback")
  @PermitAll
  @Transactional
  public Response callback(@QueryParam("code") String code, @QueryParam("state") String state,
    @QueryParam("error") String error) {
    if (StringUtils.isNotBlank(error)) {
      throw new BusinessException(error);
    }
    if (StringUtils.isBlank(code) || StringUtils.isBlank(state)) {
      throw new BusinessException("Data invalid");
    }
    UserSocial userSocial = UserSocial.find("twitterToken", state).firstResult();
    if (userSocial == null) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    Client client = null;
    Response tokenResponse = null;
    try {
      client = ClientBuilder.newClient();
      Form form = new Form();
      form.param("grant_type", "authorization_code");
      form.param("code", code);
      form.param("redirect_uri", twitterConfig.callbackUrl());
      form.param("client_id", twitterConfig.clientId());
      form.param("code_verifier", DigestUtils.md5Hex(state));
      tokenResponse = client.target("https://api.x.com/2/oauth2/token").request(MediaType.APPLICATION_JSON)
        .post(Entity.form(form));
      if (tokenResponse != null) {
        TwitterAccessTokenResponse accessToken = tokenResponse.readEntity(TwitterAccessTokenResponse.class);
        String bearerToken = "Bearer " + accessToken.accessToken;
        HttpClient clientHttp = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.twitter.com/2/users/me"))
          .header("Authorization", bearerToken).GET().build();
        HttpResponse<String> response1 = clientHttp.send(request, HttpResponse.BodyHandlers.ofString());
        if (response1.statusCode() == 200) {
          TwitterUserResponse user = Utils.toObject(response1.body(), TwitterUserResponse.class);
          if (user == null || user.data == null) {
            throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
          }
          UserSocialHistory accountTwitter = UserSocialHistory.findById(user.data.id);
          if (accountTwitter != null && !Objects.equals(accountTwitter.user.id, userSocial.userId)) {
            throw new BusinessException(
              "This Twitter: " + user.data.username + " has been connected to another account");
          }
          Map<String, Object> params = new HashMap<>();
          params.put("id", userSocial.userId);
          params.put("twitterName", user.data.name);
          params.put("twitterUid", user.data.id);
          params.put("twitterUsername", user.data.username);
          if (UserSocial.update(
            "twitterToken = null, twitterName = :twitterName, twitterUid = :twitterUid, twitterUsername = :twitterUsername where userId = :id",
            params) != 1) {
            throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
          }
          if (accountTwitter == null) {
            accountTwitter = new UserSocialHistory();
            accountTwitter.user = new User(userSocial.userId);
            accountTwitter.twitterUid = Long.parseLong(user.data.id);
            accountTwitter.create();
            accountTwitter.persist();
          }
          redisService.clear("USER_SOCIAL_" + userSocial.userId);
          try {
            Mission mission = redisService.findMissionByType(Enums.MissionType.CONNECT_X);
            if (mission != null) {
              UserMission userMission = UserMission.find("mission.id = ?1 and user.id = ?2", mission.id,
                userSocial.userId).firstResult();
              if (userMission == null) {
                userMission = new UserMission();
                userMission.mission = mission;
                userMission.user = new User(userSocial.userId);
                userMission.status = Enums.MissionStatus.VERIFIED;
                UserMission.create(userMission);
              }
              redisService.clearMissionUser("REWARD", userSocial.userId);
            }
          } catch (Exception ignored) {
          }
          URL url = new URL(twitterConfig.redirectUrl());
          return Response.temporaryRedirect(url.toURI()).build();
        } else {
          TwitterErrorResponse errorRs = Utils.toObject(response1.body(), TwitterErrorResponse.class);
          throw new BusinessException(errorRs.detail);
        }
      } else {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
    } catch (BusinessException e) {
      log.error(Utils.printLogStackTrace(e));
      throw e;
    } catch (Exception e) {
      if (client != null) {
        client.close();
      }
      if (tokenResponse != null) {
        tokenResponse.close();
      }
      log.error(Utils.printLogStackTrace(e));
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
  }
}
