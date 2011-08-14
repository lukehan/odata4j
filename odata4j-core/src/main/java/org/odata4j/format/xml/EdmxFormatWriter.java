package org.odata4j.format.xml;

import java.io.Writer;

import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmAssociationSet;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmFunctionParameter;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.stax2.QName2;
import org.odata4j.stax2.XMLFactoryProvider2;
import org.odata4j.stax2.XMLWriter2;

public class EdmxFormatWriter extends XmlFormatWriter {

  public static void write(EdmDataServices services, Writer w) {

    XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
    writer.startDocument();

    writer.startElement(new QName2(edmx, "Edmx", "edmx"));
    writer.writeAttribute("Version", "1.0");
    writer.writeNamespace("edmx", edmx);
    writer.writeNamespace("d", d);
    writer.writeNamespace("m", m);

    writer.startElement(new QName2(edmx, "DataServices", "edmx"));
    writer.writeAttribute(new QName2(m, "DataServiceVersion", "m"), "1.0");

    // Schema
    for (EdmSchema schema : services.getSchemas()) {

      writer.startElement(new QName2("Schema"), edm);
      writer.writeAttribute("Namespace", schema.namespace);

      // ComplexType
      for (EdmComplexType ect : schema.complexTypes) {
        writer.startElement(new QName2("ComplexType"));

        writer.writeAttribute("Name", ect.name);

        write(ect.properties, writer);

        writer.endElement("ComplexType");
      }
      // EntityType
      for (EdmEntityType eet : schema.entityTypes) {
        writer.startElement(new QName2("EntityType"));

        writer.writeAttribute("Name", eet.name);

        // keys only on base types
        if (eet.isRootType()) {
          writer.startElement(new QName2("Key"));
          for (String key : eet.getKeys()) {
              writer.startElement(new QName2("PropertyRef"));
              writer.writeAttribute("Name", key);
              writer.endElement("PropertyRef");
          }

          writer.endElement("Key");
        } else {
            writer.writeAttribute("BaseType", eet.getBaseType().getFullyQualifiedTypeName());
        }

        write(eet.getDeclaredProperties(), writer);

        for (EdmNavigationProperty np : eet.getDeclaredNavigationProperties()) {

          writer.startElement(new QName2("NavigationProperty"));
          writer.writeAttribute("Name", np.name);
          writer.writeAttribute("Relationship", np.relationship.getFQNamespaceName());
          writer.writeAttribute("FromRole", np.fromRole.role);
          writer.writeAttribute("ToRole", np.toRole.role);

          writer.endElement("NavigationProperty");

        }

        writer.endElement("EntityType");

      }

      // Association
      for (EdmAssociation assoc : schema.associations) {
        writer.startElement(new QName2("Association"));

        writer.writeAttribute("Name", assoc.name);

        writer.startElement(new QName2("End"));
        writer.writeAttribute("Role", assoc.end1.role);
        writer.writeAttribute("Type", assoc.end1.type.getFullyQualifiedTypeName());
        writer.writeAttribute("Multiplicity", assoc.end1.multiplicity.getSymbolString());
        writer.endElement("End");

        writer.startElement(new QName2("End"));
        writer.writeAttribute("Role", assoc.end2.role);
        writer.writeAttribute("Type", assoc.end2.type.getFullyQualifiedTypeName());
        writer.writeAttribute("Multiplicity", assoc.end2.multiplicity.getSymbolString());
        writer.endElement("End");

        writer.endElement("Association");
      }

      // EntityContainer
      for (EdmEntityContainer container : schema.entityContainers) {
        writer.startElement(new QName2("EntityContainer"));

        writer.writeAttribute("Name", container.name);
        writer.writeAttribute(new QName2(m, "IsDefaultEntityContainer", "m"), Boolean.toString(container.isDefault));

        for (EdmEntitySet ees : container.entitySets) {
          writer.startElement(new QName2("EntitySet"));
          writer.writeAttribute("Name", ees.name);
          writer.writeAttribute("EntityType", ees.type.getFullyQualifiedTypeName());
          writer.endElement("EntitySet");
        }

        for (EdmFunctionImport fi : container.functionImports) {
          writer.startElement(new QName2("FunctionImport"));
          writer.writeAttribute("Name", fi.name);
          if (null != fi.entitySet) {
            writer.writeAttribute("EntitySet", fi.entitySet.name);
          }
          // TODO: how to differentiate inline ReturnType vs embedded ReturnType?
          writer.writeAttribute("ReturnType", fi.returnType.getFullyQualifiedTypeName());
          writer.writeAttribute(new QName2(m, "HttpMethod", "m"), fi.httpMethod);

          for (EdmFunctionParameter param : fi.parameters) {
              writer.startElement(new QName2("Parameter"));
              writer.writeAttribute("Name", param.name);
              writer.writeAttribute("Type", param.type.getFullyQualifiedTypeName());
              writer.writeAttribute("Mode", param.mode);
              writer.endElement("Parameter");
          }
          writer.endElement("FunctionImport");
        }

        for (EdmAssociationSet eas : container.associationSets) {
          writer.startElement(new QName2("AssociationSet"));
          writer.writeAttribute("Name", eas.name);
          writer.writeAttribute("Association", eas.association.getFQNamespaceName());

          writer.startElement(new QName2("End"));
          writer.writeAttribute("Role", eas.end1.role.role);
          writer.writeAttribute("EntitySet", eas.end1.entitySet.name);
          writer.endElement("End");

          writer.startElement(new QName2("End"));
          writer.writeAttribute("Role", eas.end2.role.role);
          writer.writeAttribute("EntitySet", eas.end2.entitySet.name);
          writer.endElement("End");

          writer.endElement("AssociationSet");
        }

        writer.endElement("EntityContainer");
      }

      writer.endElement("Schema");

    }

    writer.endDocument();
  }

  private static void write(Iterable<EdmProperty> properties, XMLWriter2 writer) {
    for (EdmProperty prop : properties) {
      writer.startElement(new QName2("Property"));

      writer.writeAttribute("Name", prop.name);
      writer.writeAttribute("Type", prop.type.getFullyQualifiedTypeName());
      writer.writeAttribute("Nullable", Boolean.toString(prop.nullable));
      if (prop.maxLength != null)
        writer.writeAttribute("MaxLength", Integer.toString(prop.maxLength));
      writer.endElement("Property");
    }
  }

}
