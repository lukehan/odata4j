package org.odata4j.test.expression;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.core4j.Funcs;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.examples.producer.ProducerUtil;
import org.odata4j.producer.inmemory.InMemoryProducer;
import org.odata4j.producer.resources.DefaultODataProducerProvider;
import org.odata4j.producer.server.ODataServer;

public class PojoTest {

  @Test
  public void testPojo() {

    String uri = "http://localhost:18889/";

    InMemoryProducer producer = new InMemoryProducer("PojoTest");
    DefaultODataProducerProvider.setInstance(producer);

    ODataServer server = ProducerUtil.startODataServer(uri);

    try {
      ODataConsumer c = ODataConsumer.create(uri);
      Assert.assertEquals(0, c.getEntitySets().count());

      List<Pojo1> pojo1s = new ArrayList<Pojo1>();
      producer.register(Pojo1.class, Integer.TYPE, "Pojo1", Funcs.constant((Iterable<Pojo1>) pojo1s), "Id");

      Assert.assertEquals(1, c.getEntitySets().count());

      pojo1s.add(new Pojo1(1, "John"));

      Assert.assertEquals(1, c.getEntities(Pojo1.class, "Pojo1").execute().first().getId());
      Assert.assertEquals("John", c.getEntities(Pojo1.class, "Pojo1").execute().first().getName());

      Assert.assertEquals(1, c.getEntity(Pojo1.class, "Pojo1", 1).execute().getId());
      Assert.assertEquals("John", c.getEntity(Pojo1.class, "Pojo1", 1).execute().getName());

      Assert.assertEquals(1, c.getEntities(Pojo1.class, "Pojo1").filter("Name eq 'John'").execute().first().getId());
      Assert.assertEquals("John", c.getEntities(Pojo1.class, "Pojo1").filter("Name eq 'John'").execute().first().getName());

    } finally {
      server.stop();
    }

  }

  public static class Pojo1 {
    private int id;
    private String name;

    protected Pojo1() {}

    public Pojo1(int id, String name) {
      this.id = id;
      this.name = name;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public void setId(int id) {
      this.id = id;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
