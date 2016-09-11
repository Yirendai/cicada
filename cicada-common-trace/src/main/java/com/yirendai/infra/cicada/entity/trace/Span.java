package com.yirendai.infra.cicada.entity.trace;

import com.alibaba.fastjson.annotation.JSONField;
import com.yirendai.infra.cicada.constants.AnnotationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Span implements Serializable, Comparable<Span> {
  private static final long serialVersionUID = -527884689646021836L;

  private String traceId;
  private String id;
  private String parentId;

  private String appName;
  private String serviceName;
  private String methodName;

  @JSONField(serialize = false)
  private int subSpanNum;
  private int durationServer;
  private int durationClient;

  private boolean hasException = false;

  @JSONField(serialize = false)
  private boolean sample;

  private List<Annotation> annotations = new ArrayList<Annotation>();
  private List<BinaryAnnotation> binaryAnnotations = new ArrayList<BinaryAnnotation>();

  @JSONField(serialize = false)
  public boolean isRootSpan() {
    return (parentId == null);
  }

  public void addAnnotation(Annotation anno) {
    annotations.add(anno);
  }

  public void addBinaryAnnotation(BinaryAnnotation anno) {
    binaryAnnotations.add(anno);
  }

  @Override
  public String toString() {
    return "Span{" + "traceId=" + traceId + ", id=" + id + ", parentId=" + parentId + ", serviceName=" + serviceName
        + ", methodName='" + methodName + '\'' + ", annotations=" + annotations + ", binaryAnnotations="
        + binaryAnnotations + '}';
  }

  public int compareTo(Span other) {
    int traceIdResult = traceId.compareTo(other.traceId);
    if (traceIdResult != 0) {
      return traceIdResult;
    }

    String[] thisSpanArr = id.split("\\.");
    String[] otherSpanArr = other.id.split("\\.");

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
      String thisA = thisSpanArr[i];
      String otherA = otherSpanArr[i];

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
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Span)) {
      return false;
    }

    Span that = (Span) obj;
    return this.traceId.equals(that.traceId) && this.id.equals(that.id);
  }

  @JSONField(serialize = false)
  public boolean isConsumerSide() {
    for (Annotation a : getAnnotations()) {
      if (a.getType() == AnnotationType.CLIENT_SEND || a.getType() == AnnotationType.CLIENT_RECEIVE) {
        return true;
      }
    }

    return false;
  }

  @JSONField(serialize = false)
  public Endpoint getEndpoint() {
    if (!annotations.isEmpty()) {
      return annotations.get(0).getEndpoint();
    }

    return null;
  }

  public boolean addException(String className, String methodName, Throwable ex, Endpoint endpoint) {
    this.hasException = true;
    BinaryAnnotation exAnnotation = new BinaryAnnotation();
    exAnnotation.setThrowable(className, methodName, ex);
    exAnnotation.setEndpoint(endpoint);
    addBinaryAnnotation(exAnnotation);
    return true;
  }

}
