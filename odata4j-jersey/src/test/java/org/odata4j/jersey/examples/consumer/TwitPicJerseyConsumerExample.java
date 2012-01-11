package org.odata4j.jersey.examples.consumer;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.examples.consumers.AbstractTwitPicConsumerExample;
import org.odata4j.format.FormatType;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

public class TwitPicJerseyConsumerExample extends AbstractTwitPicConsumerExample {

  public static void main(String[] args) {
    TwitPicJerseyConsumerExample example = new TwitPicJerseyConsumerExample();
    example.run(args);
  }

  @Override
  public ODataConsumer create(String endpointUri, FormatType formatType) {
    return ODataJerseyConsumer.create(endpointUri);
  }

}
