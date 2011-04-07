package org.odata4j.core;

public class ODataConstants {

	public static final String APPLICATION_ATOM_XML = "application/atom+xml";
    public static final String APPLICATION_ATOM_XML_CHARSET_UTF8 = APPLICATION_ATOM_XML + ";charset=utf-8";
    public static final String APPLICATION_XML_CHARSET_UTF8 = "application/xml;charset=utf-8";
    public static final String TEXT_JAVASCRIPT_CHARSET_UTF8 = "text/javascript;charset=utf-8";
    public static final String APPLICATION_JAVASCRIPT = "application/json";
    public static final String APPLICATION_JAVASCRIPT_CHARSET_UTF8 = APPLICATION_JAVASCRIPT + ";charset=utf-8";
    
    
    public static final ODataVersion DATA_SERVICE_VERSION = ODataVersion.V1;
    public static final String DATA_SERVICE_VERSION_HEADER = DATA_SERVICE_VERSION.asString;

    public static class Headers {
        public static final String X_HTTP_METHOD = "X-HTTP-METHOD";
        public static final String DATA_SERVICE_VERSION = "DataServiceVersion";
        public static final String CONTENT_TYPE = "Content-Type";
    }

}
