package org.odata4j.format;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.json.JsonCollectionFormatWriter;
import org.odata4j.format.json.JsonComplexObjectFormatWriter;
import org.odata4j.format.json.JsonEntryFormatWriter;
import org.odata4j.format.json.JsonFeedFormatWriter;
import org.odata4j.format.json.JsonPropertyFormatWriter;
import org.odata4j.format.json.JsonRequestEntryFormatWriter;
import org.odata4j.format.json.JsonServiceDocumentFormatWriter;
import org.odata4j.format.json.JsonSingleLinkFormatWriter;
import org.odata4j.format.json.JsonSingleLinksFormatWriter;
import org.odata4j.format.xml.AtomEntryFormatWriter;
import org.odata4j.format.xml.AtomFeedFormatWriter;
import org.odata4j.format.xml.AtomRequestEntryFormatWriter;
import org.odata4j.format.xml.AtomServiceDocumentFormatWriter;
import org.odata4j.format.xml.AtomSingleLinkFormatWriter;
import org.odata4j.format.xml.AtomSingleLinksFormatWriter;
import org.odata4j.format.xml.XmlPropertyFormatWriter;
import org.odata4j.producer.CollectionResponse;
import org.odata4j.producer.ComplexObjectResponse;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.PropertyResponse;
import org.odata4j.producer.exceptions.NotImplementedException;

public class FormatWriterFactory {

  private static interface FormatWriters {

    FormatWriter<EdmDataServices> getServiceDocumentFormatWriter();

    FormatWriter<EntitiesResponse> getFeedFormatWriter();

    FormatWriter<EntityResponse> getEntryFormatWriter();

    FormatWriter<PropertyResponse> getPropertyFormatWriter();

    FormatWriter<Entry> getRequestEntryFormatWriter();

    FormatWriter<SingleLink> getSingleLinkFormatWriter();

    FormatWriter<SingleLinks> getSingleLinksFormatWriter();

    FormatWriter<ComplexObjectResponse> getComplexObjectFormatWriter();

    FormatWriter<CollectionResponse<?>> getCollectionFormatWriter();
  }

  @SuppressWarnings("unchecked")
  public static <T> FormatWriter<T> getFormatWriter(Class<T> targetType, List<MediaType> acceptTypes, String format, String callback) {

    FormatType type = null;

    // if format is explicitly specified, use that
    if (format != null)
      type = FormatType.parse(format);

    // if header accepts json, use that
    if (type == null && acceptTypes != null) {
      for (MediaType acceptType : acceptTypes) {
        if (acceptType.equals(MediaType.APPLICATION_JSON_TYPE)) {
          type = FormatType.JSON;
          break;
        }
      }
    }

    // else default to atom
    if (type == null)
      type = FormatType.ATOM;

    FormatWriters formatWriters = type.equals(FormatType.JSON) ? new JsonWriters(callback) : new AtomWriters();

    if (targetType.equals(EdmDataServices.class))
      return (FormatWriter<T>) formatWriters.getServiceDocumentFormatWriter();

    if (targetType.equals(EntitiesResponse.class))
      return (FormatWriter<T>) formatWriters.getFeedFormatWriter();

    if (targetType.equals(EntityResponse.class))
      return (FormatWriter<T>) formatWriters.getEntryFormatWriter();

    if (targetType.equals(PropertyResponse.class))
      return (FormatWriter<T>) formatWriters.getPropertyFormatWriter();

    if (Entry.class.isAssignableFrom(targetType))
      return (FormatWriter<T>) formatWriters.getRequestEntryFormatWriter();

    if (SingleLink.class.isAssignableFrom(targetType))
      return (FormatWriter<T>) formatWriters.getSingleLinkFormatWriter();

    if (SingleLinks.class.isAssignableFrom(targetType))
      return (FormatWriter<T>) formatWriters.getSingleLinksFormatWriter();

    if (targetType.equals(ComplexObjectResponse.class)) {
      return (FormatWriter<T>) formatWriters.getComplexObjectFormatWriter();
    }

    if (targetType.equals(CollectionResponse.class)) {
      return (FormatWriter<T>) formatWriters.getCollectionFormatWriter();
    }

    throw new IllegalArgumentException("Unable to locate format writer for " + targetType.getName() + " and format " + type);

  }

  public static class JsonWriters implements FormatWriters {

    private final String callback;

    public JsonWriters(String callback) {
      this.callback = callback;
    }

    @Override
    public FormatWriter<EdmDataServices> getServiceDocumentFormatWriter() {
      return new JsonServiceDocumentFormatWriter(callback);
    }

    @Override
    public FormatWriter<EntitiesResponse> getFeedFormatWriter() {
      return new JsonFeedFormatWriter(callback);
    }

    @Override
    public FormatWriter<EntityResponse> getEntryFormatWriter() {
      return new JsonEntryFormatWriter(callback);
    }

    @Override
    public FormatWriter<PropertyResponse> getPropertyFormatWriter() {
      return new JsonPropertyFormatWriter(callback);
    }

    @Override
    public FormatWriter<Entry> getRequestEntryFormatWriter() {
      return new JsonRequestEntryFormatWriter(callback);
    }

    @Override
    public FormatWriter<SingleLink> getSingleLinkFormatWriter() {
      return new JsonSingleLinkFormatWriter(callback);
    }

    @Override
    public FormatWriter<SingleLinks> getSingleLinksFormatWriter() {
      return new JsonSingleLinksFormatWriter(callback);
    }

    @Override
    public FormatWriter<ComplexObjectResponse> getComplexObjectFormatWriter() {
        return new JsonComplexObjectFormatWriter(callback);
    }

    @Override
    public FormatWriter<CollectionResponse<?>> getCollectionFormatWriter() {
      return new JsonCollectionFormatWriter(callback);
    }
  }

  public static class AtomWriters implements FormatWriters {

    @Override
    public FormatWriter<EdmDataServices> getServiceDocumentFormatWriter() {
      return new AtomServiceDocumentFormatWriter();
    }

    @Override
    public FormatWriter<EntitiesResponse> getFeedFormatWriter() {
      return new AtomFeedFormatWriter();
    }

    @Override
    public FormatWriter<EntityResponse> getEntryFormatWriter() {
      return new AtomEntryFormatWriter();
    }

    @Override
    public FormatWriter<PropertyResponse> getPropertyFormatWriter() {
      return new XmlPropertyFormatWriter();
    }

    @Override
    public FormatWriter<Entry> getRequestEntryFormatWriter() {
      return new AtomRequestEntryFormatWriter();
    }

    @Override
    public FormatWriter<SingleLink> getSingleLinkFormatWriter() {
      return new AtomSingleLinkFormatWriter();
    }

    @Override
    public FormatWriter<SingleLinks> getSingleLinksFormatWriter() {
      return new AtomSingleLinksFormatWriter();
    }

    @Override
    public FormatWriter<ComplexObjectResponse> getComplexObjectFormatWriter() {
      throw new NotImplementedException();
    }

    @Override
    public FormatWriter<CollectionResponse<?>> getCollectionFormatWriter() {
      throw new NotImplementedException();
    }

  }

}
