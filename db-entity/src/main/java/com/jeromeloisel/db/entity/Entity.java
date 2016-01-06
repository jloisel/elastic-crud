package com.jeromeloisel.db.entity;

/**
 * Representation of an immutable entity.
 * 
 * @author jerome
 *
 */
public interface Entity {

  /**
   * Inject this field into the bean by using the {@link Id} annotation.
   * 
   * @return id of the entity
   */
  String getId();
  
  /**
   * Returns a copy of the entity with this {@code id} set.
   * 
   * @param id new id to set
   * @return
   */
  Entity withId(String id);
}
