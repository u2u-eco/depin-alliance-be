package xyz.telegram.depinalliance.common.models.request;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;

/**
 * @author holden on 25-Jul-2024
 */
public class PagingParameters {
  @QueryParam("page")
  public int page = 1;
  @QueryParam("size")
  public int size = 16;
  @DefaultValue("false")
  @QueryParam("sortAscending")
  public boolean sortAscending;
  @QueryParam("sortBy")
  public String sortBy;

  public void setSortByDefault(String sortBy) {
    if (StringUtils.isBlank(this.sortBy)) {
      this.sortBy = sortBy;
    }
  }

  public Page getPage() {
    if (size < 0 || size > 100) {
      this.size = 100;
    }
    int index = page >= 1 ? page - 1 : page;
    return new Page(index, size);
  }

  public Sort getSort() {
    return this.sortAscending ? Sort.ascending(this.sortBy) : Sort.descending(this.sortBy);
  }
}
