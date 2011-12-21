package org.odata4j.producer.resources;

import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;

import org.odata4j.core.ODataConstants;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityIds;
import org.odata4j.core.OEntityKey;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.FormatWriterFactory;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;

@Path("{entitySetName}{id: (\\(.+?\\))}")
public class EntityRequestResource extends BaseResource {

  private static final Logger log = Logger.getLogger(EntityRequestResource.class.getName());

  @PUT
  public Response updateEntity(@Context HttpHeaders httpHeaders, @Context UriInfo uriInfo, @Context ContextResolver<ODataProducer> producerResolver,
      @PathParam("entitySetName") String entitySetName,
      @PathParam("id") String id,
      String payload) {

    log.info(String.format("updateEntity(%s,%s)", entitySetName, id));

    ODataProducer producer = producerResolver.getContext(ODataProducer.class);

    OEntity entity = this.getRequestEntity(httpHeaders, uriInfo, payload, producer.getMetadata(), entitySetName, OEntityKey.parse(id));
    producer.updateEntity(entitySetName, entity);

    return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
  }

  @POST
  public Response mergeEntity(@Context HttpHeaders httpHeaders, @Context UriInfo uriInfo, @Context ContextResolver<ODataProducer> producerResolver,
      @PathParam("entitySetName") String entitySetName,
      @PathParam("id") String id,
      String payload) {

    log.info(String.format("mergeEntity(%s,%s)", entitySetName, id));

    ODataProducer producer = producerResolver.getContext(ODataProducer.class);

    OEntityKey entityKey = OEntityKey.parse(id);

    String method = httpHeaders.getRequestHeaders().getFirst(ODataConstants.Headers.X_HTTP_METHOD);
    if ("MERGE".equals(method)) {
      OEntity entity = this.getRequestEntity(httpHeaders, uriInfo, payload, producer.getMetadata(), entitySetName, entityKey);
      producer.mergeEntity(entitySetName, entity);

      return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
    }

    if ("DELETE".equals(method)) {
      producer.deleteEntity(entitySetName, entityKey);

      return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
    }

    if ("PUT".equals(method)) {
      OEntity entity = this.getRequestEntity(httpHeaders, uriInfo, payload, producer.getMetadata(), entitySetName, OEntityKey.parse(id));
      producer.updateEntity(entitySetName, entity);

      return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
    }

    throw new RuntimeException("Expected a tunnelled PUT, MERGE or DELETE");
  }

  @DELETE
  public Response deleteEntity(@Context ContextResolver<ODataProducer> producerResolver,
      @PathParam("entitySetName") String entitySetName,
      @PathParam("id") String id) {

    log.info(String.format("getEntity(%s,%s)", entitySetName, id));

    ODataProducer producer = producerResolver.getContext(ODataProducer.class);

    OEntityKey entityKey = OEntityKey.parse(id);
    producer.deleteEntity(entitySetName, entityKey);

    return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
  }

  @GET
  @Produces({ ODataConstants.APPLICATION_ATOM_XML_CHARSET_UTF8, ODataConstants.TEXT_JAVASCRIPT_CHARSET_UTF8, ODataConstants.APPLICATION_JAVASCRIPT_CHARSET_UTF8 })
  public Response getEntity(@Context HttpHeaders httpHeaders, @Context UriInfo uriInfo, @Context ContextResolver<ODataProducer> producerResolver,
      @PathParam("entitySetName") String entitySetName,
      @PathParam("id") String id,
      @QueryParam("$format") String format,
      @QueryParam("$callback") String callback,
      @QueryParam("$expand") String expand,
      @QueryParam("$select") String select) {

    QueryInfo query = new QueryInfo(
        null, null, null, null, null, null,
        OptionsQueryParser.parseCustomOptions(uriInfo),
        OptionsQueryParser.parseExpand(expand),
        OptionsQueryParser.parseSelect(select));

    log.info(String.format(
        "getEntity(%s,%s,%s,%s)",
        entitySetName,
        id,
        expand,
        select));

    ODataProducer producer = producerResolver.getContext(ODataProducer.class);

    EntityResponse response = producer.getEntity(entitySetName, OEntityKey.parse(id), query);

    StringWriter sw = new StringWriter();
    FormatWriter<EntityResponse> fw = FormatWriterFactory.getFormatWriter(EntityResponse.class, httpHeaders.getAcceptableMediaTypes(), format, callback);
    fw.write(uriInfo, sw, response);
    String entity = sw.toString();

    return Response.ok(entity, fw.getContentType()).header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
  }

  @Path("{first: \\$}links/{targetNavProp:.+?}{targetId: (\\(.+?\\))?}")
  public LinksRequestResource getLinks(
      @PathParam("entitySetName") String entitySetName,
      @PathParam("id") String id,
      @PathParam("targetNavProp") String targetNavProp,
      @PathParam("targetId") String targetId) {

    OEntityKey targetEntityKey = targetId == null || targetId.isEmpty() ? null : OEntityKey.parse(targetId);

    return new LinksRequestResource(OEntityIds.create(entitySetName, OEntityKey.parse(id)), targetNavProp, targetEntityKey);
  }

  @Path("{navProp:.+}")
  public PropertyRequestResource getNavProperty() {
    return new PropertyRequestResource();
  }

  @Path("{navProp: .+?}{optionalParens: ((\\(\\)))}")
  public PropertyRequestResource getSimpleNavProperty() {
    return new PropertyRequestResource();
  }

}