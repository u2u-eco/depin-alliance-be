package xyz.telegram.depinalliance.services;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.quarkus.rest.client.reactive.NotBody;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import xyz.telegram.depinalliance.common.models.response.*;

/**
 * @author holden on 05-Aug-2024
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "twitter-auth")
@Path("/2")
public interface TwitterAuth {

  @GET
  @Path("users/me")
  Response getInfo(@HeaderParam("Authorization") String authorization);
}
