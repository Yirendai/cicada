package com.yirendai.infra.cicada.entity;

import com.yirendai.infra.cicada.entity.model.AnnotationModel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
public class SpanEntity implements Serializable, Comparable<SpanEntity> {
  private static final long serialVersionUID = -527884689646021836L;

  private String traceId;
  private String id;
  private String parentId;

  private String appName;
  private String serviceName;
  private String methodName;

  private int durationServer;
  private int durationClient;

  private boolean hasException;
  private List<AnnotationModel> annotations;
  
  public SpanEntity() {
    this.hasException = false;
    this.annotations = new LinkedList<AnnotationModel>();
  }

  public int compareTo(final SpanEntity other) {
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
      if (!(obj instanceof SpanEntity)) {
        equals = false;
      } else {
        final SpanEntity that = (SpanEntity) obj;
        equals = traceId.equals(that.traceId) && id.equals(that.id); 
      }
    }
    
    return equals;
  }
}
