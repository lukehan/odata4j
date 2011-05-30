package org.odata4j.format;

import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.internal.FeedCustomizationMapping;

public class Settings {
  public final ODataVersion version;
  public final EdmDataServices metadata;
  public final String entitySetName;
  public final OEntityKey entityKey;
  public final FeedCustomizationMapping fcMapping;
  public final boolean isResponse;

  public Settings(ODataVersion version, EdmDataServices metadata,
      String entitySetName, OEntityKey entityKey, FeedCustomizationMapping fcMapping) {
    this(version, metadata, entitySetName, entityKey, fcMapping, true);
  }

  public Settings(ODataVersion version, EdmDataServices metadata,
      String entitySetName, OEntityKey entityKey, FeedCustomizationMapping fcMapping,
      boolean isResponse) {
    this.version = version;
    this.metadata = metadata;
    this.entitySetName = entitySetName;
    this.entityKey = entityKey;
    this.fcMapping = fcMapping;
    this.isResponse = isResponse;
  }
}
