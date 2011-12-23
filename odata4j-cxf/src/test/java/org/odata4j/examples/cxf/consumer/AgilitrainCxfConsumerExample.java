package org.odata4j.examples.cxf.consumer;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.cxf.consumer.ODataCxfConsumer;
import org.odata4j.examples.consumers.AbstractAgilitrainConsumerExample;

public class AgilitrainCxfConsumerExample extends AbstractAgilitrainConsumerExample {

  public static void main(String... args) {
    AgilitrainCxfConsumerExample example = new AgilitrainCxfConsumerExample();
    example.run(args);
  }

  @Override
  public ODataConsumer create(String endpointUri) {
    return ODataCxfConsumer.create(endpointUri);
  }

}
