package com.jeromeloisel.db.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the index name and the type in which the document is stored.
 * 
 * @author jerome
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Document {

  /**
   * ElasticSearch index name.
   * 
   * @return
   */
  String indexName();
  
  /**
   * Elasticsearch type associated to the annotated bean.
   * 
   * @return
   */
  String type();
}
