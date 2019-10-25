# API Gateway
API 网关是基于Netflix Zuul的封装，集路由和鉴权于一体，为微服务提供统一的网关入口，同时支持从配置中心拉取动态路由。

![Flow chart](screenshot/flow_chart.png)

请求流程图
![Flow workflow](screenshot/filter_workflow.jpg)

## 服务配置

- `application.yml`

  ```yaml
   spring:
     servlet:
       multipart:
         max-file-size: 30MB # 单个文件最大上传大小
         max-request-size: 30MB # 总上传文件最大上传大小
     datasource:
       url: jdbc:mysql://localhost/base_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true
       username: choerodon
       password: 123456
     redis:
       host: localhost
       port: 6379
       database: 4
       lettuce:
         shutdown-timeout: 200ms # 连接池最大阻塞等待时间（使用负值表示没有限制）
         pool:
           max-active: 8 # 连接池最大连接数（使用负值表示没有限制） 默认 8
           max-idle: 8 # 连接池中的最大空闲连接 默认 8
           min-idle: 2 # 连接池中的最小空闲连接 默认 0
           max-wait: -1ms # 等待可用连接的最大时间，负数不限制
     cache:
       multi:
         l1:
           enabled: true # 是否开启一级缓存
           type: caffeine # 一级缓存实现。目前支持caffeine和guava
         l2:
           enabled: false # 是否开启二级缓存
           type: redis # 二级缓存的实现
         caches:
           permission: # 请求对应的权限缓存
             l1-spec: initialCapacity=50,maximumSize=500,expireAfterWrite=600s
             l2-spec: expiration=600
   eureka:
     instance:
       preferIpAddress: true
       leaseRenewalIntervalInSeconds: 10
       leaseExpirationDurationInSeconds: 30
       metadata-map:
         VERSION: v1
     client:
       serviceUrl:
         defaultZone: http://localhost:8000/eureka/
       registryFetchIntervalSeconds: 10
       disable-delta: true
   zuul:
     addHostHeader: true
     routes:
       iam:
         path: /iam/**
         serviceId: iam-service
       oauth:
         path: /oauth/**
         serviceId: oauth-server
         stripPrefix: false
         sensitiveHeaders: none
       notify:
         path: /notify/**
         serviceId: notify-service
       asgard:
         path: /asgard/**
         serviceId: asgard-service
       manager:
         path: /manager/**
         serviceId: manager-service
       file:
         path: /file/**
         serviceId: file-service
       org:
         path: /org/**
         serviceId: organization-service
       devops:
         path: /devops/**
         serviceId: devops-service
       agile:
         path: /agile/**
         serviceId: agile-service
       test-manager:
         path: /test/**
         serviceId: test-manager-service
       apim:
         path: /apim/**
         serviceId: apim-service
       knowledge:
         path: /knowledge/**
         serviceId: knowledgebase-service
       low-code:
         path: /lc/**
         serviceId: low-code-service
       base-service:
         path: /base/**
         serviceId: base-service
       buzz:
         path: /buzz/**
         serviceId: buzz-service
       market:
         path: /market/**
         serviceId: market-service
     semaphore:
       max-semaphores: 300
     sensitiveHeaders: Cookie,Set-Cookie
   hystrix:
     command:
       default:
         execution:
           isolation:
             thread:
               timeoutInMilliseconds: 80000
   choerodon:
     category:
       enabled: false   # 是否开启项目/组织类别权限校验
     gateway:
       allowed:
         origin: '*'
       enabled: true
       enabled-jwt-log: false
       jwt-key: choerodon # jwt的密钥
       oauth-info-uri: http://oauth-server/oauth/api/user # oauth获取userDetail地址
       permission:
         enabled: true # 是否开启权限校验
         # 跳过权限校验路径,本地开发使用swagger需要跳过这些链接/manager/swagger-ui.html,/manager/webjars/**,/manager/swagger-resources/**
         skip-paths: /**/skip/**, /oauth/**,/prometheus,/health,/env,/metrics,/favicon.ico
         cache-seconds: 600 # 请求地址和对应的权限缓存时间
         cache-size: 3000  # 请求地址和对应的权限缓存大小
   security:
     oauth2:
       client:
         grant-type: client_credentials
       resource:
         userInfoUri: http://oauth-server/oauth/api/user
   mybatis:
     mapperLocations: classpath*:/mapper/*.xml
     configuration:
       mapUnderscoreToCamelCase: true
   ribbon:
     ReadTimeout: 20000
     ConnectTimeout: 20000
     httpclient:
       enabled: false
     okhttp:
       enabled: true
   logging:
     level:
       com.netflix.discovery.DiscoveryClient: warn
  ```

- `bootstrap.yml`

  ```yaml
  server:
    port: 8080
  spring:
    application:
      name: api-gateway
    cloud:
      config:
        failFast: true
        uri: http://localhost:8010
        enabled: false
        retry:
          maxAttempts: 6
          multiplier: 1.1
          maxInterval: 2000
  management:
    endpoint:
      health:
        show-details: ALWAYS
    server:
      port: 8081
    endpoints:
      web:
        exposure:
          include: '*'
  ```
## 环境需求

- mysql 5.6+
- redis 3.0+
- `api-gateway` 服务依赖于 [base-service](https://code.choerodon.com.cn/choerodon-framework/base-service.git) 服务的数据库, 所以请确保 `base-service` 服务的数据库已经创建并初始化。
- 该项目是一个 Eureka Client 项目，启动后需要注册到 `EurekaServer`，本地环境需要 `eureka-server`，线上环境需要使用 `go-register-server`

## 安装和启动步骤

- 运行 `eureka-server`，[代码库地址](https://code.choerodon.com.cn/choerodon-framework/eureka-server.git)。

- 本地启动 redis-server

- 启动项目，项目根目录下执行如下命令：

  ```sh
   mvn spring-boot:run
  ```

## 更新日志

- [更新日志](./CHANGELOG.zh-CN.md)

## 如何参与

- 欢迎参与我们的项目，了解更多有关如何[参与贡献](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)的信息。


