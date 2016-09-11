package com.yirendai.infra.cicada.repository;

import com.yirendai.infra.cicada.config.CicadaCollectorProps;
import com.yirendai.infra.cicada.constants.DateTimeFormats;
import com.yirendai.infra.cicada.util.BufferedFile;
import com.yirendai.infra.cicada.util.progress.Progress;
import com.yirendai.infra.cicada.util.progress.ProgressRecorder;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * trace日志读取工具.
 * @author Zecheng
 */
@Slf4j
@Component
public class LogReader {
  private static final String FILENAME_PREFIX = "record.log";
  private static final int LINE_COLUMN_SIZE = 2;
  private static final int LINES_LIMIT = 10000;
  
  private volatile boolean isInterrupt = false;

  @Autowired
  private CicadaCollectorProps props;

  @Autowired
  private ProgressRecorder recorder;

  public void finish() {
    isInterrupt = true;
  }

  // 从rootDir目录下的文件中tail数据，操作必须串行
  public synchronized List<String> read() {
    final List<String> results = new LinkedList<String>();

    final DateTime curTime = DateTime.now();
    Progress progress = recorder.get();

    // 获取所有自上次读取以来到当前时间为止，合法（未读取或未读取完）的文件列表
    final List<String> validFilePaths = getAllValidFiles(progress, curTime);

    boolean isCurDateLog = false;
    final String curDateStr = curTime.toString(DateTimeFormats.FULL_DATE_ENGLISH);
    for (int i = 0; i < validFilePaths.size(); ++i) {
      // 判断是否为当天的日志
      final String logDateStr = StringUtils.substringAfterLast(validFilePaths.get(i), ".");
      if (logDateStr.equals(curDateStr)) {
        isCurDateLog = true;
      }

      results.addAll(getLines(validFilePaths.get(i), progress, isCurDateLog));
      progress = null;
    }

    return results;
  }

  private List<String> getLines(final String path, final Progress progress, final boolean isCurDateLog) {
    // 打开文件并定位到上次读取的位置（如果有）
    BufferedFile file = BufferedFile.open(path);
    try {
      if (progress != null) {
        file.seek(progress.getOffset());
      }
    } catch (IOException ex) {
      log.error("failed seek file {} to offset {}, error {}", path, progress.getOffset(), ex);
    }

    final List<String> lines = new LinkedList<String>();
    // 如果读取的日志文件不是当天的文件，
    // 从文件的当前偏移把后面所有的日志读取出来
    if (!isCurDateLog) {
      try {
        for (final String line : file.getAllLines()) {
          final String[] arr = line.split("\t");
          if (arr.length != LINE_COLUMN_SIZE) {
            continue;
          }
          lines.add(arr[1]);
        }

        // 记录进度
        final Progress newProgress = new Progress(path, file.tell());
        recorder.set(newProgress);

      } catch (IOException ex) {
        log.error("failed get all lines from file {}, error {}", path, ex);
      } finally {
        file.close();
      }

      return lines;
    }

    // 如果是文件尾，等待 1000ms 再继续读
    try {
      if (file.feof()) {
        TimeUnit.MILLISECONDS.sleep(1000);
      }
    } catch (IOException ex) {
      log.error("failed judge if reached EOF, file {}, error {}", path, ex);
    } catch (InterruptedException ex) {
      log.error("failed sleep interrupted, error{}", ex);
    }

    String line = null;
    long curOffset = 0;
    int count = 0;
    try {
      while (true) {
        // 如果设置了isInterrupt标识，表示进程接收到SIGTERM信号
        // 应该停止读操作，丢弃已经读取的数据，进行清理工作
        if (isInterrupt) {
          break;
        }

        line = file.getLine();
        if (line == null) {
          curOffset = file.tell();
          // 如果当前文件指针并非指向文件尾，重新打开文件，
          // seek到上次的位置，继续读取
          if (curOffset < file.getFileSize()) {
            file.close();
            file = BufferedFile.open(path);
            file.seek(curOffset);
            continue;
          } else {
            // 否则，记录进度，退出循环
            final Progress newProgress = new Progress(path, curOffset);
            recorder.set(newProgress);
            break;
          }
        }

        final String[] arr = line.split("\t");
        if (arr.length != LINE_COLUMN_SIZE) {
          continue;
        }
        lines.add(arr[1]);

        // 退出条件
        ++count;
        if (count >= LINES_LIMIT) {
          // 记录读取进度
          curOffset = file.tell();
          final Progress newProgress = new Progress(path, curOffset);
          recorder.set(newProgress);
          break;
        }
      }

    } catch (IOException ex) {
      log.error("failed get lines from file {}, error {}", path, ex);
    } finally {
      file.close();
    }

    return lines;
  }

  private List<String> getAllValidFiles(final Progress progress, final DateTime endTime) {
    final List<String> validPaths = new ArrayList<String>(); // 有效期范围内的文件列表

    // 如果目录不存在，返回空列表
    final File dir = new File(props.getTraceLogRootDir());
    if (!dir.exists()) {
      return validPaths;
    }

    // 扫描目录下的所有文件
    final String[] fileNames = dir.list(new PrefixFileFilter(FILENAME_PREFIX));
    // 按日期排序
    Arrays.sort(fileNames);
    if (progress == null) {
      // 返回所有endTime之前的文件
      for (final String fileName : fileNames) {
        final String dateStr = StringUtils.substringAfterLast(fileName, ".");
        final DateTime logDate = DateTime.parse(dateStr);
        if (logDate.isBefore(endTime)) {
          validPaths.add(props.getTraceLogRootDir() + fileName);
        }
      }
    } else {
      final String curFilePath = progress.getFileName();
      validPaths.add(curFilePath);

      final String beginTimeStr = StringUtils.substringAfterLast(curFilePath, ".");
      final DateTime beginTime = new DateTime(beginTimeStr);
      // 返回所有上次记录到当前时间范围之内的文件
      for (final String fileName : fileNames) {
        final String dateStr = StringUtils.substringAfterLast(fileName, ".");
        final DateTime logDate = DateTime.parse(dateStr);
        if (logDate.isAfter(beginTime) && logDate.isBefore(endTime)) {
          validPaths.add(props.getTraceLogRootDir() + fileName);
        }
      }
    }

    return validPaths;
  }
}
