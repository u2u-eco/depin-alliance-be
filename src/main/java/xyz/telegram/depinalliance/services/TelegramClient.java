package xyz.telegram.depinalliance.services;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import xyz.telegram.depinalliance.common.models.response.TelegramResponse;

/**
 * @author holden on 01-Aug-2024
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "telegram-api")
public interface TelegramClient {
  @GET
  @Path("/bot{token}/getChatMember")
  TelegramResponse getChatMember(@PathParam("token") String token, @QueryParam("chat_id") String chatId,
    @QueryParam("user_id") String userId);

}
