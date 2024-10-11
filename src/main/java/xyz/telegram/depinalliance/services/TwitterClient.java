package xyz.telegram.depinalliance.services;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import xyz.telegram.depinalliance.common.models.response.TwitterFollowResponse;
import xyz.telegram.depinalliance.common.models.response.TwitterRepliesResponse;
import xyz.telegram.depinalliance.common.models.response.TwitterRetweetsResponse;

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

  @GET
  @Path("/tweet/retweets")
  @ClientQueryParam(name = "limit", value = "100")
  @ClientHeaderParam(name = "x-rapidapi-host", value = "${twitter.rapidapi-host}")
  @ClientHeaderParam(name = "x-rapidapi-key", value = "${twitter.rapidapi-key}")
  TwitterRetweetsResponse getRetweets(@QueryParam("tweet_id") String tweetId);

  @GET
  @Path("/tweet/retweets/continuation")
  @ClientQueryParam(name = "limit", value = "100")
  @ClientHeaderParam(name = "x-rapidapi-host", value = "${twitter.rapidapi-host}")
  @ClientHeaderParam(name = "x-rapidapi-key", value = "${twitter.rapidapi-key}")
  TwitterRetweetsResponse getRetweetsContinuation(@QueryParam("tweet_id") String tweetId,
    @QueryParam("continuation_token") String continuationToken);

  @GET
  @Path("/tweet/replies")
  @ClientQueryParam(name = "limit", value = "100")
  @ClientHeaderParam(name = "x-rapidapi-host", value = "${twitter.rapidapi-host}")
  @ClientHeaderParam(name = "x-rapidapi-key", value = "${twitter.rapidapi-key}")
  TwitterRepliesResponse getReplies(@QueryParam("tweet_id") String tweetId);

  @GET
  @Path("/tweet/replies/continuation")
  @ClientQueryParam(name = "limit", value = "100")
  @ClientHeaderParam(name = "x-rapidapi-host", value = "${twitter.rapidapi-host}")
  @ClientHeaderParam(name = "x-rapidapi-key", value = "${twitter.rapidapi-key}")
  TwitterRepliesResponse getRepliesContinuation(@QueryParam("tweet_id") String tweetId,
    @QueryParam("continuation_token") String continuationToken);
}
