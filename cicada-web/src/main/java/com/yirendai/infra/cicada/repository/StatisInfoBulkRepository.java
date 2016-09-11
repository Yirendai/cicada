package com.yirendai.infra.cicada.repository;

import com.yirendai.infra.cicada.entity.StatisInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 针对统计结果数据，批量数据插入.
 * @author zeche
 */
@Service
@Transactional
public class StatisInfoBulkRepository {
  private static final int BATCH_SIZE = 1000;

  @PersistenceContext
  private EntityManager entityManager;

  public <T extends StatisInfo> void saveAll(final Collection<T> entities) {
    if (entities.isEmpty()) {
      return;
    }

    int index = 0;
    for (final T entity : entities) {
      persistOrMerge(entity);
      ++index;
      if (index % BATCH_SIZE == 0 || index == entities.size() - 1) {
        entityManager.flush();
        entityManager.clear();
      }
    }
  }

  private <T extends StatisInfo> T persistOrMerge(final T entity) {
    final T result;
    if (entity.getId() == null) {
      entityManager.persist(entity);
      result = entity;
    } else {
      result = entityManager.merge(entity);
    }
    
    return result;
  }
}
