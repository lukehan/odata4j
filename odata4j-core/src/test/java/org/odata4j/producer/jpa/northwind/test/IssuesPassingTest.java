package org.odata4j.producer.jpa.northwind.test;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OProperties;
import org.odata4j.core.ORelatedEntityLink;



public class IssuesPassingTest extends JPAProducerTestBase{

	protected static final String endpointUri = "http://localhost:8810/northwind/Northwind.svc/";

	@BeforeClass
	public static void setUpClass() throws Exception {
		
		setUpClass(20);		
	}
	
	@Test
	public void createCompositeKeyEntityUsingLinks() {
		final long now = System.currentTimeMillis();
		ODataConsumer consumer = ODataConsumer.create(endpointUri);
		
		OEntity product = consumer
			.createEntity("Products")
			.properties(OProperties.string("ProductName", "Product"+now))
			.execute();
		
		Assert.assertNotNull(product);	
		
		OEntity order = consumer
			.createEntity("Orders")		
			.execute();
		
		Assert.assertNotNull(order);
		
		Short quantity=1;
		OEntity orderDetails = consumer
			.createEntity("Order_Details")		
			.link("Order",order)
			.link("Product", product)
			.properties(OProperties.decimal("UnitPrice", 1.0))
			.properties(OProperties.short_("Quantity", quantity ))
			.properties(OProperties.decimal("Discount", 1.0))
			.execute();
		
		Assert.assertNotNull(orderDetails);
		Assert.assertEquals(
				OEntityKey.create("OrderID",order.getEntityKey().asSingleValue(),"ProductID",product.getEntityKey().asSingleValue()),
				orderDetails.getEntityKey());
	}
	
	@Test
	public void createCompositeKeyEntityUsingProperties() {
		final long now = System.currentTimeMillis();
		ODataConsumer consumer = ODataConsumer.create(endpointUri);
		
		OEntity product = consumer
			.createEntity("Products")
			.properties(OProperties.string("ProductName", "Product"+now))
			.execute();
		
		Assert.assertNotNull(product);	
		
		OEntity order = consumer
			.createEntity("Orders")		
			.execute();
		
		Assert.assertNotNull(order);
		
		Short quantity=1;
		OEntity orderDetails = consumer
			.createEntity("Order_Details")		
			.properties(OProperties.decimal("UnitPrice", 1.0))
			.properties(OProperties.short_("Quantity", quantity ))
			.properties(OProperties.decimal("Discount", 1.0))
			.properties(OProperties.simple("OrderID", order.getEntityKey().asSingleValue()))
			.properties(OProperties.simple("ProductID", product.getEntityKey().asSingleValue()))
			.execute();
		
		Assert.assertNotNull(orderDetails);
		Assert.assertEquals(
				OEntityKey.create("OrderID",order.getEntityKey().asSingleValue(),"ProductID",product.getEntityKey().asSingleValue()),
				orderDetails.getEntityKey());
	}
	
	
	@Test
	public void passEntityRefFromFilter() {
		final long now = System.currentTimeMillis();
		ODataConsumer consumer = ODataConsumer.create(endpointUri);
		
	
		OEntity customer = consumer
		.createEntity("Customers")
		.properties(OProperties.string("CustomerID", "ID" + now))
		.properties(OProperties.string("CompanyName", "Company" + now))
		.execute();
		Assert.assertNotNull(customer);
		
		String filterQuery="CompanyName eq 'Company"+now+"'";
		OEntity customerRet= consumer.getEntities("Customers").top(1).filter(filterQuery).execute().first();		
		
		Assert.assertNotNull(customerRet);
		
		OEntity order = consumer
		.createEntity("Orders")
		.properties(OProperties.string("ShipName", "Ship" + now))
		.link("Customer", customerRet)
		.execute();
		
		Assert.assertNotNull(order);
		
		ORelatedEntityLink link=order.getLink("Customer", ORelatedEntityLink.class);
		OEntity customerValid = consumer.getEntity(link).execute();

		Assert.assertNotNull(customerValid);		
		Assert.assertEquals("Company" + now, customerValid.getProperty("CompanyName").getValue());	
				
	}
	
	

}
