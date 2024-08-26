package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.models.request.PagingParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author holden on 25-Jul-2024
 */
public class ResponsePage<T> {

  public final List<T> content = new ArrayList();
  public final PagingParameters pageable;
  public long total;

  public ResponsePage(List<T> content, PagingParameters pageable, long total) {
    this.content.addAll(content);
    this.pageable = pageable;
    this.total = total;
  }

  public int getTotalPages() {
    return this.getSize() == 0 ? 1 : (int) Math.ceil((double) this.total / (double) this.getSize());
  }

  public int getSize() {
    return this.pageable.size;
  }
}