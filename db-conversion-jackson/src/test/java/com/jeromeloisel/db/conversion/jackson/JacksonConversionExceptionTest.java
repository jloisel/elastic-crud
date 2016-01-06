package com.jeromeloisel.db.conversion.jackson;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.google.common.testing.NullPointerTester;

public class JacksonConversionExceptionTest {

  @Test
  public void shouldPassNPETester() {
    new NullPointerTester().testConstructors(JacksonConversionException.class, PACKAGE);
  }
  
  @Test
  public void shouldThrow() {
    final Exception e = new JacksonConversionException("", new IOException());
    assertNotNull(e);
  }
}
