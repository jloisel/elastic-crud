package com.jeromeloisel.db.conversion.jackson;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.testing.NullPointerTester;
import com.jeromeloisel.db.entity.Entity;

@RunWith(MockitoJUnitRunner.class)
public class JacksonJsonDeserializerTest {

  @Mock
  private ObjectMapper mapper;
  @Mock
  private Entity entity;
  @Mock
  private ObjectReader reader;
  
  private JacksonJsonDeserializer<Entity> function;
  
  @Before
  public void before() {
    when(mapper.readerFor(Entity.class)).thenReturn(reader);
    function = new JacksonJsonDeserializer<>(mapper, Entity.class);
  }
  
  @Test
  public void shouldPassNPETesterSerializer() {
    new NullPointerTester().testConstructors(JacksonJsonDeserializer.class, PACKAGE);
  }
  
  @Test(expected=JacksonConversionException.class)
  public void shouldThrow() throws IOException {
    when(reader.readValue("")).thenThrow(new JsonGenerationException(""));
    function.apply("");
  }
}
