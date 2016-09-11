/*************************************************************
 *
 * Copyright (c) 2015 yirendai.com, Inc. All Rights Reserved
 *
 *************************************************************/

/**
 * @file BufferedFile.java
 * @author melody(zechengzhao@yirendai.com)
 * @date 2016年3月31日
 * @brief 对RandomAccessFile添加缓存，提高存储性能。
 */

package com.yirendai.infra.cicada.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

public final class BufferedFile {

  private static final int BUFFER_LEN = 1 << 20;
  private final RandomAccessFile file;
  private long curOffset;
  private byte[] buffer;

  // 用于在buffer中定位一行的起止下标
  private int startPos;
  private int endPos;
  private int readBytes;

  private BufferedFile(final RandomAccessFile file) {
    this.file = file;
    this.curOffset = 0;
    this.buffer = null;
    this.startPos = 0;
    this.endPos = 0;
    this.readBytes = 0;
  }

  /**
   * 打开文件.
   * 
   * @param path 文件路径
   * @return BufferedFile实例
   */
  public static BufferedFile open(final String path) {
    BufferedFile bufferFile = null;
    try {
      final RandomAccessFile file = new RandomAccessFile(path, "rw");
      bufferFile = new BufferedFile(file);
    } catch (FileNotFoundException ex) { 
      // 不做处理，bufferFile为null
    }
    return bufferFile;
  }

  /**
   * 关闭文件.
   */
  public void close() {
    try {
      file.close();
      buffer = null;
    } catch (IOException ex) {
      // 不做处理
    }
  }

  /**
   * 获取文件当前偏移.
   * @return 文件当前偏移量
   */
  public long tell() {
    return curOffset;
  }

  /**
   * 获取当前文件长度.
   * @return 当前文件长度.
   */
  public long getFileSize() throws IOException {
    return file.length();
  }

  /**
   * 判断当前文件指针是否指向文件尾 EOF.
   * 
   * @return true or false
   */
  public boolean feof() throws IOException {
    return this.file.length() == this.curOffset;
  }

  /**
   * 定位到文件的指定位置.
   * 
   * @param offset 指定文件相对于文件起始处的偏移量
   */
  public void seek(final long offset) throws IOException {
    file.seek(offset);
    curOffset = offset;
    buffer = null;
  }

  /**
   * 从当前文件偏移，一直读到文件尾.
   * @return 包含文件所有行的string list
   */
  public List<String> getAllLines() throws IOException {
    final List<String> lines = new LinkedList<String>();
    String line;
    while (true) {
      line = getLine();
      if (line == null) {
        break;
      }
      lines.add(line);
    }

    return lines;
  }

  /**
   * 读取文件的一行.
   * 
   * @return String
   */
  public String getLine() throws IOException {

    if (buffer == null) {
      buffer = new byte[BUFFER_LEN];
      readBytes = file.read(buffer, 0, BUFFER_LEN);
      if (readBytes <= 0) {
        // end of file
        return null;
      }
    }

    String result = null;
    int length = 0;
    // 一行的数据都位于buffer中
    // -|----|----
    for (; endPos < readBytes; ++endPos) {
      if (buffer[endPos] == '\n') {
        ++endPos;
        length = endPos - startPos;
        result = new String(buffer, startPos, endPos - startPos);

        startPos = endPos;
        curOffset += length;
        return result;
      }
    }

    // 如果一行的长度超过 1MB，直接调用 readLine 方法
    // |---------- |
    if (startPos == 0 && endPos == readBytes - 1) {
      seek(curOffset);
      result = file.readLine();

      curOffset = file.getFilePointer();
      startPos = endPos = 0;
      buffer = null;

      return result;
    }

    // 行尾不在buffer的情况
    // -----------|--- |
    file.seek(curOffset);
    startPos = endPos = 0;
    buffer = null;
    return getLine();
  }
}
