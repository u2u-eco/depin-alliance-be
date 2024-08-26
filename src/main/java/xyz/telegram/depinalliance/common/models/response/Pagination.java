package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author holden on 25-Jul-2024
 */
public class Pagination {
  @JsonProperty(value = "page")
  public long currentPage;
  @JsonProperty(value = "size")
  public long pageSize;
  @JsonProperty(value = "totalPage")
  public long totalPage;
  @JsonProperty(value = "totalRecord")
  public long totalRecord;

  public Pagination() {
  }

  public Pagination(long currentPage, long pageSize, long totalPage, long totalRecord) {
    this.currentPage = currentPage;
    this.pageSize = pageSize;
    this.totalPage = totalPage;
    this.totalRecord = totalRecord;
  }
}
