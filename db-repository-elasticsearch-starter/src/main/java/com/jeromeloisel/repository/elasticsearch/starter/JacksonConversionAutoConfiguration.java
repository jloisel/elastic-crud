package com.jeromeloisel.repository.elasticsearch.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeromeloisel.db.conversion.api.JsonSerializationFactory;
import com.jeromeloisel.db.conversion.jackson.JacksonSerializationFactory;

@Configuration
@ConditionalOnClass(ObjectMapper.class)
class JacksonConversionAutoConfiguration {

  @Bean
  @Autowired
  @ConditionalOnBean(ObjectMapper.class)
  @ConditionalOnMissingBean(JsonSerializationFactory.class)
  JsonSerializationFactory jsonSerializationFactory(final ObjectMapper mapper) {
    return new JacksonSerializationFactory(mapper);
  }
}
