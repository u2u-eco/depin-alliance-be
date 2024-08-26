package xyz.telegram.depinalliance.common.filters;

import xyz.telegram.depinalliance.common.models.response.ResponseData;
import io.quarkus.security.ForbiddenException;
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
@Priority(Priorities.AUTHENTICATION)
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

  @Override
  public Response toResponse(ForbiddenException exception) {
    return Response.status(Response.Status.FORBIDDEN)
      .entity(ResponseData.error("You don't have enough permissions to perform this action."))
      .type(MediaType.APPLICATION_JSON).build();
  }
}
