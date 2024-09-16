package xyz.telegram.depinalliance.services;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * @author holden on 16-Sep-2024
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "mini-ton-api")
public interface MiniTonClient {
  @GET
  @Path("api/v1/channel/u2u_verify_played_2_cash_matches")
  Boolean verify(@QueryParam("telegram_id") Long telegramId);
}
