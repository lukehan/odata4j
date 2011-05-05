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
import javax.ws.rs.core.Response;
import org.odata4j.core.ODataConstants;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.format.FormatWriter;
import org.odata4j.format.FormatWriterFactory;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.exceptions.ExceptionHandler;

import com.sun.jersey.api.core.HttpContext;

@Path("{entitySetName}{id: (\\(.+?\\))}")
public class EntityRequestResource extends BaseResource {

    private static final Logger log = Logger.getLogger(EntityRequestResource.class.getName());

    @PUT
    public Response updateEntity(@Context HttpContext context, @Context ODataProducer producer, final @PathParam("entitySetName") String entitySetName, @PathParam("id") String id) {

        log.info(String.format("updateEntity(%s,%s)", entitySetName, id));

        try {
        	OEntity entity = this.getRequestEntity(context.getRequest(),producer.getMetadata(),entitySetName,OEntityKey.parse(id));
        	producer.updateEntity(entitySetName, entity);
        }
        catch(Exception e) {
        	return ExceptionHandler.Handle(e);
        }

        return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
    }

    @POST
    public Response mergeEntity(@Context HttpContext context, @Context ODataProducer producer, final @PathParam("entitySetName") String entitySetName, @PathParam("id") String id) {

        log.info(String.format("mergeEntity(%s,%s)", entitySetName, id));
        try {
	        OEntityKey entityKey = OEntityKey.parse(id);
	
	        String method = context.getRequest().getHeaderValue(ODataConstants.Headers.X_HTTP_METHOD);
	        if ("MERGE".equals(method)) {
	            OEntity entity = this.getRequestEntity(context.getRequest(),producer.getMetadata(),entitySetName,entityKey);
	            producer.mergeEntity(entitySetName, entity);
	
	            return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
	        }
	
	        if ("DELETE".equals(method)) {
	            producer.deleteEntity(entitySetName, entityKey);
	            
	            return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
	        }
	        
	        if ("PUT".equals(method)) {
	            OEntity entity = this.getRequestEntity(context.getRequest(),producer.getMetadata(),entitySetName,OEntityKey.parse(id));
	            producer.updateEntity(entitySetName, entity);
	
	            return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
	        }
	
	        throw new RuntimeException("Expected a tunnelled PUT, MERGE or DELETE");
	        
        }catch(Exception e) {
        	return ExceptionHandler.Handle(e);
        }
    }

    @DELETE
    public Response deleteEntity(@Context HttpContext context, @Context ODataProducer producer, final @PathParam("entitySetName") String entitySetName, @PathParam("id") String id) {

        log.info(String.format("getEntity(%s,%s)", entitySetName, id));
        try {
        	OEntityKey entityKey = OEntityKey.parse(id);
        	producer.deleteEntity(entitySetName, entityKey);
        }catch(Exception e) {
        	return ExceptionHandler.Handle(e);
        }       

        return Response.ok().header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();
    }

    @GET
    @Produces({ODataConstants.APPLICATION_ATOM_XML_CHARSET_UTF8, ODataConstants.TEXT_JAVASCRIPT_CHARSET_UTF8, ODataConstants.APPLICATION_JAVASCRIPT_CHARSET_UTF8})
    public Response getEntity(@Context HttpContext context, @Context ODataProducer producer, final @PathParam("entitySetName") String entitySetName, @PathParam("id") String id, 
    		@QueryParam("$format") String format,
			@QueryParam("$callback") String callback,
			@QueryParam("$expand") String expand,
			@QueryParam("$select") String select) {

    	QueryInfo query = new QueryInfo(
				null,null,null,null,null,null,null,
				OptionsQueryParser.parseExpand(expand),
				OptionsQueryParser.parseSelect(select));
    	    	
    	log.info(String.format(
				"getEntity(%s,%s,%s,%s)",
				entitySetName,
				id,				
				expand,
				select));
    	
    	EntityResponse response=null;
    	
    	try {    	
    		response = producer.getEntity(entitySetName, OEntityKey.parse(id),query);
    	}
        catch(Exception e) {
        	return ExceptionHandler.Handle(e);
        }
        
        StringWriter sw = new StringWriter();
        FormatWriter<EntityResponse> fw = FormatWriterFactory.getFormatWriter(EntityResponse.class, context.getRequest().getAcceptableMediaTypes(), format, callback);
        fw.write(context.getUriInfo(), sw, response);
        String entity = sw.toString();

        return Response.ok(entity, fw.getContentType()).header(ODataConstants.Headers.DATA_SERVICE_VERSION, ODataConstants.DATA_SERVICE_VERSION_HEADER).build();

    }
    
    @Path("{first: \\$}links/{navProp:.+}") 
    public LinksRequestResource getLinks() {
    	return new LinksRequestResource();
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
