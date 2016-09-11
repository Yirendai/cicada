package com.yirendai.infra.cicada.entity.model;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SpanModel implements Serializable, Comparable<SpanModel> {

  private static final long serialVersionUID = 6055130304371692968L;

  private String traceId;
  private String id;
  private String parentId;

  private int appId;
  private int serviceId;
  private int methodId;

  private long startTime;
  private int durationServer;
  private boolean hasException;

  // 用于分布式计算，任务分片
  private int sliceNo;
  
  public SpanModel() {
    this.hasException = false;
    this.sliceNo = 0;
  }

  @JSONField(serialize = false)
  public boolean isRootSpan() {
    return parentId == null;
  }

  public int compareTo(final SpanModel other) {
    final String[] thisSpanArr = id.split("\\.");
    final String[] otherSpanArr = other.id.split("\\.");

    int shortLength = thisSpanArr.length;
    if (thisSpanArr.length > otherSpanArr.length) {
      shortLength = otherSpanArr.length;
      if (this.id.startsWith(other.id)) {
        return 1;
      }
    } else if (thisSpanArr.length < otherSpanArr.length) {
      if (other.id.startsWith(this.id)) {
        return -1;
      }
    }

    for (int i = 0; i < shortLength; i++) {
      final String thisA = thisSpanArr[i];
      final String otherA = otherSpanArr[i];

      if (!thisA.equals(otherA)) {
        return (Integer.parseInt(thisA) - Integer.parseInt(otherA));
      }
    }

    return 0;
  }

  public void calcSliceNo(final int range) {
    sliceNo = 17;
    sliceNo = sliceNo * 31 + appId;
    sliceNo = sliceNo * 31 + serviceId;
    sliceNo = sliceNo * 31 + methodId;

    sliceNo = Math.abs(sliceNo % range);
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = result * 31 + id.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    final boolean equals;
    if (this == obj) {
      equals = true;
    } else {
      if (!(obj instanceof SpanModel)) {
        equals = false;
      } else {
        final SpanModel that = (SpanModel) obj;
        equals = traceId.equals(that.traceId) && id.equals(that.id);
      }
    }

    return equals;
  }
}
