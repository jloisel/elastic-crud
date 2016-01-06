package com.jeromeloisel.repository.elasticsearch.starter;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jeromeloisel.db.conversion.api.JsonSerializationFactory;
import com.jeromeloisel.db.repository.api.DatabaseRepositoryFactory;
import com.jeromeloisel.db.repository.elasticsearch.ElasticSearchRepositoryFactory;

@Configuration
class ElasticSearchRepositoryFactoryAutoConfiguration {

  @Bean
  @Autowired
  @ConditionalOnMissingBean(DatabaseRepositoryFactory.class)
  @ConditionalOnBean(value={Client.class, JsonSerializationFactory.class})
  DatabaseRepositoryFactory databaseRepositoryFactory(
      final Client client,
      final JsonSerializationFactory serialization) {
    return new ElasticSearchRepositoryFactory(serialization, client);
  }
}
