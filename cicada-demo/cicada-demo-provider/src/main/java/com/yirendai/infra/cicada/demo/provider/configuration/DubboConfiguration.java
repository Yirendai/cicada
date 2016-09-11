package com.yirendai.infra.cicada.demo.provider.configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import com.yirendai.infra.cicada.demo.provider.api.DemoService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * see: http://dubbo.io/User+Guide.htm#UserGuide-APIConfig
 *
 */
@Configuration
@ConditionalOnProperty(name = "dubbo.config.enabled")
@EnableConfigurationProperties({DubboConfiguration.DubboProperty.class})
public class DubboConfiguration {

  @Autowired
  private DemoService demoService;

  @Bean
  public ApplicationConfig dubboApplication() {
    final ApplicationConfig application = new ApplicationConfig();
    application.setName(dubboProperty.getApplicationName());
    return application;
  }

  @Bean
  public List<RegistryConfig> dubboRegistries() {
    List<RegistryConfig> registrConfigs = new ArrayList<RegistryConfig>();
    String zkUrls = dubboProperty.getZkUrls();
    if (StringUtils.isNotBlank(zkUrls)) {
      String[] zkUrlsArr = zkUrls.split(",");
      for (String zkUrl : zkUrlsArr) {
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(zkUrl);
        registry.setProtocol("zookeeper");
        registrConfigs.add(registry);
      }
    }
    return registrConfigs;
  }

  @Bean
  public ProtocolConfig dubboProtocol() {
    final ProtocolConfig protocol = new ProtocolConfig();
    protocol.setName(dubboProperty.getProtocolName());
    protocol.setPort(dubboProperty.getProtocolPort());
    return protocol;
  }

  @Bean
  public ProviderConfig dubboProvider() {
    final ProviderConfig provider = new ProviderConfig();
    provider.setProtocol(this.dubboProtocol());
    provider.setRetries(dubboProperty.getProviderRetries());
    provider.setActives(dubboProperty.getProviderActives());
    provider.setAccepts(dubboProperty.getProviderAccepts());
    provider.setTimeout(dubboProperty.getProviderTimeout());
    provider.setToken(dubboProperty.getProviderToken());
    return provider;
  }

  @Bean
  public ServiceConfig<DemoService> dubboDemoSerive() {
    final ServiceConfig<DemoService> dubboService = new ServiceConfig<>();// 该类很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
    dubboService.setApplication(this.dubboApplication());
    dubboService.setRegistries(this.dubboRegistries()); // 多个注册中心可以用setRegistries()
    dubboService.setProvider(dubboProvider());
    dubboService.setInterface(DemoService.class);
    dubboService.setRef(demoService);    
    dubboService.export();

    return dubboService;
  }

  @ConfigurationProperties(prefix = "dubbo.config", ignoreUnknownFields = true)
  static class DubboProperty {
    private String applicationName;
    private String zkUrls;
    private String protocolName;
    private int protocolPort;
    private String providerProtocol;
    private int providerRetries;
    private int providerActives;
    private int providerAccepts;
    private int providerTimeout;
    private String providerToken;
    
    public String getApplicationName() {
      return applicationName;
    }
    public void setApplicationName(String applicationName) {
      this.applicationName = applicationName;
    }
    public String getZkUrls() {
      return zkUrls;
    }
    public void setZkUrls(String zkUrls) {
      this.zkUrls = zkUrls;
    }
    public String getProtocolName() {
      return protocolName;
    }
    public void setProtocolName(String protocolName) {
      this.protocolName = protocolName;
    }
    public int getProtocolPort() {
      return protocolPort;
    }
    public void setProtocolPort(int protocolPort) {
      this.protocolPort = protocolPort;
    }
    public String getProviderProtocol() {
      return providerProtocol;
    }
    public void setProviderProtocol(String providerProtocol) {
      this.providerProtocol = providerProtocol;
    }
    public int getProviderRetries() {
      return providerRetries;
    }
    public void setProviderRetries(int providerRetries) {
      this.providerRetries = providerRetries;
    }
    public int getProviderActives() {
      return providerActives;
    }
    public void setProviderActives(int providerActives) {
      this.providerActives = providerActives;
    }
    public int getProviderAccepts() {
      return providerAccepts;
    }
    public void setProviderAccepts(int providerAccepts) {
      this.providerAccepts = providerAccepts;
    }
    public int getProviderTimeout() {
      return providerTimeout;
    }
    public void setProviderTimeout(int providerTimeout) {
      this.providerTimeout = providerTimeout;
    }
    public String getProviderToken() {
      return providerToken;
    }
    public void setProviderToken(String providerToken) {
      this.providerToken = providerToken;
    }
  }

  @Autowired
  private DubboProperty dubboProperty;
}
