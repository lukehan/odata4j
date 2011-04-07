package org.odata4j.format.json;

import java.io.IOException;
import java.io.Writer;

public class JsonWriter {

    private final Writer writer;
    public JsonWriter(Writer writer){
        this.writer = writer;
    }
    public void startCallback(String functionName) {
        try {
            writer.write(encode(functionName)+"(");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void endCallback() {
        try {
            writer.write(");");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
    public void startObject() {
        try {
            writer.write("{\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void endObject() {
        try {
            writer.write("\n}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeName(String name) {
        try {
            writer.write("\"" + encode(name) + "\" : ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
  
    public void startArray() {
        try {
            writer.write("[\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void endArray() {
        try {
            writer.write("\n]");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeSeparator() {
        try {
            writer.write(", ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeString(String name) {
        try {
            writer.write("\"" + encode(name) + "\"");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeNull() {
        try {
            writer.write("null");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeNumber(int value) {
        try {
            writer.write(Integer.toString(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeBoolean(boolean value) {
        try {
            writer.write(value?"true":"false");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeRaw(String value) {
        try {
            writer.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    
    
    private String encode(String unencoded){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<unencoded.length();i++){
            char c = unencoded.charAt(i);
            if (c=='\\')
                sb.append("\\\\");
            else if (c=='"')
                sb.append("\\\"");
            else if (c=='\n')
                sb.append("\\n");
            else if (c=='\r')
                sb.append("\\r");
            else if (c=='\f')
                sb.append("\\f");
            else if (c=='\b')
                sb.append("\\b");
            else if (c=='\t')
                sb.append("\\t");
            
            else
                sb.append(c);
        }
        return sb.toString();
    }
    
    
    
   
}
