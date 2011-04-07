package org.odata4j.core;

import java.util.List;

import org.odata4j.edm.EdmEntitySet;

public interface OEntity {
	
	//	TODO consider a new property etag, so the etag
	//	does not get lost while processing the OEntity

    public abstract EdmEntitySet getEntitySet();
    public abstract OEntityKey getEntityKey(); 
    
    public abstract List<OProperty<?>> getProperties();
    public abstract OProperty<?> getProperty(String propName);
    public abstract <T> OProperty<T> getProperty(String propName, Class<T> propClass);
    
    public abstract List<OLink> getLinks();
    public abstract <T extends OLink> T getLink(String title, Class<T> linkClass);

    
}
