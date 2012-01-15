package org.odata4j.producer.resources;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;

import org.odata4j.core.ODataConstants;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.FormatType;
import org.odata4j.format.xml.EdmxFormatWriter;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.edm.MetadataProducer;

@Path("{first: \\$}metadata")
public class MetadataResource {

  @GET
  @Produces({ ODataConstants.APPLICATION_XML_CHARSET_UTF8, ODataConstants.APPLICATION_ATOMSVC_XML_CHARSET_UTF8 })
  public Response getMetadata(
      @Context HttpHeaders httpHeaders,
      @Context UriInfo uriInfo,
      @Context ContextResolver<ODataProducer> producerResolver,
      @QueryParam("$format") String format) {

    ODataProducer producer = producerResolver.getContext(ODataProducer.class);

    // a request for media type atomsvc+xml means give me the service document of the metadata producer
    if ("atomsvc".equals(format) || isAtomSvcRequest(httpHeaders)) {
      MetadataProducer metadataProducer = producer.getMetadataProducer();
      if (metadataProducer == null) {
        return noMetadata();
      }
      ServiceDocumentResource r = new ServiceDocumentResource();
      return r.getServiceDocument(httpHeaders, uriInfo, producerResolver, FormatType.ATOM.name(), null);
    } else {
      StringWriter w = new StringWriter();
      ODataProducer source = "metamodel".equals(format) ? producer.getMetadataProducer() : producer;
      if (source == null) {
        return noMetadata();
      }
      EdmDataServices s = source.getMetadata();
      EdmxFormatWriter.write(s, w);

      return Response.ok(w.toString(), ODataConstants.APPLICATION_XML_CHARSET_UTF8)
          .header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
    }
  }

  private boolean isAtomSvcRequest(HttpHeaders h) {
    for (MediaType mt : h.getAcceptableMediaTypes()) {
      if (mt.equals(ODataConstants.APPLICATION_ATOMSVC_XML_TYPE)) {
        return true;
      }
    }
    return false;
  }

  @GET
  @Path("{entitySetName}")
  @Produces({ ODataConstants.APPLICATION_ATOM_XML_CHARSET_UTF8,
      ODataConstants.TEXT_JAVASCRIPT_CHARSET_UTF8,
      ODataConstants.APPLICATION_JAVASCRIPT_CHARSET_UTF8 })
  public Response getMetadataEntities(
      @Context HttpHeaders httpHeaders,
      @Context UriInfo uriInfo,
      @Context ContextResolver<ODataProducer> producerResolver,
      final @PathParam("entitySetName") String entitySetName,
      final @PathParam("optionalId") String optionalId,
      @QueryParam("$inlinecount") String inlineCount,
      @QueryParam("$top") String top,
      @QueryParam("$skip") String skip,
      @QueryParam("$filter") String filter,
      @QueryParam("$orderby") String orderBy,
      @QueryParam("$format") String format,
      @QueryParam("$callback") String callback,
      @QueryParam("$skiptoken") String skipToken,
      @QueryParam("$expand") String expand,
      @QueryParam("$select") String select) throws Exception {

    ODataProducer producer = producerResolver.getContext(ODataProducer.class);

    MetadataProducer metadataProducer = producer.getMetadataProducer();
    if (metadataProducer == null) {
      return noMetadata();
    }

    EntitiesRequestResource r = new EntitiesRequestResource();
    return r.getEntitiesImpl(httpHeaders, uriInfo, metadataProducer, entitySetName, false, inlineCount, top, skip, filter, orderBy, format, callback, skipToken, expand, select);
  }

  @GET
  @Path("{entitySetName}{id: (\\(.+?\\))}")
  @Produces({ ODataConstants.APPLICATION_ATOM_XML_CHARSET_UTF8,
      ODataConstants.TEXT_JAVASCRIPT_CHARSET_UTF8,
      ODataConstants.APPLICATION_JAVASCRIPT_CHARSET_UTF8 })
  public Response getMetadataEntity(
      @Context HttpHeaders httpHeaders,
      @Context UriInfo uriInfo,
      @Context ContextResolver<ODataProducer> producerResolver,
      final @PathParam("entitySetName") String entitySetName,
      final @PathParam("id") String id,
      @QueryParam("$format") String format,
      @QueryParam("$callback") String callback,
      @QueryParam("$expand") String expand,
      @QueryParam("$select") String select) {

    ODataProducer producer = producerResolver.getContext(ODataProducer.class);

    MetadataProducer metadataProducer = producer.getMetadataProducer();
    if (metadataProducer == null) {
      return noMetadata();
    }

    EntityRequestResource r = new EntityRequestResource();
    return r.getEntityImpl(httpHeaders, uriInfo, metadataProducer, entitySetName, id, format, callback, expand, select);
  }

  public static final int HTTP_NOT_IMPLEMENTED = 501;

  private Response error(int status, String msg) {
    return Response.status(status).entity(msg).type("text/plain").build();
  }

  private Response noMetadata() {
    return error(HTTP_NOT_IMPLEMENTED, "Queryable metadata not implemented by this producer");
  }

}
