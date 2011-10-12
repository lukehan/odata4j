package org.odata4j.edm;

import org.core4j.Func;
import org.odata4j.core.ImmutableList;
import org.odata4j.core.Named;

/**
 * The EntitySet element in conceptual schema definition language is a logical container for instances of an entity type and instances of any type that is derived from that entity type.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/bb386874.aspx">[msdn] Entity Sets (EDM)</a>
 */
public class EdmEntitySet extends EdmItem implements Named {

  private final String name;
  private final Func<EdmEntityType> type;

  private EdmEntitySet(String name, Func<EdmEntityType> type,
      EdmDocumentation doc, ImmutableList<EdmAnnotation<?>> annots) {
    super(doc, annots);
    this.name = name;
    this.type = type;
  }

  /**
   * The name of the entity set.
   */
  public String getName() {
    return name;
  }

  /**
   * The entity type for which the entity set contains instances.
   */
  public EdmEntityType getType() {
    return type == null ? null : type.apply();
  }

  @Override
  public String toString() {
    return String.format("EdmEntitySet[%s,%s]", name, type);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(EdmEntitySet entitySet, BuilderContext context) {
    return context.newBuilder(entitySet, new Builder());
  }

  public static class Builder extends EdmItem.Builder<EdmEntitySet, Builder> implements Named {

    private String name;
    private String entityTypeName;
    private EdmEntityType.Builder entityType;

    @Override
    Builder newBuilder(EdmEntitySet entitySet, BuilderContext context) {
      this.name = entitySet.name;
      EdmEntityType et = entitySet.type.apply();
      this.entityType = EdmEntityType.newBuilder(et, context);
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setEntityType(EdmEntityType.Builder entityType) {
      this.entityType = entityType;
      return this;
    }

    public EdmEntitySet build() {
      return new EdmEntitySet(name, entityType == null ? null : entityType.builtFunc(), getDocumentation(), ImmutableList.copyOf(getAnnotations()));
    }

    @Override
    public String getName() {
      return name;
    }
    public String getEntityTypeName() {
      return entityTypeName;
    }

    public Builder setEntityTypeName(String entityTypeName) {
      this.entityTypeName = entityTypeName;
      return this;
    }

  }

}
