package com.jeromeloisel.db.conversion.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.testing.NullPointerTester;
import com.jeromeloisel.db.entity.Entity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    when(reader.readValue("")).thenThrow(mock(JsonProcessingException.class));
    function.apply("");
  }
}
