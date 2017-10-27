package com.jeromeloisel.db.repository.elasticsearch;

import com.jeromeloisel.db.conversion.api.JsonSerializationFactory;
import com.jeromeloisel.db.repository.api.DatabaseRepositoryFactory;
import com.jeromeloisel.db.scroll.api.DatabaseScrollingFactory;
import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TestRepositoryConfig {

  @Bean
  DatabaseRepositoryFactory databaseRepositoryFactory(
    final Client client,
    final JsonSerializationFactory serialization,
    final DatabaseScrollingFactory factory) {
    return new ElasticSearchRepositoryFactory(serialization, client, factory);
  }
}
