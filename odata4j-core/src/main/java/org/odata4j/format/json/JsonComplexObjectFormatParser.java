package org.odata4j.format.json;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.odata4j.core.OComplexObject;
import org.odata4j.core.OComplexObjects;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.format.FormatParser;
import org.odata4j.format.Settings;
import org.odata4j.format.json.JsonStreamReaderFactory.JsonParseException;
import org.odata4j.format.json.JsonStreamReaderFactory.JsonStreamReader;
import org.odata4j.format.json.JsonStreamReaderFactory.JsonStreamReader.JsonEvent;

/**
 * Parser for OComplexObjects in JSON
 */
public class JsonComplexObjectFormatParser extends JsonFormatParser implements FormatParser<OComplexObject> {

    public JsonComplexObjectFormatParser(Settings s) {
        super(s);
        returnType = (EdmComplexType) (null == s ? null : s.parseType);
    }
    
    private EdmComplexType returnType = null;
    
    @Override
    public OComplexObject parse(Reader reader) {
        JsonStreamReader jsr = JsonStreamReaderFactory.createJsonStreamReader(reader);
        try {

            if (isResponse) {
                ensureNext(jsr);
                ensureStartObject(jsr.nextEvent()); // the response object
        
                // "d" property
                ensureNext(jsr);
                ensureStartProperty(jsr.nextEvent(), DATA_PROPERTY);
                
                // "aresult" for DataServiceVersion > 1.0
                if (version.compareTo(ODataVersion.V1) > 0) {
                    ensureNext(jsr);
                    ensureStartObject(jsr.nextEvent());
                    ensureNext(jsr);
                    ensureStartProperty(jsr.nextEvent(), RESULTS_PROPERTY);
                }
            }
            
            // parse the entry
            OComplexObject o = parseSingleObject(jsr);

            if (isResponse) {
                
                // the "d" property was our object...it is also a property.
                ensureNext(jsr);
                ensureEndProperty(jsr.nextEvent());
                
                if (version.compareTo(ODataVersion.V1) > 0) {
                    ensureNext(jsr);
                    ensureEndObject(jsr.nextEvent());
                    ensureNext(jsr);
                    ensureEndProperty(jsr.nextEvent()); // "results"
                }
                ensureNext(jsr);
                ensureEndObject(jsr.nextEvent()); // the response object
            }
            
            return o;
           
        } finally {
            jsr.close();
        }
    }
    
    public OComplexObject parseSingleObject(JsonStreamReader jsr) {
        ensureNext(jsr);
        
        // this can be used in a context where we require an object and one
        // where there *may* be an object...like a collection
        
        JsonEvent event = jsr.nextEvent();
        if (event.isStartObject()) {

            List<OProperty<?>> props = new ArrayList<OProperty<?>>();

            while (jsr.hasNext()) {
                event = jsr.nextEvent();

                if (event.isStartProperty()) {
                    addProperty(props, event.asStartProperty().getName(), jsr);
                } else if (event.isEndObject()) {
                    break;
                } else {
                    throw new JsonParseException("unexpected parse event: " + event.toString());
                }
            }
            return OComplexObjects.create(returnType, props);
        } else {
            // not a start object.
            return null;
        }
    }
    
    protected void addProperty(List<OProperty<?>> props, String name, JsonStreamReader jsr) {

        JsonEvent event = jsr.nextEvent();

        if (event.isEndProperty()) {
            // scalar property
            EdmProperty ep = returnType.findProperty(name);
            
            if (ep == null) {
                throw new IllegalArgumentException("unknown property " + name + " for " + returnType.toTypeString());
            }
            props.add(JsonTypeConverter.parse(name, ep.type, event.asEndProperty().getValue()));
        } 
        // TODO support complex type properties
        // else if (event.isStartObject()) {
        //    
        //} 
        else {
            throw new JsonParseException("expecting endproperty, got: " + event.toString());
        }
    }
    
}
