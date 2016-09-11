# cicada-demo使用指南

## 目录 
        cicada\cicada-demo
## 目的
        演示cicada-client使用，如拦截http访问、dubbo、注解.....
## 环境搭建
### 1、  安装zookeeper并修改zk服务地址
        修改application.yml zookeeper的ip和端口，默认为本地
### 2、搭建nginx，配置nginx开启日志收集功能（LUA_JIT），日志将以Http Post的方式发送到nginx
         Nginx搭建好后，修改application.yml中nginx地址，provicer和consumer都需要修改
   补充说明：不操作此步骤的话，消费发送失败而已，不影响演示
### 3、  启动       
    cicada-demo-provider (生产者)
    cada-demo-consumer-web （消费者）
   启动成功后,在url上输入
   http://localhost:8080/test   //普通测试
   显示Hello test即为成功 