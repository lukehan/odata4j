package org.odata4j.consumer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;

/**
 * Client-side extension mechanism - provides a Jersey {@link Client} implementation given a configuration.
 */
public interface ClientFactory {

  /**
   * Creates a new Jersey client.
   *
   * @param clientConfig  the Jersey client api configuration
   */
  Client createClient(ClientConfig clientConfig);

}
