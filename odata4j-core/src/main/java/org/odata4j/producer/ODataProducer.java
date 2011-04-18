package org.odata4j.producer;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;

/** 
 * <code>ODataProducer</code> is the server-side interface to be implemented by an OData data-source.  
 * <p>The interface consists of two portions: the first portion consists
 * of the methods clients use to retrieve/query entities and to introspect service metadata; 
 * the second half consists of methods to create/modify/delete entities.  Not all
 * OData producers will support this second half of the interface.</p>
 */
public interface ODataProducer {

	/** 
	 * Obtains the service metadata for this producer.
	 * 
	 * @return a fully-constructed metadata object
	 */
    public abstract EdmDataServices getMetadata();

    /** 
     * Gets all the entities for a given set matching the query information.
     * 
     * @param entitySetName  the entity-set name for entities to return
     * @param queryInfo  the additional constraints to apply to the entities
     * @return a packaged collection of entities to pass back to the client
     */
    public abstract EntitiesResponse getEntities(String entitySetName, QueryInfo queryInfo);

    /** 
     * Obtains a single entity based on its type and key.
     * 
     * @param entitySetName  the entity-set name for the entity to return
     * @param entityKey  the unique entity-key within the set
     * @return the matching entity
     */
    public abstract EntityResponse getEntity(String entitySetName, OEntityKey entityKey);

    /** 
     * Given a specific entity, follow one of its navigation properties, applying constraints as appropriate.
     * Return the resulting entity, entities, or property value.
     * 
     * @param entitySetName  the entity-set of the entity to start with
     * @param entityKey  the unique entity-key of the entity to start with
     * @param navProp  the navigation property to follow
     * @param queryInfo  additional constraints to apply to the result
     * @return the resulting entity, entities, or property value
     */
    public abstract BaseResponse getNavProperty(
            String entitySetName,
            OEntityKey entityKey,
            String navProp,
            QueryInfo queryInfo);

    /**
     * Releases any resources managed by this producer.
     */
    public abstract void close();

    
    /**
     * Creates a new OData entity.
     * 
     * @param entitySetName  the entity-set name
     * @param entity  the request entity sent from the client
     * @return the newly-created entity, fully populated with the key and default properties
     * @see <a href="http://www.odata.org/developers/protocols/operations#CreatingnewEntries">[odata.org] Creating new Entries</a>
     */
    public abstract EntityResponse createEntity(String entitySetName, OEntity entity);

    /**
     * Creates a new OData entity as a reference of an existing entity, implicitly linked to the existing entity by a navigation property.
     * 
     * @param entitySetName  the entity-set name of the existing entity
     * @param entityKey  the entity-key of the existing entity
     * @param navProp  the navigation property off of the existing entity
     * @param entity  the request entity sent from the client
     * @return the newly-created entity, fully populated with the key and default properties, and linked to the existing entity
     * @see <a href="http://www.odata.org/developers/protocols/operations#CreatingnewEntries">[odata.org] Creating new Entries</a>
     */
    public abstract EntityResponse createEntity(String entitySetName, OEntityKey entityKey, String navProp, OEntity entity);

    /**
     * Deletes an existing entity.
     * 
     * @param entitySetName  the entity-set name of the entity
     * @param entityKey  the entity-key of the entity
     * @see <a href="http://www.odata.org/developers/protocols/operations#DeletingEntries">[odata.org] Deleting Entries</a>
     */
    public abstract void deleteEntity(String entitySetName, OEntityKey entityKey);

    /**
     * Modifies an existing entity using merge semantics.
     * 
     * @param entitySetName  the entity-set name
     * @param entity  the entity modifications sent from the client
     * @see <a href="http://www.odata.org/developers/protocols/operations#UpdatingEntries">[odata.org] Updating Entries</a>
     */
    public abstract void mergeEntity(String entitySetName, OEntity entity);

    /**
     * Modifies an existing entity using update semantics.
     * 
     * @param entitySetName  the entity-set name
     * @param entity  the entity modifications sent from the client
     * @see <a href="http://www.odata.org/developers/protocols/operations#UpdatingEntries">[odata.org] Updating Entries</a>
     */
    public abstract void updateEntity(String entitySetName, OEntity entity);
}
