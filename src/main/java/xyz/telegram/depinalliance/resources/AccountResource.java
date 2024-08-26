package xyz.telegram.depinalliance.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.models.request.TelegramInitDataRequest;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.models.response.UserTelegramResponse;
import xyz.telegram.depinalliance.services.JwtService;
import xyz.telegram.depinalliance.services.TelegramService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 26-Jul-2024
 */
@Path("/accounts")
public class AccountResource extends BaseResource {

  @Inject
  TelegramService telegramService;
  @Inject
  JwtService jwtService;

  @POST
  @Path("/auth")
  @PermitAll
  @Transactional
  public ResponseData auth(TelegramInitDataRequest request) {
    UserTelegramResponse userTelegramResponse = telegramService.validateInitData(request.initData);
    if (userTelegramResponse == null) {
      return ResponseData.error(ResponseMessageConstants.HAS_ERROR);
    }
//    PbkAccount pbkAccount = PbkAccount.findById(userTelegramResponse.id);
//    if (pbkAccount == null) {
//      pbkAccount = new PbkAccount();
//      pbkAccount.id = userTelegramResponse.id;
//      pbkAccount.username = userTelegramResponse.username;
//      PbkAccount.createAccount(pbkAccount);
//    } else if (!userTelegramResponse.username.equals(pbkAccount.username)) {
//      Map<String, Object> params = new HashMap<>();
//      params.put("id", pbkAccount.id);
//      params.put("username", userTelegramResponse.username);
//      PbkAccount.updateAccount("username = :username where id = :id", params);
//    }
    return ResponseData.ok(jwtService.generateToken(String.valueOf(userTelegramResponse.id)));
  }

}
