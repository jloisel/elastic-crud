package com.jeromeloisel.db.integration.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeromeloisel.db.conversion.api.JsonSerializationFactory;
import com.jeromeloisel.db.conversion.jackson.JacksonSerializationFactory;

@Configuration
class TestConversionConfig {

  @Bean
  ObjectMapper objectMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    return mapper;
  }
  
  @Bean
  @Autowired
  JsonSerializationFactory jsonSerializationFactory(final ObjectMapper mapper) {
    return new JacksonSerializationFactory(mapper);
  }
}
