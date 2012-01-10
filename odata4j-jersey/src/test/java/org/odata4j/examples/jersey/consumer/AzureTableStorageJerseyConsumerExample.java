package org.odata4j.examples.jersey.consumer;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.examples.consumers.AbstractAzureTableStorageConsumerExample;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

public class AzureTableStorageJerseyConsumerExample extends AbstractAzureTableStorageConsumerExample {

  public static void main(String... args) {
    AzureTableStorageJerseyConsumerExample example = new AzureTableStorageJerseyConsumerExample();
    example.run(args);
  }

  @Override
  public ODataConsumer create(String endpointUri) {
    return ODataJerseyConsumer.newBuilder(endpointUri).setClientBehaviors(OClientBehaviors.azureTables(this.getLoginName(), this.getLoginPassword())).build();
  }

}
