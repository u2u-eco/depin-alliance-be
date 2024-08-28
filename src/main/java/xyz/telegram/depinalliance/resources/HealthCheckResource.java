package xyz.telegram.depinalliance.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 10-Jul-2024
 */
@Path(("api/v1/health"))
public class HealthCheckResource {

  @GET
  @Path("")
  @PermitAll
  public Object healthCheck() {
    Map<String, String> map = new HashMap<>();
    map.put("commit", System.getenv("GIT_COMMIT"));
    map.put("date", System.getenv("COMPILED_AT"));
    map.put("version", System.getenv("GIT_TAG"));
    Map<String, Object> response = new HashMap<>();
    response.put("data", map);
    response.put("status", true);
    return response;
  }
}
