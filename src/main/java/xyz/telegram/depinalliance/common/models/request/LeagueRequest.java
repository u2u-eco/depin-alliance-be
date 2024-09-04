package xyz.telegram.depinalliance.common.models.request;

import org.jboss.resteasy.reactive.RestForm;

import java.io.File;

/**
 * @author holden on 29-Aug-2024
 */
public class LeagueRequest {
  @RestForm("image")
  public File file;
  @RestForm("name")
  public String name;
}
