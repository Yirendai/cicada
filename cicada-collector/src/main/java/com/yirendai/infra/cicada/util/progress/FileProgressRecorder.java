package com.yirendai.infra.cicada.util.progress;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yirendai.infra.cicada.config.CicadaCollectorProps;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @brief 使用文件存取当前进度.
 * @author Zecheng
 */
@Slf4j
@Component
public class FileProgressRecorder implements ProgressRecorder {
  @Autowired
  private CicadaCollectorProps props;

  private RandomAccessFile file;

  private void mkdirs() {
    final String dirStr = StringUtils.substringBeforeLast(props.getProgressFilePath(), "/");
    final File dir = new File(dirStr);
    if (!dir.exists()) {
      dir.mkdirs();
    }
  }

  @Override
  public void set(final Progress record) {
    final String path = props.getProgressFilePath();
    try {
      mkdirs();
      file = new RandomAccessFile(path, "rw");
      final String recordStr = JSON.toJSONString(record);
      file.setLength(0L);
      file.writeBytes(recordStr);
    } catch (FileNotFoundException ex) {
      log.error("file not exists: {}, error: {}", path, ex);
    } catch (IOException ex) {
      log.error("failed record to file: {}, error: {}", path, ex);
    } finally {
      if (file != null) {
        try {
          file.close();
        } catch (IOException ex) {
          log.error("failed close file: {}, error: {}", path, ex);
        }
      }
    }
  }

  @Override
  public Progress get() {

    String line = null;
    Progress record = null;
    final String path = props.getProgressFilePath();

    try {
      if (exists(path)) {
        file = new RandomAccessFile(path, "rw");
        line = file.readLine();
        if (!StringUtils.isBlank(line.trim())) {
          record = JSON.parseObject(line, Progress.class);
        }
      }
    } catch (FileNotFoundException ex) {
      log.error("file not exists: {}", path);
    } catch (IOException ex) {
      log.error("failed read record from file: {}, error: {}", path, ex);
    } catch (JSONException ex) {
      log.error("failed parse json str: {}, error: {}", line, ex);
    } finally {
      if (file != null) {
        try {
          file.close();
        } catch (IOException ex) {
          log.error("failed close file: ", path);
        }
      }
    }

    return record;
  }

  private boolean exists(final String path) {
    final File ff = new File(path);
    return ff.exists();
  }
}
