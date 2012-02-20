package org.odata4j.producer.jdbc.commandproducer;

import org.odata4j.core.OEntity;
import org.odata4j.producer.EntityResponse;

public interface CreateEntityCommandContext extends ProducerCommandContext<EntityResponse> {

  String getEntitySetName();

  OEntity getEntity();

}
