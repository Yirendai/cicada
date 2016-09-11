package com.yirendai.infra.cicada.util;

import com.yirendai.infra.cicada.configure.CicadaWebProps;
import com.yirendai.infra.cicada.constants.DateTimeFormats;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@Component
public class ElasticIndexManager {
  private static final String INDEX_CAT_CHARS = "_";
  private static final int VALID_INDEX_NAMEARR_LEN = 2;

  @Autowired
  private CicadaWebProps props;

  @Autowired
  private TransportClient client;

  /**
   * 删除elastic search中超过保存期限的索引.
   */
  public void removeExpireIndice() {
    final List<String> expireIndice = getExpireIndice();
    for (final String index : expireIndice) {
      client.admin().indices().delete(new DeleteIndexRequest(index)).actionGet();
    }
  }

  private List<String> getExpireIndice() {
    final List<String> allIndice = getAllIndice();
    final long expireTimestamp = DateTime.now().minusDays(props.getEsIndexRetentionDays() + 1).getMillis();

    final ListIterator<String> iter = allIndice.listIterator();
    while (iter.hasNext()) {
      final String indexName = iter.next();
      final String[] nameArr = indexName.split(INDEX_CAT_CHARS);
      if (nameArr.length != VALID_INDEX_NAMEARR_LEN) {
        iter.remove();
        continue;
      }

      final long createTimestamp = DateTime.parse(nameArr[1], //
          DateTimeFormats.FULL_DATE_ENGLISH_FORMATTER).getMillis();
      if (createTimestamp >= expireTimestamp) {
        iter.remove();
      }
    }

    return allIndice;
  }

  private List<String> getAllIndice() {
    final List<String> indice = new LinkedList<String>();
    final GetIndexResponse resp = client.admin().indices().getIndex(new GetIndexRequest()).actionGet();

    for (final String indexName : resp.indices()) {
      if (indexName.startsWith(props.getEsSpanIndexPrefix()) //
          || indexName.startsWith(props.getEsAnnotationIndexPrefix())) {
        indice.add(indexName);
      }
    }

    return indice;
  }
}
