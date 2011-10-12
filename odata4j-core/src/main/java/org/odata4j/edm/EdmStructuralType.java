package org.odata4j.edm;

import java.util.ArrayList;
import java.util.List;

import org.core4j.Enumerable;
import org.odata4j.core.ImmutableList;
import org.odata4j.core.Named;
import org.odata4j.core.OPredicates;

public abstract class EdmStructuralType extends EdmNonSimpleType implements Named {

  private final String namespace;
  private final String name;
  private final List<EdmProperty> declaredProperties;
  private final Boolean isAbstract;
  private EdmEntityType baseType;

  protected EdmStructuralType(EdmEntityType baseType, String namespace, String name, List<EdmProperty.Builder> declaredProperties,
      EdmDocumentation doc, ImmutableList<EdmAnnotation<?>> annotations, Boolean isAbstract) {
    super(namespace + "." + name, doc, annotations);
    this.baseType = baseType;
    this.namespace = namespace;
    this.name = name;
    this.isAbstract = isAbstract;
    this.declaredProperties = new ArrayList<EdmProperty>();
    if (declaredProperties != null) {
      for (EdmProperty.Builder declaredProperty : declaredProperties) {
        this.declaredProperties.add(declaredProperty.setDeclaringType(this).build());
      }
    }
  }

  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
  }

  public Boolean getIsAbstract() {
    return isAbstract;
  }

  public EdmEntityType getBaseType() {
    return baseType;
  }

  /**
   * Finds a property by name, searching up the type hierarchy if necessary.
   */
  public EdmProperty findProperty(String name) {
    return getProperties().firstOrNull(OPredicates.edmPropertyNameEquals(name));
  }

  /**
   * Gets the properties defined for this structural type <i>not including</i> inherited properties.
   */
  public Enumerable<EdmProperty> getDeclaredProperties() {
    return Enumerable.create(declaredProperties);
  }

  /**
   * Finds a property by name on this structural type <i>not including</i> inherited properties.
   */
  public EdmProperty findDeclaredProperty(String name) {
    return getDeclaredProperties().firstOrNull(OPredicates.edmPropertyNameEquals(name));
  }

  /**
   * Gets the properties defined for this structural type <i>including</i> inherited properties.
   */
  public Enumerable<EdmProperty> getProperties() {
    return isRootType()
        ? getDeclaredProperties()
        : baseType.getProperties().union(getDeclaredProperties());
  }

  public boolean isRootType() {
    return baseType == null;
  }

  public abstract static class Builder<T, TBuilder> extends EdmType.Builder<T, TBuilder> {

    protected String namespace;
    protected String name;
    protected final List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
    protected Boolean isAbstract;
    protected EdmEntityType baseType;

    protected void fillBuilder(EdmStructuralType structuralType, BuilderContext context) {
      List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
      for(EdmProperty property : structuralType.declaredProperties)
        properties.add(EdmProperty.newBuilder(property, context));
      this.namespace = structuralType.namespace;
      this.name = structuralType.name;
      this.properties.addAll(properties);
      this.isAbstract = structuralType.isAbstract;
      this.baseType = structuralType.baseType;
    }

    @SuppressWarnings("unchecked")
    public TBuilder setNamespace(String namespace) {
      this.namespace = namespace;
      return (TBuilder) this;
    }

    @SuppressWarnings("unchecked")
    public TBuilder setName(String name) {
      this.name = name;
      return (TBuilder) this;
    }

    @SuppressWarnings("unchecked")
    public TBuilder addProperties(List<EdmProperty.Builder> properties) {
      this.properties.addAll(properties);
      return (TBuilder) this;
    }

    @SuppressWarnings("unchecked")
    public TBuilder setIsAbstract(Boolean isAbstract) {
      this.isAbstract = isAbstract;
      return (TBuilder) this;
    }

    public String getFullyQualifiedTypeName() {
      return namespace + "." + name;
    }

    public EdmProperty.Builder findProperty(String name) {
      // TODO share or remove
      return Enumerable.create(properties).firstOrNull(OPredicates.nameEquals(EdmProperty.Builder.class, name));
    }

  }

}
