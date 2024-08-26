package xyz.telegram.depinalliance.common.filters;

import xyz.telegram.depinalliance.common.models.response.ResponseData;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @author holden on 25-Jul-2024
 */
@Provider
public class NotFoundExceptionCustomMapper implements ExceptionMapper<NotFoundException> {

  @Override
  public Response toResponse(NotFoundException exception) {
    return Response.status(Response.Status.OK).entity(ResponseData.error(exception.getMessage()))
      .type(MediaType.APPLICATION_JSON).build();
  }
}
