package org.odata4j.format.xml;

import java.io.Writer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.odata4j.core.ODataConstants;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.format.Entry;
import org.odata4j.format.FormatWriter;
import org.odata4j.internal.InternalUtil;
import org.odata4j.producer.EntityResponse;
import org.odata4j.stax2.QName2;
import org.odata4j.stax2.XMLFactoryProvider2;
import org.odata4j.stax2.XMLWriter2;

import com.sun.jersey.api.core.ExtendedUriInfo;

public class AtomEntryFormatWriter extends XmlFormatWriter implements FormatWriter<EntityResponse> {

  protected String baseUri;

  public void writeRequestEntry(Writer w, Entry entry) {

    DateTime utc = new DateTime().withZone(DateTimeZone.UTC);
    String updated = InternalUtil.toString(utc);

    XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
    writer.startDocument();

    writer.startElement(new QName2("entry"), atom);
    writer.writeNamespace("d", d);
    writer.writeNamespace("m", m);

    writeEntry(writer, null, entry.getEntity().getProperties(),
        entry.getEntity().getLinks(),
        null, null, updated, null, false);
    writer.endDocument();

  }

  @Override
  public String getContentType() {
    return ODataConstants.APPLICATION_ATOM_XML_CHARSET_UTF8;
  }

  @Override
  public void write(ExtendedUriInfo uriInfo, Writer w, EntityResponse target) {
    String baseUri = uriInfo.getBaseUri().toString();
    EdmEntitySet ees = target.getEntity().getEntitySet();

    String entitySetName = ees.name;
    DateTime utc = new DateTime().withZone(DateTimeZone.UTC);
    String updated = InternalUtil.toString(utc);

    XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
    writer.startDocument();

    writer.startElement(new QName2("entry"), atom);
    writer.writeNamespace("m", m);
    writer.writeNamespace("d", d);
    writer.writeAttribute("xml:base", baseUri);

    writeEntry(writer, target.getEntity(), target.getEntity().getProperties(), target.getEntity().getLinks(), entitySetName, baseUri, updated, ees, true);
    writer.endDocument();
  }
}
