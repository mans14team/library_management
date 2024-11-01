package com.example.library_management.domain.book.cache;

import java.util.List;
import java.util.Set;

public interface EntityService<T> {
    T getEntityFromCache(String name);
    T createEntity(String name);
    T updateCache(T entity);
    List<T> saveEntities(Set<T> entities);
    List<T> getAllEntitiesFromDatabase();
    // 예시 EntityService
}
