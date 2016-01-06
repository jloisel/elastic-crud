package com.jeromeloisel.repository.elasticsearch.starter;

import static org.mockito.Mockito.mock;

import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
class TestConfig {

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
  
  @Bean
  Client client() {
    return mock(Client.class);
  }
}
