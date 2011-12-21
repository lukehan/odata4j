package org.odata4j.producer.jpa.northwind.test;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.Assert;

import org.core4j.Enumerable;
import org.core4j.Func1;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.Guid;
import org.odata4j.core.OComplexObject;
import org.odata4j.core.OObject;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.format.FormatType;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.jpa.JPAProducer;

/**
 * Some basic tests for functions.
 *
 */
public class FunctionTest extends JPAProducerTestBase {

  @BeforeClass
  public static void setUpClass() throws Exception {
    setUpClass(20, new Func1<ODataProducer, ODataProducer>() {
      @Override
      public ODataProducer apply(final ODataProducer jpa) {
        p = new NorthwindProducerWithFunctions((JPAProducer) jpa);
        return p;
      }
    });
  }

  @Test
  public void functionComplexObjectTest() {
    String inp = "FunctionComplexObjectTest";
    String uri = "TestFunction1?Param1='Testing'&Param2=false";
    NorthwindTestUtils.testJSONResult(endpointUri, uri, inp);
    // TODO: NorthwindTestUtils.testAtomResult(endpointUri, uri, inp);

    // test client call JSON
    ODataConsumer c = ODataJerseyConsumer.newBuilder(endpointUri).setFormatType(FormatType.JSON).build();

    // this is also testing that the server can handle all parameter types (simple types for now)
    Enumerable<OObject> e = c.callFunction("TestFunction1")
        .pBoolean("PBoolean", false)
        .pByte("PByte", (byte) 33)
        .pDateTime("PDateTime", new Date())
        .pDecimal("PDecimal", new BigDecimal("12345.6789"))
        .pDouble("PDouble", (double) 33.33)
        .pGuid("PGuid", Guid.randomGuid())
        .pInt16("PInt16", (short) 44)
        .pInt32("PInt32", 55)
        .pInt64("PInt64", 66)
        .pSingle("PSingle", (float) 12.34)
        .pTime("PTime", new Date())
        .execute();

    EdmComplexType ct = c.getMetadata().findEdmComplexType("NorthwindModel.Order_DetailsPK");

    int count = 0;
    for (OObject o : e) {
      count += 1;
      Assert.assertTrue(o instanceof OComplexObject);
      Assert.assertEquals(ct, o.getType());
      OComplexObject co = (OComplexObject) o;
      Assert.assertTrue(co.getProperties().size() == 2);
      Assert.assertTrue(co.getProperty("OrderID", Integer.class).getValue().equals(33));
      Assert.assertTrue(co.getProperty("ProductID", Integer.class).getValue().equals(44));
    }
    Assert.assertEquals(count, 1);

    // TODO: ODataConsumer cx = ODataConsumer.create(FormatType.ATOM, endpointUri);

  }

  @Test
  public void functionCollectionComplexObjectTest() {
    String inp = "FunctionCollectionComplexObjectTest";
    String uri = "TestFunction2?NResults=3";
    NorthwindTestUtils.testJSONResult(endpointUri, uri, inp);
    // TODO: NorthwindTestUtils.testAtomResult(endpointUri, uri, inp);

    // test client call JSON
    ODataConsumer c = ODataJerseyConsumer.newBuilder(endpointUri).setFormatType(FormatType.JSON).build();

    Enumerable<OObject> e = c.callFunction("TestFunction2")
        .pInt16("NResults", (short) 3)
        .execute();

    EdmComplexType ct = c.getMetadata().findEdmComplexType("NorthwindModel.Order_DetailsPK");

    // results is a collection of complex objects
    int count = 0;
    int orderid = 1;
    int productid = 2;
    for (OObject o : e) {

      count += 1;
      Assert.assertTrue(o instanceof OComplexObject);
      Assert.assertEquals(ct, o.getType());
      OComplexObject co = (OComplexObject) o;
      Assert.assertTrue(co.getProperties().size() == 2);
      Assert.assertTrue(co.getProperty("OrderID", Integer.class).getValue().equals(orderid));
      Assert.assertTrue(co.getProperty("ProductID", Integer.class).getValue().equals(productid));
      orderid += 2;
      productid += 2;
    }
    Assert.assertEquals(count, 3);
  }

  @Test
  public void functionWithNoReturnTypeTest() {
    // test client call JSON
    ODataConsumer c = ODataJerseyConsumer.newBuilder(endpointUri).setFormatType(FormatType.JSON).build();

    Enumerable<OObject> results = c.callFunction("TestFunction3")
        .pString("PString", "hello world")
        .execute();
    Assert.assertEquals(0, results.count());
  }

  private static NorthwindProducerWithFunctions p;
}
