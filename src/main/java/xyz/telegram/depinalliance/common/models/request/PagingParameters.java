package xyz.telegram.depinalliance.common.models.request;

import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import io.quarkus.panache.common.Page;
import jakarta.ws.rs.QueryParam;

/**
 * @author holden on 25-Jul-2024
 */
public class PagingParameters {
  @QueryParam("page")
  public int page = 1;
  @QueryParam("size")
  public int size = 16;

  public Page getPage() throws BusinessException {
    if (size < 0 || size > 100) {
      throw new BusinessException("Size invalid");
    }
    int index = page >= 1 ? page - 1 : page;
    return new Page(index, size);
  }
}
