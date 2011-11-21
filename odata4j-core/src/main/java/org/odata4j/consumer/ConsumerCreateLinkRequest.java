package org.odata4j.consumer;

import org.core4j.Enumerable;
import org.odata4j.core.OEntityId;
import org.odata4j.edm.EdmDataServices;

class ConsumerCreateLinkRequest extends ConsumerEntityRequestBase<Void> {

  private final String targetNavProp;
  private final OEntityId targetEntity;

  ConsumerCreateLinkRequest(ODataClient client, String serviceRootUri,
      EdmDataServices metadata, OEntityId sourceEntity, String targetNavProp, OEntityId targetEntity) {
    super(client, serviceRootUri, metadata, sourceEntity.getEntitySetName(), sourceEntity.getEntityKey());
    this.targetNavProp = targetNavProp;
    this.targetEntity = targetEntity;
  }

  @Override
  public Void execute() {
    String path = Enumerable.create(getSegments()).join("/");
    path = ConsumerQueryLinksRequest.linksPath(targetNavProp, null).apply(path);

    ODataClientRequest request = ODataClientRequest.post(getServiceRootUri() + path, toSingleLink(targetEntity));
    getClient().createLink(request);
    return null;
  }

}
