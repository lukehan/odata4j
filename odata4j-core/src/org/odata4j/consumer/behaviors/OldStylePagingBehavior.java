package org.odata4j.consumer.behaviors;

import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.core.OClientBehavior;
import org.odata4j.format.Entry;

import com.sun.jersey.api.client.config.ClientConfig;

public class OldStylePagingBehavior implements OClientBehavior {

    private final int startPage;
    private final int itemsPerPage;

    public OldStylePagingBehavior() {
        this(10);
    }

    public OldStylePagingBehavior(int itemsPerPage) {
        this(itemsPerPage, 1);
    }
    
    @Override
    public void modify(ClientConfig clientConfig) { }

    public OldStylePagingBehavior(int itemsPerPage, int startPage) {
        this.itemsPerPage = itemsPerPage;
        this.startPage = startPage;
    }

    @Override
    public <E extends Entry> ODataClientRequest transform(ODataClientRequest request) {
        if (request.getQueryParams().containsKey("$page"))
            return request;
        return request.queryParam("$page", Integer.toString(startPage)).queryParam("$itemsPerPage", Integer.toString(itemsPerPage));
    }

}