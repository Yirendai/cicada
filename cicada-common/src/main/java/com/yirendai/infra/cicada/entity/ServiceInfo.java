package com.yirendai.infra.cicada.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.yirendai.infra.cicada.constants.DateTimeFormats;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "service_info")
public class ServiceInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private Integer appId;
  private String serviceName;

  @OneToMany(targetEntity = MethodInfo.class, cascade = CascadeType.REMOVE, mappedBy = "serviceId",
      orphanRemoval = true)
  @JsonIgnore
  private List<MethodInfo> methods;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormats.FULL_TIME_ENGLISH,
      timezone = DateTimeFormats.CHINESE_TIME_ZONE)
  @JsonSerialize(using = DateTimeSerializer.class)
  @CreatedDate
  DateTime registerTime = DateTime.now();
}
