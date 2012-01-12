package org.odata4j.jersey.producer.jpa.northwind.test;

import org.odata4j.jersey.examples.producer.JerseyProducerUtil;
import org.odata4j.producer.jpa.northwind.test.AbstractQueryOptionAtomTest50;
import org.odata4j.producer.jpa.northwind.test.NorthwindTestUtils;
import org.odata4j.producer.server.ODataServer;

public class QueryOptionAtomTest50 extends AbstractQueryOptionAtomTest50 {
  @Override
  public void hostODataServer(String baseUri) {
    JerseyProducerUtil.hostODataServer(baseUri);
  }

  @Override
  public ODataServer startODataServer(String baseUri) {
    return JerseyProducerUtil.startODataServer(baseUri);
  }

  @Override
  public NorthwindTestUtils getUtils() {
    return new JerseyNorthwindTestUtils();
  }
}
