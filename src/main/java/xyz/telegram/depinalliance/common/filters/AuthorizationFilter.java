package xyz.telegram.depinalliance.common.filters;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import xyz.telegram.depinalliance.common.exceptions.AccessDeniedException;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author holden on 25-Jul-2024
 */
@Provider
@Dependent
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

  @Context
  ResourceInfo resourceInfo;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    Method method = resourceInfo.getResourceMethod();
    // @DenyAll on the method takes precedence over @RolesAllowed and @PermitAll
    if (method.isAnnotationPresent(DenyAll.class)) {
      throw new AccessDeniedException("You don't have permissions to perform this action.");
    }
    // @PermitAll on the method takes precedence over @RolesAllowed on the class
    if (method.isAnnotationPresent(PermitAll.class)) {
      return;
    }
    // @PermitAll on the class
    if (resourceInfo.getResourceClass().isAnnotationPresent(PermitAll.class)) {
      return;
    }
    // Authentication is required for non-annotated methods
    if (!isAuthenticated(requestContext)) {
      throw new AccessDeniedException("Authentication is required to perform this action.");
    }
  }

  private boolean isAuthenticated(final ContainerRequestContext requestContext) {
    return requestContext.getSecurityContext().getUserPrincipal() != null;
  }
}
