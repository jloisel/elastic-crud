package com.jeromeloisel.repository.elasticsearch.starter;

import com.jeromeloisel.db.conversion.api.JsonSerializationFactory;
import com.jeromeloisel.db.repository.api.DatabaseRepositoryFactory;
import com.jeromeloisel.db.repository.elasticsearch.ElasticSearchRepositoryFactory;
import com.jeromeloisel.db.scroll.api.DatabaseScrollingFactory;
import org.elasticsearch.client.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch Repository autoconfiguration.
 * 
 * @author jerome
 *
 */
@Configuration
public class ElasticSearchRepositoryFactoryAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(DatabaseRepositoryFactory.class)
  @ConditionalOnBean(value={Client.class, JsonSerializationFactory.class, DatabaseScrollingFactory.class})
  DatabaseRepositoryFactory databaseRepositoryFactory(
    final Client client,
    final JsonSerializationFactory serialization,
    final DatabaseScrollingFactory factory) {
    return new ElasticSearchRepositoryFactory(serialization, client, factory);
  }
}
