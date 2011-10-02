package org.odata4j.edm;

import java.util.ArrayList;
import java.util.List;

import org.core4j.Enumerable;
import org.odata4j.core.OPredicates;

public class EdmEntityType extends EdmStructuralType {

  private final String alias;
  private final Boolean hasStream;
  private final List<String> keys;
  private final List<EdmNavigationProperty> navigationProperties;

  private String baseTypeNameFQ;

  public EdmEntityType(String namespace, String alias, String name, Boolean hasStream, List<String> keys,
      List<EdmProperty> properties, List<EdmNavigationProperty> navigationProperties) {
    this(namespace, alias, name, hasStream, keys, null, properties, navigationProperties);
  }

  public EdmEntityType(String namespace, String alias, String name, Boolean hasStream, List<String> keys,
      List<EdmProperty> properties, List<EdmNavigationProperty> navigationProperties,
      String baseTypeNameFQ, EdmDocumentation doc, List<EdmAnnotation<?>> annotations, Boolean isAbstract) {
    this(namespace, alias, name, hasStream, keys, null, properties, navigationProperties, doc, annotations, isAbstract);
    // during schema parsing we may not have the base type object yet...
    this.baseTypeNameFQ = baseTypeNameFQ;
  }

  public EdmEntityType(String namespace, String alias, String name, Boolean hasStream,
      List<String> keys, EdmEntityType baseType, List<EdmProperty> properties,
      List<EdmNavigationProperty> navigationProperties) {
    this(namespace, alias, name, hasStream, keys, baseType, properties, navigationProperties, null, null, null);
  }

  public EdmEntityType(String namespace, String alias, String name, Boolean hasStream,
      List<String> keys, EdmEntityType baseType, List<EdmProperty> properties,
      List<EdmNavigationProperty> navigationProperties,
      EdmDocumentation doc, List<EdmAnnotation<?>> annotations) {
    this(namespace, alias, name, hasStream, keys, baseType, properties,
            navigationProperties, doc, annotations, null);
  }

  public EdmEntityType(String namespace, String alias, String name, Boolean hasStream,
      List<String> keys, EdmEntityType baseType, List<EdmProperty> properties,
      List<EdmNavigationProperty> navigationProperties,
      EdmDocumentation doc, List<EdmAnnotation<?>> annotations, Boolean isAbstract) {
    super(baseType, namespace, name, properties, doc, annotations, isAbstract);
    this.alias = alias;
    this.hasStream = hasStream;

    this.keys = keys;

    if (baseType == null && keys == null)
      throw new IllegalArgumentException("Root types must have keys");
    if (baseType != null && keys != null)
      throw new IllegalArgumentException("Keys on root types only");

    this.navigationProperties = navigationProperties == null ? new ArrayList<EdmNavigationProperty>() : navigationProperties;
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
    return getNavigationProperties().firstOrNull(OPredicates.edmNavigationPropertyNameEquals(name));
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
    return getDeclaredNavigationProperties().firstOrNull(OPredicates.edmNavigationPropertyNameEquals(name));
  }

  /**
   * Gets the navigation properties defined for this entity type <i>including</i> inherited properties.
   */
  public Enumerable<EdmNavigationProperty> getNavigationProperties() {
    return isRootType()
        ? getDeclaredNavigationProperties()
        : getBaseType().getNavigationProperties().union(getDeclaredNavigationProperties());
  }

  public String getFQBaseTypeName() {
    return baseTypeNameFQ != null ? baseTypeNameFQ :
        (getBaseType() != null ? getBaseType().getFullyQualifiedTypeName() : null);
  }

  /**
   * Gets the keys for this EdmEntityType.  Keys are defined only in a root types.
   */
  public List<String> getKeys() {
    return isRootType() ? keys : getBaseType().getKeys();
  }

  //TODO remove!
  public void addNavigationProperty(EdmNavigationProperty np) {
    this.navigationProperties.add(np);
  }

  // TODO remove!
  public void setDeclaredNavigationProperties(Enumerable<EdmNavigationProperty> navProperties) {
    this.navigationProperties.clear();
    this.navigationProperties.addAll(navProperties.toList());
  }

}
