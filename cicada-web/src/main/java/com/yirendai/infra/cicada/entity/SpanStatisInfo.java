package com.yirendai.infra.cicada.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.yirendai.infra.cicada.constants.DateTimeFormats;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "span_statis_info")
@EqualsAndHashCode(callSuper = false)
public class SpanStatisInfo extends StatisInfo {
  private static final long serialVersionUID = 3657478520301707196L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;


  private int appId;
  private int serviceId;
  private int methodId;

  private long count;
  private double failureRate;

  private int line95Duration;
  private int line999Duration;

  private double avgDuration;
  private int minDuration;
  private int maxDuration;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormats.FULL_TIME_ENGLISH,
      timezone = DateTimeFormats.CHINESE_TIME_ZONE)
  @JsonSerialize(using = DateTimeSerializer.class)
  DateTime statisTime;
}
