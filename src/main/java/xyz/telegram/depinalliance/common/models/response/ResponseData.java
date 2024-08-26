package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author holden on 25-Jul-2024
 */
public class ResponseData<T> {
  public T data;
  public String status;
  public String message;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String error;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Pagination pagination;

  public ResponseData(T data, String status, String message, String error, Pagination pagination) {
    this.data = data;
    this.status = status;
    this.message = message;
    this.error = error;
    this.pagination = pagination;
  }

  public ResponseData(T data, String status, Pagination pagination) {
    this.data = data;
    this.status = status;
    this.pagination = pagination;
  }

  public ResponseData(T data, String status, String message, String error) {
    this.data = data;
    this.status = status;
    this.message = message;
    this.error = error;
  }

  public static ResponseData ok(Object data) {
    return ok(data, "");
  }

  public static ResponseData ok() {
    return ok("", "");
  }

  public static ResponseData ok(Object data, String message, long totalPages, long totalRecords, int page, int size) {
    return new ResponseData(data, Status.SUCCESS.getStatus(), message, null,
      new Pagination(page, size, totalPages, totalRecords));
  }

  public static ResponseData ok(Object data, long totalPages, long totalRecords, int page, int size) {
    return new ResponseData(data, Status.SUCCESS.getStatus(), "", null,
      new Pagination(page, size, totalPages, totalRecords));

  }

  public static ResponseData ok(Object data, Pagination pagination) {
    return new ResponseData(data, Status.SUCCESS.getStatus(), pagination);
  }

  public static ResponseData ok(ResponsePage data) {
    return ok(data.content, new Pagination(data.pageable.page, data.pageable.size, data.getTotalPages(), data.total));
  }

  public static ResponseData ok(Object data, String message) {
    return new ResponseData(data, Status.SUCCESS.getStatus(), message, null);
  }

  public static ResponseData error(String message) {
    return error(message, 0);
  }

  public static ResponseData error(String message, long error) {
    return new ResponseData(null, Status.ERROR.getStatus(), message, error > 0 ? String.valueOf(error) : null);
  }

  public enum Status {
    SUCCESS("success"), ERROR("error");

    private final String status;

    Status(String status) {
      this.status = status;
    }

    public String getStatus() {
      return status;
    }

  }
}