package org.odata4j.examples.consumers;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.examples.AbstractExample;
import org.odata4j.fit.support.ConsumerSupport;
import org.odata4j.fit.support.RunSupport;

public abstract class AbstractJsonGrabbingConsumerExample extends AbstractExample implements ConsumerSupport, RunSupport {

  @Override
  public void run(String[] args) {

    String serviceUri = "http://services.odata.org/Northwind/Northwind.svc";
    ODataConsumer c = this.create(serviceUri, null, null);

    c.getEntity("Customers", "VICTE").execute();
  }

}
