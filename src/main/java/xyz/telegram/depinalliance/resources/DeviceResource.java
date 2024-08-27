package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.request.AddItemRequest;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.entities.Item;
import xyz.telegram.depinalliance.services.DeviceService;

/**
 * @author holden on 26-Aug-2024
 */
@Path("devices")
public class DeviceResource extends BaseResource {

  @Inject
  DeviceService deviceService;

  @Path("item")
  @GET
  public ResponseData getDeviceUser(@QueryParam("type") @DefaultValue(value = "CPU") String type,
    PagingParameters pagingParameters) {
    return ResponseData.ok(Item.findByTypeAndPaging(pagingParameters, Enums.ItemType.valueOf(type.toUpperCase())));
  }

  @Path("add-item")
  @POST
  public ResponseData addItem(AddItemRequest request) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.addItem(getUser(), request));
    }
  }
}
