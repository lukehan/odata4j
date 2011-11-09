package org.odata4j.core;

import java.util.UUID;

/**
 * A custom Guid class is necessary to interop with .net Guid strings incompatible with {@link UUID}.
 * <p>Guids are equal if their string representations are equal.</p>
 */
public class Guid {

  private final String value;

  private Guid(String value) {
    this.value = value;
  }

  /**
   * Return a Guid for a given string.
   *
   * @param value  the guid's string representation.
   * @return a new Guid
   */
  public static Guid fromString(String value) {
    return new Guid(value);
  }

  public static Guid fromUUID(UUID uuid) {
    return new Guid(uuid.toString());
  }

  /**
   * Generate a new Guid.
   *
   * @return a new Guid
   */
  public static Guid randomGuid() {
    return new Guid(UUID.randomUUID().toString());
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return (other instanceof Guid) && ((Guid) other).value.equals(value);
  }

  @Override
  public String toString() {
    return value;
  }

}
