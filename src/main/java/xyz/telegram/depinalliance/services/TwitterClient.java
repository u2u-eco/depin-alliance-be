package xyz.telegram.depinalliance.services;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import xyz.telegram.depinalliance.common.models.response.TwitterFollowResponse;

/**
 * @author holden on 05-Aug-2024
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "twitter-api")

public interface TwitterClient {
  @GET
  @Path("/user/following/continuation")
  @ClientQueryParam(name = "limit", value = "100")
  @ClientHeaderParam(name = "x-rapidapi-host", value = "${twitter.rapidapi-host}")
  @ClientHeaderParam(name = "x-rapidapi-key", value = "${twitter.rapidapi-key}")
  TwitterFollowResponse getFollowingContinuation(@QueryParam("user_id") String userId,
    @QueryParam("continuation_token") String continuationToken);

  @GET
  @Path("/user/following")
  @ClientQueryParam(name = "limit", value = "100")
  @ClientHeaderParam(name = "x-rapidapi-host", value = "${twitter.rapidapi-host}")
  @ClientHeaderParam(name = "x-rapidapi-key", value = "${twitter.rapidapi-key}")
  TwitterFollowResponse getFollowing(@QueryParam("user_id") String userId);
}
