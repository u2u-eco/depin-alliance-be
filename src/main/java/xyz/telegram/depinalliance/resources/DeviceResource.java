package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import xyz.telegram.depinalliance.common.models.request.*;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.entities.UserDevice;
import xyz.telegram.depinalliance.entities.UserItem;
import xyz.telegram.depinalliance.services.DeviceService;
import xyz.telegram.depinalliance.services.RedisService;

/**
 * @author holden on 26-Aug-2024
 */
@Path("devices")
public class DeviceResource extends BaseResource {

  @Inject
  DeviceService deviceService;
  @Inject
  RedisService redisService;

  @Path("item")
  @GET
  public ResponseData getDeviceItem(@QueryParam("type") String type, PagingParameters pagingParameters) {
    pagingParameters.setSortByDefault("price");
    return ResponseData.ok(redisService.findListItemInShop(pagingParameters, type));
  }

  @Path("user-device")
  @GET
  public ResponseData getUserDevice() {
    return ResponseData.ok(UserDevice.findByUser(getTelegramId()));
  }

  @Path("add-device")
  @GET
  public ResponseData addDevice() {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.addDevice(getUser()));
    }
  }

  @Path("user-item")
  @GET
  public ResponseData getUserItem(@QueryParam("type") String type, PagingParameters pagingParameters) {
    pagingParameters.setSortByDefault("price");
    return ResponseData.ok(UserItem.findByUserIdAndType(pagingParameters, getTelegramId(), type));
  }

  @Path("user-device-item")
  @GET
  public ResponseData getUserDeviceItem(@QueryParam("index") Long index, @QueryParam("type") String type,
    PagingParameters pagingParameters) {
    pagingParameters.setSortByDefault("price");
    return ResponseData.ok(UserItem.findByUserIdAndIndexAndType(pagingParameters, getTelegramId(), index, type));
  }

  @Path("user-device-item-contribute")
  @GET
  public ResponseData getUserDeviceItemContribute(PagingParameters pagingParameters) {
    pagingParameters.setSortByDefault("price");
    pagingParameters.sortAscending = true;
    return ResponseData.ok(UserItem.findItemNotHasDeviceAndNotSpecial(pagingParameters, getTelegramId()));
  }

  @Path("buy-item")
  @POST
  public ResponseData buyItem(BuyItemRequest request) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.buyItem(getUser(), request));
    }
  }

  @Path("add-item/{index}/{itemId}")
  @GET
  public ResponseData addItem(@PathParam("index") int index, @PathParam("itemId") long itemId) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.addItem(getUser(), index, itemId));
    }
  }

  @Path("remove-item/{itemId}")
  @GET
  public ResponseData removeItem(@PathParam("itemId") long itemId) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.removeItem(getUser(), itemId));
    }
  }

  @Path("swap-item/{itemRemoveId}/{itemAddId}")
  @GET
  public ResponseData swapItem(@PathParam("itemRemoveId") long itemRemoveId, @PathParam("itemAddId") long itemAddId)
    throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.swapItem(getUser(), itemRemoveId, itemAddId));
    }
  }

  @Path("sell-item/{itemId}")
  @GET
  public ResponseData sellItem(@PathParam("itemId") long itemId) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.sellItem(getUser(), itemId));
    }
  }

  @Path("sell-item")
  @POST
  public ResponseData sellItemMulti(SellItemRequest request) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.sellItem(getUser(), request));
    }
  }

  @Path("change-name")
  @POST
  public ResponseData changeName(ChangeNameDeviceRequest request) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.changeNameDevice(getUser(), request));
    }
  }

  @Path("estimate-use-key")
  @POST
  public ResponseData estimateUseKey(BoxUseKeyRequest request) {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.estimateUseKeyBox(getUser(), request));
    }
  }

  @Path("use-key")
  @POST
  public ResponseData useKeyBox(BoxUseKeyRequest request) {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(deviceService.useKeyBox(getUser(), request));
    }
  }
}
