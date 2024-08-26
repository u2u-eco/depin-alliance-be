package xyz.telegram.depinalliance.common.filters;

import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.utils.Utils;
import io.quarkus.logging.Log;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * @author holden on 25-Jul-2024
 */
@Provider
public class ExceptionCustomMapper implements ExceptionMapper<Exception> {
  @Override
  public Response toResponse(Exception exception) {
    Log.error(Utils.printLogStackTrace(exception));
    return Response.status(Response.Status.OK).entity(ResponseData.error(ResponseMessageConstants.HAS_ERROR))
      .type(MediaType.APPLICATION_JSON).build();
  }
}
