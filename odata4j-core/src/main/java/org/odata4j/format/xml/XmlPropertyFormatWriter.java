package org.odata4j.format.xml;

import java.io.Writer;

import org.odata4j.core.ODataConstants;
import org.odata4j.format.FormatWriter;
import org.odata4j.producer.PropertyResponse;
import org.odata4j.stax2.XMLFactoryProvider2;
import org.odata4j.stax2.XMLWriter2;

import com.sun.jersey.api.core.ExtendedUriInfo;

public class XmlPropertyFormatWriter extends XmlFormatWriter implements FormatWriter<PropertyResponse> {

  @Override
  public String getContentType() {
    return ODataConstants.APPLICATION_XML_CHARSET_UTF8;
  }

  @Override
  public void write(ExtendedUriInfo uriInfo, Writer w, PropertyResponse target) {
    XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
    writer.startDocument();
    writeProperty(writer, target.getProperty(), true);
    writer.endDocument();
  }
}
