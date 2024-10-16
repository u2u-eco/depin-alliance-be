package xyz.telegram.depinalliance.common.models.request;

import jakarta.ws.rs.FormParam;

/**
 * @author holden on 15-Oct-2024
 */
public class TwitterAccessTokenRequest {
  @FormParam("grant_type")
  public String grantType;
  @FormParam("code")
  public String code;
  @FormParam("redirect_uri")
  public String redirectUri;
  @FormParam("client_id")
  public String clientId;
  @FormParam("code_verifier")
  public String codeVerifier;
}
