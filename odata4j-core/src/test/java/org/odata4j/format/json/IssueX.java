package org.odata4j.format.json;

import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;
import org.odata4j.core.OComplexObject;
import org.odata4j.format.Entry;
import org.odata4j.format.FormatParser;
import org.odata4j.format.FormatParserFactory;
import org.odata4j.format.FormatType;
import org.odata4j.format.json.JsonFormatParser;
import org.odata4j.format.json.JsonStreamReaderFactory;
import org.odata4j.format.json.JsonStreamReaderFactory.JsonStreamReader;
import org.odata4j.format.json.JsonStreamReaderFactory.JsonStreamReader.JsonEvent;
import org.odata4j.format.json.JsonStreamReaderFactory.JsonStreamTokenizer;
import org.odata4j.format.json.JsonStreamReaderFactory.JsonStreamTokenizer.JsonToken;
import org.odata4j.format.json.JsonStreamReaderFactory.JsonStreamTokenizer.JsonTokenType;


/**
 * JSON tokenizer issue
 */
public class IssueX {
    @Test
    public void issueX() {

        String j = "{\n"
                + "\"d\" : {\n"
                + "\"OrderID\" : 33, \"ProductID\" : 44\n"
                + "}\n"
                + "}";

        // issue: the tokenizer emits a NUMBER token for the ProductID value
        //        with value = '44\n' instead of '44'  The tokenizer checks number
        //        validity with a NumberFormatter, the JsonTypeConverter.parse
        //        uses Integer.parse for Int32 which pukes on the '\n'
        
        StringReader sr = new StringReader(j);
        JsonStreamTokenizer t = JsonStreamReaderFactory.createJsonStreamTokenizer(sr);
        JsonToken tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.LEFT_CURLY_BRACKET);
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.STRING);
        Assert.assertTrue(tok.value.equals("d"));
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.COLON);
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.LEFT_CURLY_BRACKET);
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.STRING);
        Assert.assertTrue(tok.value.equals("OrderID"));
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.COLON);
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.NUMBER);
        Assert.assertTrue(tok.value.equals("33"));
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.COMMA);
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.STRING);
        Assert.assertTrue(tok.value.equals("ProductID"));
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.COLON);
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.NUMBER);
        Assert.assertTrue(tok.value.equals("44"));
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.RIGHT_CURLY_BRACKET);
        tok = t.nextToken();
        Assert.assertTrue(tok.type == JsonTokenType.RIGHT_CURLY_BRACKET);


    }

    @Test
    public void issueX0() {

        String j = "{\n"
                + "\"d\" : {\n"
                + "\"OrderID\" : 33, \"ProductID\" : 44\n"
                + "}\n"
                + "}";

        StringReader sr = new StringReader(j);
        JsonStreamReader r = JsonStreamReaderFactory.createJsonStreamReader(sr);
        Assert.assertTrue(r.hasNext());
        Assert.assertTrue(r.nextEvent().isStartObject());
        
        Assert.assertTrue(r.hasNext());
        Assert.assertTrue(r.nextEvent().isStartProperty());
        
        Assert.assertTrue(r.hasNext());
        Assert.assertTrue(r.nextEvent().isStartObject());
        
        Assert.assertTrue(r.hasNext());
        Assert.assertTrue(r.nextEvent().isStartProperty());
        
        Assert.assertTrue(r.hasNext());
        JsonEvent e = r.nextEvent();
        Assert.assertTrue(e.isEndProperty());
        Assert.assertTrue(e.asEndProperty().getValue().equals("33"));
        
        Assert.assertTrue(r.hasNext());
        Assert.assertTrue(r.nextEvent().isStartProperty());
        
        Assert.assertTrue(r.hasNext());
        e = r.nextEvent();
        Assert.assertTrue(e.isEndProperty());
        Assert.assertTrue(e.asEndProperty().getValue().equals("44"));
        
        Assert.assertTrue(r.hasNext());
        Assert.assertTrue(r.nextEvent().isEndObject());
        
        Assert.assertTrue(r.hasNext());
        e = r.nextEvent();
        Assert.assertTrue(e.isEndProperty());
        
        Assert.assertTrue(r.hasNext());
        Assert.assertTrue(r.nextEvent().isEndObject());   
    }
    
    @Test
    public void issueX1() {
        FormatParser<?> fp = FormatParserFactory.getParser(OComplexObject.class,
            FormatType.JSON, null);

        JsonFormatParser p = (JsonFormatParser) fp;

        final String name = "blat";
        JsonEvent e = new JsonStartPropertyEventImpl() {

            @Override
            public String getName() {
                return name;
            }
        };

        try {
            p.ensureStartProperty(e, "foobar");

            // shouldn't get here but ensureStartProperty has a logic bug.
            Assert.assertFalse(1 == 1);

        } catch(Exception ex) {
            
        }
    }
}
