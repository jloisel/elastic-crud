package com.jeromeloisel.db.conversion.jackson;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.testing.NullPointerTester;

@RunWith(MockitoJUnitRunner.class)
public class JacksonSerializationFactoryTest {
  
  @Mock
  private ObjectMapper mapper;

  private JacksonSerializationFactory factory;
  
  @Before
  public void before() {
    factory = new JacksonSerializationFactory(mapper);
  }
  
  @Test
  public void shouldCreateDeserializer() {
    assertEquals(JacksonJsonDeserializer.class, factory.deserializer(Animal.class).getClass());
  }
  
  @Test
  public void shouldCreateSerializer() {
    assertEquals(JacksonJsonSerializer.class, factory.serializer(Animal.class).getClass());
  }
  
  @Test
  public void shouldPassNPETesterSerializer() {
    new NullPointerTester().testConstructors(JacksonSerializationFactory.class, PACKAGE);
  }
}
