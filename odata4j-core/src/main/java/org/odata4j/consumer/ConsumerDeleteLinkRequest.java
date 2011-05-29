package org.odata4j.consumer;

import org.core4j.Enumerable;
import org.odata4j.core.OEntityId;
import org.odata4j.edm.EdmDataServices;

class ConsumerDeleteLinkRequest extends ConsumerEntityRequestBase<Void> {

  private final String targetNavProp;
  private final Object[] targetKeyValues;
  
  public ConsumerDeleteLinkRequest(ODataClient client, String serviceRootUri,
      EdmDataServices metadata, OEntityId sourceEntity, String targetNavProp, Object... targetKeyValues) {
    super(client, serviceRootUri, metadata, sourceEntity.getEntitySet().name, sourceEntity.getEntityKey());
    this.targetNavProp = targetNavProp;
    this.targetKeyValues = targetKeyValues;
  }

  @Override
  public Void execute() {
    String path = Enumerable.create(getSegments()).join("/");
    path = ConsumerQueryLinksRequest.linksPath(targetNavProp, targetKeyValues).apply(path);
    ODataClientRequest request = ODataClientRequest.delete(getServiceRootUri() + path);
    getClient().deleteLink(request);
    return null;
  }

}
