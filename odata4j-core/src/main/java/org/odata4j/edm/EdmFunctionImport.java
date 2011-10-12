package org.odata4j.edm;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.core.ImmutableList;

public class EdmFunctionImport extends EdmItem {

  private final String name;
  private final EdmEntitySet entitySet;
  private final EdmType returnType;
  private final String httpMethod;
  private final ImmutableList<EdmFunctionParameter> parameters;

  private EdmFunctionImport(String name, EdmEntitySet entitySet, EdmType returnType,
      String httpMethod, ImmutableList<EdmFunctionParameter> parameters, EdmDocumentation doc, ImmutableList<EdmAnnotation<?>> annots) {
    super(doc, annots);
    this.name = name;
    this.entitySet = entitySet;
    this.returnType = returnType;
    this.httpMethod = httpMethod;
    this.parameters = parameters;
  }

  public String getName() {
    return name;
  }

  public EdmEntitySet getEntitySet() {
    return entitySet;
  }

  public EdmType getReturnType() {
    return returnType;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public List<EdmFunctionParameter> getParameters() {
    return parameters;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(EdmFunctionImport functionImport, BuilderContext context) {
    return context.newBuilder(functionImport, new Builder());
  }

  public static class Builder extends EdmItem.Builder<EdmFunctionImport, Builder> {
    private String name;
    private EdmEntitySet.Builder entitySet;
    private String entitySetName;
    private EdmType returnType;
    private EdmType.Builder<?, ?> returnTypeBuilder;
    private String returnTypeName;
    private String httpMethod;
    private final List<EdmFunctionParameter.Builder> parameters = new ArrayList<EdmFunctionParameter.Builder>();
    private boolean isCollection;

    @Override
    Builder newBuilder(EdmFunctionImport functionImport, BuilderContext context) {
      List<EdmFunctionParameter.Builder> functionParameters = new ArrayList<EdmFunctionParameter.Builder>();
      for(EdmFunctionParameter functionParameter : functionImport.parameters)
        functionParameters.add(EdmFunctionParameter.newBuilder(functionParameter, context));
      return new Builder().setName(functionImport.name).setEntitySet(EdmEntitySet.newBuilder(functionImport.entitySet, context)).setReturnType(functionImport.returnType).setHttpMethod(functionImport.httpMethod)
          .addParameters(functionParameters);
    }
    
    public EdmFunctionImport build() {
      List<EdmFunctionParameter> parameters = new ArrayList<EdmFunctionParameter>();
      for(EdmFunctionParameter.Builder parameter : this.parameters)
        parameters.add(parameter.build());
      EdmType returnType = this.returnType != null ? this.returnType : returnTypeBuilder.build();
      return new EdmFunctionImport(name, entitySet == null ? null : entitySet.build(), returnType, httpMethod, ImmutableList.copyOf(parameters), getDocumentation(), ImmutableList.copyOf(getAnnotations()));
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setEntitySet(EdmEntitySet.Builder entitySet) {
      this.entitySet = entitySet;
      return this;
    }

    public Builder setReturnType(EdmType returnType) {
      this.returnType = returnType;
      return this;
    }

    public Builder setReturnType(EdmType.Builder<?, ?> returnType) {
      this.returnTypeBuilder = returnType;
      return this;
    }

    public Builder setHttpMethod(String httpMethod) {
      this.httpMethod = httpMethod;
      return this;
    }

    public Builder addParameters(List<EdmFunctionParameter.Builder> parameters) {
      this.parameters.addAll(parameters);
      return this;
    }

    public String getEntitySetName() {
      return entitySetName;
    }

    public Builder setEntitySetName(String entitySetName) {
      this.entitySetName = entitySetName;
      return this;
    }

    public String getReturnTypeName() {
      return returnTypeName;
    }

    public boolean isCollection() {
      return isCollection;
    }

    public String getName() {
      return name;
    }

    public String getHttpMethod() {
      return httpMethod;
    }

    public List<EdmFunctionParameter.Builder> getParameters() {
      return parameters;
    }

    public Builder setReturnTypeName(String returnTypeName) {
      this.returnTypeName = returnTypeName;
      return this;
    }

    public Builder setIsCollection(boolean isCollection) {
      this.isCollection = isCollection;
      return this;
    }

  }

}
