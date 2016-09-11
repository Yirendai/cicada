package com.yirendai.infra.cicada.util.elastic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 从配置文件加载elastic索引配置信息.
 * @author Zecheng
 */
@Slf4j
@Component
public class IndexConfigLoader {
  public static final String ROOTDIR = "/elastic/";

  public String load(final String type) {
    final String path = ROOTDIR + type + ".settings";

    InputStream in = null;
    JsonNode node = null;
    try {
      in = IndexConfigLoader.class.getResourceAsStream(path);
      node = new ObjectMapper().readTree(in);
      return node.toString();
    } catch (Exception ex) {
      log.error("failed load config from path: {}, error: {}", path, ex);
      throw new RuntimeException("failed load index config from path: " + path);
    } finally {
      try {
        in.close();
      } catch (Exception ex) { // 
      }
    }
  }
}
