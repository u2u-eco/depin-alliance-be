package xyz.telegram.depinalliance.common.filters;

import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @author holden on 25-Jul-2024
 */
@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

  @Override
  public Response toResponse(BusinessException exception) {
    return Response.status(Response.Status.OK).entity(ResponseData.error(exception.getMessage(), exception.getError()))
      .type(MediaType.APPLICATION_JSON).build();
  }
}
