package xyz.telegram.depinalliance.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import twitter4j.User;
import twitter4j.auth.RequestToken;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.entities.UserSocial;
import xyz.telegram.depinalliance.services.RedisService;
import xyz.telegram.depinalliance.services.TwitterService;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 09-Oct-2024
 */
@Path("/twitter")
public class TwitterResource extends BaseResource {

  @Inject
  RedisService redisService;
  @Inject
  TwitterService twitterService;

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
  public ResponseData<?> login() throws Exception {
    UserSocial userSocial = redisService.findUserSocial(getTelegramId());
    if (userSocial != null && userSocial.twitterUid != null && userSocial.twitterUid > 0) {
      throw new BusinessException(ResponseMessageConstants.USER_LINKED_TELEGRAM);
    }
    RequestToken requestToken = twitterService.getRequestToken();
    if (userSocial != null) {
      Map<String, Object> params = new HashMap<>();
      params.put("id", userSocial.userId);
      params.put("twitterToken", requestToken.getToken());
      UserSocial.update("twitterToken = :twitterToken where id = :id", params);
    } else {
      userSocial = new UserSocial();
      userSocial.userId = getTelegramId();
      userSocial.twitterToken = requestToken.getToken();
      userSocial.persist();
    }
    redisService.clearCacheByPrefix("USER_SOCIAL_" + getTelegramId());
    return ResponseData.ok(requestToken.getAuthorizationURL());
  }

  @GET
  @Path("test")
  @PermitAll
  public Response test() throws MalformedURLException, URISyntaxException {
    URL url = new URL("https://t.me/HoldenDepinBot/");
    return Response.temporaryRedirect(url.toURI()).build();
  }

  @GET
  @Path("/callback")
  @PermitAll
  @Transactional
  public Response verifyUser(@QueryParam("oauth_token") String oauthToken,
    @QueryParam("oauth_verifier") String oauthVerifier) throws Exception {
    if (StringUtils.isBlank(oauthToken) && StringUtils.isBlank(oauthVerifier)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    UserSocial userSocial = UserSocial.find("twitterToken", oauthToken).firstResult();
    if (userSocial == null || userSocial.twitterUid != null && userSocial.twitterUid > 0) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    User user = twitterService.verifyCredentials(oauthToken, oauthVerifier);
    if (user == null) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    UserSocial accountTwitter = UserSocial.find("twitterUid", user.getId()).firstResult();
    if (accountTwitter != null) {
      throw new BusinessException("This Twitter: " + user.getScreenName() + " has been connected to another account");
    }
    Map<String, Object> params = new HashMap<>();
    params.put("id", userSocial.userId);
    params.put("twitterName", user.getName());
    params.put("twitterUid", user.getId());
    params.put("twitterUsername", user.getScreenName());
    if (UserSocial.update(
      "twitterToken = null, twitterName = :twitterName, twitterUid = :twitterUid, twitterUsername = :twitterUsername where userId = :id and twitterUid is null",
      params) != 1) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    redisService.clearCacheByPrefix("USER_SOCIAL_" + userSocial.userId);
    URL url = new URL("https://t.me/HoldenDepinBot/holden?startapp=twitter");
    return Response.temporaryRedirect(url.toURI()).build();
  }
}
