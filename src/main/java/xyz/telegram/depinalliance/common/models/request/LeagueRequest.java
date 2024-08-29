package xyz.telegram.depinalliance.common.models.request;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

/**
 * @author holden on 29-Aug-2024
 */
public class LeagueRequest {
  @RestForm("image")
  public FileUpload image;
  @RestForm("name")
  public String name;
}
