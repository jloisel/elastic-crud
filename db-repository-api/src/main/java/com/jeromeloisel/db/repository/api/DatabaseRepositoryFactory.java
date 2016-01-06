package com.jeromeloisel.db.repository.api;

import com.jeromeloisel.db.entity.Entity;

public interface DatabaseRepositoryFactory {

  <T extends Entity> DatabaseRepository<T> create(Class<T> clazz);
}
