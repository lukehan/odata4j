package org.odata4j.producer.inmemory;

public class BeanBasedPropertyModel implements PropertyModel {

    private final BeanModel beanModel;
    
    public BeanBasedPropertyModel(Class<?> clazz) {
        beanModel = new BeanModel(clazz);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return beanModel.getPropertyNames();
    }

    @Override
    public Class<?> getPropertyType(String propertyName) {
        return beanModel.getPropertyType(propertyName);
    }

    @Override
    public Object getPropertyValue(Object target, String propertyName) {
        return beanModel.getPropertyValue(target, propertyName);
    }

	@Override
	public Iterable<String> getCollectionNames() {
		return beanModel.getCollectionNames();
	}

	@Override
	public Iterable<?> getCollectionValue(Object target, String collectionName) {
		return beanModel.getCollectionValue(target, collectionName);
	}

	@Override
	public Class<?> getCollectionElementType(String collectionName) {
		return beanModel.getCollectionElementType(collectionName);
	}

}
