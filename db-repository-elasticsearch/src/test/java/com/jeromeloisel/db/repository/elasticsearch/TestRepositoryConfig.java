package com.jeromeloisel.db.repository.elasticsearch;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jeromeloisel.db.conversion.api.JsonSerializationFactory;
import com.jeromeloisel.db.repository.api.DatabaseRepositoryFactory;
import com.jeromeloisel.db.repository.elasticsearch.ElasticSearchRepositoryFactory;

@Configuration
class TestRepositoryConfig {

  @Bean
  @Autowired
  DatabaseRepositoryFactory databaseRepositoryFactory(
      final Client client,
      final JsonSerializationFactory serialization) {
    return new ElasticSearchRepositoryFactory(serialization, client);
  }
}
