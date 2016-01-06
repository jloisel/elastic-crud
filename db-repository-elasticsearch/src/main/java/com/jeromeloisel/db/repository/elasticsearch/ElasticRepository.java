package com.jeromeloisel.db.repository.elasticsearch;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;

import com.jeromeloisel.db.entity.Entity;
import com.jeromeloisel.db.repository.api.DatabaseRepository;

public interface ElasticRepository<T extends Entity> extends DatabaseRepository<T> {

  
  /**
   * Searches using the given request.
   * 
   * @param search search request
   * @return matching items
   */
  List<T> search(QueryBuilder query);
}
