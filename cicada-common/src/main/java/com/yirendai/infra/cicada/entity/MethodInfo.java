package com.yirendai.infra.cicada.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.yirendai.infra.cicada.constants.DateTimeFormats;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "method_info")
public class MethodInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private int serviceId;
  private String methodName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormats.FULL_TIME_ENGLISH,
      timezone = DateTimeFormats.CHINESE_TIME_ZONE)
  @JsonSerialize(using = DateTimeSerializer.class)
  @CreatedDate
  DateTime registerTime = DateTime.now();
}
