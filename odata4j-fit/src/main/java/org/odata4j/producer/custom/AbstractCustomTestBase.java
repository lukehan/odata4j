package org.odata4j.producer.custom;

import javax.ws.rs.core.MediaType;

import org.core4j.Func1;
import org.junit.After;
import org.junit.Before;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.examples.ConsumerSupport;
import org.odata4j.examples.ProducerSupport;
import org.odata4j.examples.WebResourceSupport;
import org.odata4j.format.FormatType;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.resources.DefaultODataProducerProvider;
import org.odata4j.producer.server.ODataServer;

/**
 *
 */
public abstract class AbstractCustomTestBase implements ProducerSupport, ConsumerSupport, WebResourceSupport {
  protected static final String endpointUri = "http://localhost:8810/CustomTest.svc/";
  protected static ODataServer server;
  protected static CustomProducer producer;

  @Before
  public void setUpClass() throws Exception {
    this.setUpClass(50, null);
  }

  public void setUpClass(int maxResults, Func1<ODataProducer, ODataProducer> producerModification) throws Exception {

    producer = new CustomProducer();

    ODataProducer p = producer;
    if (producerModification != null) {
      p = producerModification.apply(producer);
    }

    DefaultODataProducerProvider.setInstance(p);
    server = this.startODataServer(endpointUri);
  }

  protected ODataConsumer createConsumer(FormatType format) {
    return this.create(endpointUri, format);
  }

  public void dumpResourceJSON(String path) {
    dumpResource(path, FormatType.JSON);
  }

  public void dumpResource(String path, FormatType ft) {
    String uri = endpointUri + path;
    switch (ft) {
    case JSON:
      this.accept(uri, MediaType.APPLICATION_JSON_TYPE);
      break;
    case ATOM:
      this.accept(uri, MediaType.APPLICATION_ATOM_XML_TYPE);
      break;
    default:
      break;
    }
    System.out.println(this.getWebResource(uri, String.class));
  }

  @After
  public void tearDownClass() throws Exception {
    if (server != null) {
      server.stop();
      server = null;
    }
    producer = null;
  }
}
