package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.entities.User;

import java.security.Principal;

/**
 * @author holden on 25-Jul-2024
 */
public class BaseResource {
  @Inject
  SecurityContext ctx;

  public Long getTelegramId() {
    Principal principal = ctx.getUserPrincipal();
    return principal != null ? Long.valueOf(ctx.getUserPrincipal().getName()) : null;
  }

  public User getUser() throws BusinessException {
    if (ctx.getUserPrincipal() != null) {
      User user = User.findById(getTelegramId());
      if (user == null) {
        throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
      }
      return user;
    } else {
      return null;
    }
  }

}