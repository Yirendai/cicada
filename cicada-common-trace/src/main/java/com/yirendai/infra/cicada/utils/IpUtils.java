package com.yirendai.infra.cicada.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IpUtils {

  private static final Logger LOG = LoggerFactory.getLogger(IpUtils.class);

  /**
   * 静态变量缓存IP.
   */
  private static String cachedip = null;

  /*
   * 同步块使用
   */
  private static Object syncObject = new Object();

  static {
    try {
      cachedip = getRealIp();
    } catch (SocketException ex) {
      LOG.error("", ex);
      cachedip = "127.0.0.1";
    }
  }

  /**
   * 取得本机的IP，并把结果放到static变量中.
   * 
   * @return 如果有多个IP地址返回外网的IP，多个外网IP返回第一个IP（在多网管等特殊情况下）
   * @throws SocketException
   */
  public static String getRealIpWithStaticCache() {
    if (cachedip == null) {
      synchronized (syncObject) {
        try {
          cachedip = getRealIp();
        } catch (SocketException ex) {
          LOG.error("", ex);
          cachedip = "127.0.0.1";
        }
      }
      return cachedip;
    } else {
      return cachedip;
    }
  }

  /**
   * 刷新getRealIpWithStaticCache()方法的static变量.
   */
  public static void flushIpStaticCache() {
    synchronized (syncObject) {
      cachedip = null;
    }
  }

  /**
   * 取得本机的IP.
   * 
   * @return 如果有多个IP地址返回外网的IP，多个外网IP返回第一个IP（在多网管等特殊情况下）
   * @throws SocketException
   */
  public static String getRealIp() throws SocketException {
    String localip = null; // 本地IP，如果没有配置外网IP则返回它
    String netip = null; // 外网IP

    Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
    InetAddress ip = null;
    boolean finded = false; // 是否找到外网IP
    while (netInterfaces.hasMoreElements() && !finded) {
      NetworkInterface ni = netInterfaces.nextElement();
      Enumeration<InetAddress> address = ni.getInetAddresses();
      while (address.hasMoreElements()) {
        ip = address.nextElement();
        if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() 
            && ip.getHostAddress().indexOf(":") == -1) { // 外网IP
          netip = ip.getHostAddress();
          finded = true;
          break;
        } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() 
            && ip.getHostAddress().indexOf(":") == -1) { // 内网IP
          localip = ip.getHostAddress();
        }
      }
    }
    if (netip != null && !"".equals(netip)) {
      return netip;
    } else {
      return localip;
    }
  }
}
