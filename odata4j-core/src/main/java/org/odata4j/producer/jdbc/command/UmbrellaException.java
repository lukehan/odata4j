package org.odata4j.producer.jdbc.command;

import java.util.Collection;

public class UmbrellaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final Collection<Exception> exceptions;

  public UmbrellaException(Collection<Exception> exceptions) {
    this.exceptions = exceptions;
  }

  public Collection<Exception> getExceptions() {
    return exceptions;
  }

}
