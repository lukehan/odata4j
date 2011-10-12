package org.odata4j.edm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.core4j.Enumerable;
import org.core4j.Func;
import org.odata4j.core.ImmutableList;
import org.odata4j.core.Named;
import org.odata4j.core.OPredicates;

public class EdmEntityType extends EdmStructuralType {

  private final String alias;
  private final Boolean hasStream;
  private final List<String> keys;
  private final List<EdmNavigationProperty> navigationProperties;

  private EdmEntityType(String namespace, String alias, String name, Boolean hasStream,
      ImmutableList<String> keys, EdmEntityType baseType, List<EdmProperty.Builder> properties,
      ImmutableList<EdmNavigationProperty> navigationProperties,
      EdmDocumentation doc, ImmutableList<EdmAnnotation<?>> annotations, Boolean isAbstract) {
    super(baseType, namespace, name, properties, doc, annotations, isAbstract);
    this.alias = alias;
    this.hasStream = hasStream;

    this.keys = keys == null || keys.isEmpty() ? null : keys;

    if (baseType == null && this.keys == null)
      throw new IllegalArgumentException("Root types must have keys");
    if (baseType != null && this.keys != null)
      throw new IllegalArgumentException("Keys on root types only");

    this.navigationProperties = navigationProperties;
  }

  public String getAlias() {
    return alias;
  }

  public Boolean getHasStream() {
    return hasStream;
  }

  public String getFQAliasName() {
    return alias == null ? null : (alias + "." + getName());
  }

  @Override
  public String toString() {
    return String.format("EdmEntityType[%s.%s,alias=%s]", getNamespace(), getName(), alias);
  }

  /**
   * Finds a navigation property by name, searching up the type hierarchy if necessary.
   */
  public EdmNavigationProperty findNavigationProperty(String name) {
    return getNavigationProperties().firstOrNull(OPredicates.nameEquals(EdmNavigationProperty.class, name));
  }

  /**
   * Gets the navigation properties defined for this entity type <i>not including</i> inherited properties.
   */
  public Enumerable<EdmNavigationProperty> getDeclaredNavigationProperties() {
    return Enumerable.create(navigationProperties);
  }

  /**
   * Finds a navigation property by name on this entity type <i>not including</i> inherited properties.
   */
  public EdmNavigationProperty findDeclaredNavigationProperty(String name) {
    return getDeclaredNavigationProperties().firstOrNull(OPredicates.nameEquals(EdmNavigationProperty.class, name));
  }

  /**
   * Gets the navigation properties defined for this entity type <i>including</i> inherited properties.
   */
  public Enumerable<EdmNavigationProperty> getNavigationProperties() {
    return isRootType()
        ? getDeclaredNavigationProperties()
        : getBaseType().getNavigationProperties().union(getDeclaredNavigationProperties());
  }

  /**
   * Gets the keys for this EdmEntityType.  Keys are defined only in a root types.
   */
  public List<String> getKeys() {
    return isRootType() ? keys : getBaseType().getKeys();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(EdmEntityType entityType, BuilderContext context) {
    return context.newBuilder(entityType, new Builder());
  }

  public static class Builder extends EdmStructuralType.Builder<EdmEntityType, Builder> implements Named {

    private String alias;
    private Boolean hasStream;
    private final List<String> keys =  new ArrayList<String>();
    private final List<EdmNavigationProperty.Builder> navigationProperties = new ArrayList<EdmNavigationProperty.Builder>();
    private EdmEntityType.Builder baseType;
    private String baseTypeNameFQ;

    @Override
    Builder newBuilder(EdmEntityType entityType, BuilderContext context) {
      fillBuilder(entityType, context);
      context.register(entityType, this);
      this.alias = entityType.alias;
      this.hasStream = entityType.hasStream;
      this.keys.addAll(entityType.keys);
      for(EdmNavigationProperty navigationProperty : entityType.navigationProperties)
        this.navigationProperties.add(EdmNavigationProperty.newBuilder(navigationProperty, context));
      return this;
    }

    private EdmEntityType e;

    @Override
    public EdmEntityType build() {
      if (e == null) {
        EdmEntityType baseType = this.baseType != null ? this.baseType.build() : null;
        List<EdmNavigationProperty> navigationProperties = new ArrayList<EdmNavigationProperty>();
        for(EdmNavigationProperty.Builder navigationProperty : this.navigationProperties)
          navigationProperties.add(navigationProperty.build());
        e = new EdmEntityType(namespace, alias, name, hasStream, ImmutableList.copyOf(keys), baseType,
            properties, ImmutableList.copyOf(navigationProperties), getDocumentation(), ImmutableList.copyOf(getAnnotations()), isAbstract);
      }
      return e;
    }

    public Builder addNavigationProperties(EdmNavigationProperty.Builder... navigationProperties) {
      return addNavigationProperties(Arrays.asList(navigationProperties));
    }

    public Builder addNavigationProperties(List<EdmNavigationProperty.Builder> navProperties) {
      this.navigationProperties.addAll(navProperties);
      return this;
    }

    public Builder addKeys(List<String> keys) {
      this.keys.addAll(keys);
      return this;
    }

    public Builder addKeys(String... keys) {
      return addKeys(Arrays.asList(keys));
    }

    @Override
    public String getName() {
      return name;
    }

    public Builder setBaseType(EdmEntityType.Builder baseType) {
      this.baseType = baseType;
      return this;
    }

    public Builder setBaseType(String baseTypeName) {
      this.baseTypeNameFQ = baseTypeName;
      return this;
    }

    public Builder setAlias(String alias) {
      this.alias = alias;
      return this;
    }

    public Builder setHasStream(Boolean hasStream) {
      this.hasStream = hasStream;
      return this;
    }

    public String getFQAliasName() {
      // TODO share or remove
      return alias == null ? null : (alias + "." + getName());
    }

    public String getFQBaseTypeName() {
      return baseTypeNameFQ != null
          ? baseTypeNameFQ
          : (baseType != null ? baseType.getFullyQualifiedTypeName() : null);
    }

    public List<EdmNavigationProperty.Builder> getNavigationProperties() {
      return navigationProperties;
    }

    public Func<EdmEntityType> builtFunc() {
      return new Func<EdmEntityType>(){
        @Override
        public EdmEntityType apply() {
          return build();
        }};
    }

  }

}
