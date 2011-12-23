package org.odata4j.jersey.consumer.behaviors;

import org.odata4j.jersey.consumer.ODataJerseyClientRequest;

public class OldStylePagingBehavior extends BaseClientBehavior {

  private final int startPage;
  private final int itemsPerPage;

  public OldStylePagingBehavior() {
    this(10);
  }

  public OldStylePagingBehavior(int itemsPerPage) {
    this(itemsPerPage, 1);
  }

  public OldStylePagingBehavior(int itemsPerPage, int startPage) {
    this.itemsPerPage = itemsPerPage;
    this.startPage = startPage;
  }

  @Override
  public ODataJerseyClientRequest transform(ODataJerseyClientRequest request) {
    if (request.getQueryParams().containsKey("$page"))
      return request;
    return request.queryParam("$page", Integer.toString(startPage)).queryParam("$itemsPerPage", Integer.toString(itemsPerPage));
  }

}