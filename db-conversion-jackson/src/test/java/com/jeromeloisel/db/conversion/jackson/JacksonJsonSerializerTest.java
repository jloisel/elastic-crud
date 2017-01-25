package com.jeromeloisel.db.conversion.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.testing.NullPointerTester;
import com.jeromeloisel.db.entity.Entity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JacksonJsonSerializerTest {

  @Mock
  private ObjectMapper mapper;
  @Mock
  private Entity entity;
  
  private JacksonJsonSerializer<Entity> serializer;
  
  @Before
  public void before() {
    serializer = new JacksonJsonSerializer<>(mapper);
  }
  
  @Test
  public void shouldPassNPETesterSerializer() {
    new NullPointerTester().testConstructors(JacksonJsonSerializer.class, PACKAGE);
  }
  
  @Test(expected=JacksonConversionException.class)
  public void shouldThrow() throws JsonProcessingException {
    when(mapper.writeValueAsString(entity)).thenThrow(mock(JsonProcessingException.class));
    serializer.apply(entity);
  }
}
