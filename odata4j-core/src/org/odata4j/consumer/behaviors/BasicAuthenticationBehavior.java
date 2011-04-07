package org.odata4j.consumer.behaviors;

import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.core.OClientBehavior;
import org.odata4j.format.Entry;
import org.odata4j.repack.org.apache.commons.codec.binary.Base64;

import com.sun.jersey.api.client.config.ClientConfig;

public class BasicAuthenticationBehavior implements OClientBehavior {

    private final String user;
    private final String password;

    public BasicAuthenticationBehavior(String user, String password) {
        this.user = user;
        this.password = password;
    }
    
    @Override
    public void modify(ClientConfig clientConfig) { }

    @Override
    public <E extends Entry> ODataClientRequest transform(ODataClientRequest request) {
        String userPassword = user + ":" + password;
        String encoded = Base64.encodeBase64String(userPassword.getBytes());
        encoded = encoded.replaceAll("\r\n?", "");
        return request.header("Authorization", "Basic " + encoded);

    }

}