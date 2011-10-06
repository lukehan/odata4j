package org.odata4j.core;

import org.odata4j.edm.EdmComplexType;

/**
 * An instance of an {@link EdmComplexType}.
 */
public interface OComplexObject extends OStructuralObject {
  public interface Builder {
    Builder add(OProperty<?> prop);
    EdmComplexType getType();
    OComplexObject build();
  }
}
