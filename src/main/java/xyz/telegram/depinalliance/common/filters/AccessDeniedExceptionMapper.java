package xyz.telegram.depinalliance.common.filters;

import xyz.telegram.depinalliance.common.exceptions.AccessDeniedException;
import xyz.telegram.depinalliance.common.models.response.ResponseData;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @author holden on 25-Jul-2024
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

  @Override
  public Response toResponse(AccessDeniedException exception) {
    return Response.status(Response.Status.UNAUTHORIZED).entity(ResponseData.error(exception.getMessage()))
      .type(MediaType.APPLICATION_JSON).build();
  }
}
