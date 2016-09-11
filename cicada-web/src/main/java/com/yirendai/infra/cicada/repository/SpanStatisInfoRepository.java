package com.yirendai.infra.cicada.repository;

import com.yirendai.infra.cicada.entity.SpanStatisInfo;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import javax.transaction.Transactional;

@Transactional
public interface SpanStatisInfoRepository extends JpaRepository<SpanStatisInfo, Integer> {
  @Query("select t from SpanStatisInfo t where "
      + "t.methodId=:methodId "
      + "and t.statisTime >= :beginTime "
      + "and t.statisTime < :endTime "
      + "order by t.id desc")
  Page<SpanStatisInfo> fetchPage(@Param("methodId") int methodId, 
      @Param("beginTime") DateTime beginTime,
      @Param("endTime") DateTime endTime, Pageable pageable);

  @Query("select t from SpanStatisInfo t where "
      + "t.methodId=:methodId "
      + "and t.statisTime >= :beginTime "
      + "and t.statisTime < :endTime "
      + "order by t.statisTime asc")
  List<SpanStatisInfo> findAllByDuration(@Param("methodId") int methodId, 
      @Param("beginTime") DateTime beginTime,
      @Param("endTime") DateTime endTime);

  /**
   * 删除超过保存期限的统计数据.
   */
  @Modifying
  @Query("delete from SpanStatisInfo t where t.statisTime < :expireTime")
  void cleanExpireInfos(@Param("expireTime") DateTime expireTime);
}
