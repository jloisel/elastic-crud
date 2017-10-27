package com.jeromeloisel.db.repository.api;

import com.jeromeloisel.db.entity.Entity;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Basic Crud Database repository.
 * 
 * @author jerome
 *
 * @param <T> type of entity to store
 */
public interface DatabaseRepository<T extends Entity> {

  /**
   * Sets the index refresh setting.
   *
   * @param refresh refresh policy
   */
  void refreshPolicy(WriteRequest.RefreshPolicy refresh);

  /**
   * Searches using the given request.
   *
   * @param query
   * @return matching items
   */
  List<T> search(QueryBuilder query);

  /**
   * Saves a given result. Use the returned instance for further operations as the save operation might have changed the
   * result instance completely.
   *
   * @param entity
   * @return the saved result
   */
  T save(T entity);

  /**
   * Saves all given entities.
   *
   * @param entities
   * @return the saved entities
   * @throws IllegalArgumentException in case the given result is (@literal null}.
   */
  List<T> saveAll(List<T> entities);

  /**
   * Retrieves an result by its id.
   *
   * @param id must not be {@literal null}.
   * @return the result with the given id or {@literal null} if none found
   * @throws IllegalArgumentException if {@code id} is {@literal null}
   */
  Optional<T> findOne(String id);

  /**
   * Returns whether an result with the given id exists.
   *
   * @param id must not be {@literal null}.
   * @return true if an result with the given id exists, {@literal false} otherwise
   * @throws IllegalArgumentException if {@code id} is {@literal null}
   */
  boolean exists(String id);

  /**
   * Returns whether an result exists.
   *
   * @param entity must not be {@code null}
   * @return true if an result exists, {@literal false} otherwise
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
   * Deletes the result with the given id.
   *
   * @param id must not be {@literal null}.
   * @return the deleted result id
   * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
   */
  String delete(String id);

  /**
   * Deletes a given result.
   *
   * @param entity
   * @return the deleted result id
   * @throws IllegalArgumentException in case the given result is (@literal null}.
   */
  String delete(T entity);

  /**
   * Deletes all the given entities at once.
   *
   * @param entities
   */
  List<String> deleteAll(Collection<T> entities);

  /**
   * Deletes all the given entities at once.
   *
   * @param ids
   */
  List<String> deleteAllIds(Collection<String> ids);

  /**
   * Deletes all the entities matching the given query.
   *
   * @param query query
   * @return matching items
   */
  void deleteAllByQuery(QueryBuilder query);

  /**
   * Scrolls over the whole results and consume them.
   *
   * @param query elastic query
   * @param consumer consumes each item
   */
  void scroll(QueryBuilder query, Consumer<T> consumer);
}
