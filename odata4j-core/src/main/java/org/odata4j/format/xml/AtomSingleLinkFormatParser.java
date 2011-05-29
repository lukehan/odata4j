package org.odata4j.format.xml;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.format.SingleLink;
import org.odata4j.format.SingleLinks;
import org.odata4j.stax2.QName2;
import org.odata4j.stax2.XMLEvent2;
import org.odata4j.stax2.XMLEventReader2;

public class AtomSingleLinkFormatParser extends XmlFormatParser {

  private static final QName2 URI = new QName2(NS_DATASERVICES, "uri");
  
  public static Iterable<SingleLink> parseLinks(XMLEventReader2 reader) {
    List<SingleLink> rt = new ArrayList<SingleLink>();
    while (reader.hasNext()) {
      XMLEvent2 event = reader.nextEvent();
      if (isStartElement(event, URI)) {
        rt.add(SingleLinks.create(reader.getElementText()));
      }
    }
    return rt;
  }
  
}
