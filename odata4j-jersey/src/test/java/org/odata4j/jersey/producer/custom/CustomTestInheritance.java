package org.odata4j.jersey.producer.custom;

import javax.ws.rs.core.MediaType;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.format.FormatType;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;
import org.odata4j.jersey.examples.producer.JerseyProducerUtil;
import org.odata4j.producer.custom.AbstractCustomTestBase;
import org.odata4j.producer.server.ODataServer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class CustomTestInheritance extends AbstractCustomTestBase {

  @Override
  public void hostODataServer(String baseUri) {
    JerseyProducerUtil.hostODataServer(baseUri);
  }

  @Override
  public ODataServer startODataServer(String baseUri) {
    return JerseyProducerUtil.startODataServer(baseUri);
  }

  @Override
  public ODataConsumer create(String endpointUri, FormatType formatType) {
    return ODataJerseyConsumer.newBuilder(endpointUri).setFormatType(formatType).build();
  }

  @Override
  public <T> String getWebResource(String uri, Class<T> c) {
    WebResource webResource = new Client().resource(uri);

    String data = webResource.get(
        String.class);
    return data;
  }

  @Override
  public void accept(String uri, MediaType mediaType) {
    WebResource webResource = new Client().resource(uri);
    webResource.accept(mediaType);
  }
}
