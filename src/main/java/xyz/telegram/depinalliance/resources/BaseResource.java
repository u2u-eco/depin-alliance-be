package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;

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

}