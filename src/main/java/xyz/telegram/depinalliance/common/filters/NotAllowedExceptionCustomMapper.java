package xyz.telegram.depinalliance.common.filters;

import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import xyz.telegram.depinalliance.common.models.response.ResponseData;

/**
 * @author holden on 25-Jul-2024
 */
@Provider
public class NotAllowedExceptionCustomMapper implements ExceptionMapper<NotAllowedException> {

  @Override
  public Response toResponse(NotAllowedException exception) {
    return Response.status(Response.Status.OK).entity(ResponseData.error(exception.getMessage()))
      .type(MediaType.APPLICATION_JSON).build();
  }
}
