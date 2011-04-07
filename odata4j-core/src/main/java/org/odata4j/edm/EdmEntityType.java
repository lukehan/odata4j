package org.odata4j.edm;

import java.util.ArrayList;
import java.util.List;

import org.core4j.Enumerable;
import org.core4j.Predicate1;

public class EdmEntityType {

    public final String namespace;
    public final String alias;
    public final String name;
    public final Boolean hasStream;
    public final List<String> keys;
    public final List<EdmProperty> properties;
    public final List<EdmNavigationProperty> navigationProperties;

    public EdmEntityType(String namespace, String alias, String name, Boolean hasStream, List<String> keys, List<EdmProperty> properties, List<EdmNavigationProperty> navigationProperties) {
        this.namespace = namespace;
        this.alias = alias;
        this.name = name;
        this.hasStream = hasStream;
        
        this.keys = keys;
        this.properties = properties == null ? new ArrayList<EdmProperty>() : properties;
        this.navigationProperties = navigationProperties == null ? new ArrayList<EdmNavigationProperty>() : navigationProperties;
    }

    public String getFQNamespaceName() {
        return namespace + "." + name;
    }
    
    public String getFQAliasName() {
        return alias==null?null:(alias + "." + name);
    }
    
    @Override
    public String toString() {
    	return String.format("EdmEntityType[%s.%s,alias=%s]",namespace,name,alias);
    }

    public EdmNavigationProperty getNavigationProperty(final String name) {
    	return Enumerable.create(navigationProperties)
    		.firstOrNull(new Predicate1<EdmNavigationProperty>() {
    			@Override
    			public boolean apply(EdmNavigationProperty input) {
    				return input.name.equals(name);
    			}
    		});
    }
    
    public EdmProperty getProperty(final String name) {
    	return Enumerable.create(properties)
    		.firstOrNull(new Predicate1<EdmProperty>() {
    			@Override
    			public boolean apply(EdmProperty input) {
    				return input.name.equals(name);
    			}
    		});
    }

}
