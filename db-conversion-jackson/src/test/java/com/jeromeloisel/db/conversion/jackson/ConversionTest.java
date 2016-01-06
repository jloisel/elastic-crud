package com.jeromeloisel.db.conversion.jackson;

import static com.google.common.testing.NullPointerTester.Visibility.PACKAGE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.testing.NullPointerTester;
import com.jeromeloisel.db.conversion.api.JsonDeserializer;
import com.jeromeloisel.db.conversion.api.JsonSerializer;

public class ConversionTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final JsonSerializer<Animal> serializer = new JacksonJsonSerializer<>(mapper);
  private final JsonDeserializer<Animal> deserializer = new JacksonJsonDeserializer<>(mapper, Animal.class);
  
  @Test
  public void shouldBijectivelyConvert() {
    final Animal animal = new Animal("id", "Tiger", "Panthera Tigris", false);
    final String json = serializer.apply(animal);
    final Animal deserialized = deserializer.apply(json);
    assertEquals(animal, deserialized);
  }
  
  @Test
  public void shouldPassNPETesterDeserializer() {
    new NullPointerTester().testConstructors(JacksonJsonDeserializer.class, PACKAGE);
  }
  
  @Test
  public void shouldPassNPETesterSerializer() {
    new NullPointerTester().testConstructors(JacksonJsonSerializer.class, PACKAGE);
  }
}
