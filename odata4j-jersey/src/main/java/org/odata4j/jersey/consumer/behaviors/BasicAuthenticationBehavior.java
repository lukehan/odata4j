package org.odata4j.jersey.consumer.behaviors;

import org.odata4j.jersey.consumer.ODataJerseyClientRequest;
import org.odata4j.repack.org.apache.commons.codec.binary.Base64;

public class BasicAuthenticationBehavior extends BaseClientBehavior {

  private final String user;
  private final String password;

  public BasicAuthenticationBehavior(String user, String password) {
    this.user = user;
    this.password = password;
  }

  @Override
  public ODataJerseyClientRequest transform(ODataJerseyClientRequest request) {
    String userPassword = user + ":" + password;
    String encoded = Base64.encodeBase64String(userPassword.getBytes());
    encoded = encoded.replaceAll("\r\n?", "");
    return request.header("Authorization", "Basic " + encoded);

  }

}