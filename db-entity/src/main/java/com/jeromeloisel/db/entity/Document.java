package com.jeromeloisel.db.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the index name and the type in which the document is stored.
 * 
 * @author jerome
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
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
