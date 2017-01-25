package com.jeromeloisel.db.repository.api;

import com.jeromeloisel.db.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * Basic Crud Database repository.
 * 
 * @author jerome
 *
 * @param <T> type of entity to store
 */
public interface DatabaseRepository<T extends Entity> {
  
  /**
   * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
   * entity instance completely.
   * 
   * @param entity
   * @return the saved entity
   */
  T save(T entity);

  /**
   * Saves all given entities.
   * 
   * @param entities
   * @return the saved entities
   * @throws IllegalArgumentException in case the given entity is (@literal null}.
   */
  List<T> saveAll(List<T> entities);

  /**
   * Retrieves an entity by its id.
   * 
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal null} if none found
   * @throws IllegalArgumentException if {@code id} is {@literal null}
   */
  Optional<T> findOne(String id);

  /**
   * Returns whether an entity with the given id exists.
   * 
   * @param id must not be {@literal null}.
   * @return true if an entity with the given id exists, {@literal false} otherwise
   * @throws IllegalArgumentException if {@code id} is {@literal null}
   */
  boolean exists(String id);

  /**
   * Returns whether an entity exists.
   * 
   * @param entity must not be {@code null}
   * @return true if an entity exists, {@literal false} otherwise
   * @throws IllegalArgumentException if {@code id} is {@literal null}
   */
  boolean exists(T entity);

  /**
   * Returns all instances of the type with the given IDs.
   * 
   * @param ids
   * @return
   */
  List<T> findAll(List<String> ids);

  /**
   * Deletes the entity with the given id.
   * 
   * @param id must not be {@literal null}.
   * @return the deleted entity id
   * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
   */
  String delete(String id);

  /**
   * Deletes a given entity.
   * 
   * @param entity
   * @return the deleted entity id
   * @throws IllegalArgumentException in case the given entity is (@literal null}.
   */
  String delete(T entity);

  /**
   * Deletes all the given entities at once.
   * 
   * @param entities
   */
  List<String> deleteAll(List<T> entities);
  
  /**
   * Deletes all the given entities at once.
   * 
   * @param ids
   */
  List<String> deleteAllIds(List<String> ids);
}
