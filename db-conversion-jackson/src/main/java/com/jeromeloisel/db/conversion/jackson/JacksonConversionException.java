package com.jeromeloisel.db.conversion.jackson;

import static java.util.Objects.requireNonNull;

class JacksonConversionException extends RuntimeException {
  private static final long serialVersionUID = -4917703394134871581L;

  JacksonConversionException(final String message, final Throwable e) {
    super(requireNonNull(message), requireNonNull(e));
  }
}
