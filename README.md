## 使用说明

### 概述

基于Spring Cloud Gateway的微服务网关服务

## 服务配置 

- `Spring Cloud Gateway的路由配置`
```
# 路由名，需要唯一
spring.cloud.gateway.routes[0].id=hiam
# lb://前缀表示使用ribbon负载均衡机制，路由到服务名为hzero-iam到服务上
spring.cloud.gateway.routes[0].uri=lb://hzero-iam
# 表示路由匹配路径为/iam/**
spring.cloud.gateway.routes[0].predicates[0]=Path=/iam/**
# 表示请求在路由时会去掉path前缀
spring.cloud.gateway.routes[0].filters[0]=StripPrefix=1
# 开启后，gateway会自动将服务发现列表转化为路由（如hzero-iam服务将会自动为其生成/hzero-iam/**匹配路径的路由）
spring.cloud.gateway.discovery.locator.enable=true

```

```
spring:
  # 文件上传大小限制
  servlet:
    multipart:
      max-file-size: 30MB
      max-request-size: 30MB
  cloud:
    config:
      # 如果设置为 true，当从配置中心获取配置势必，则服务报错无法启动，一般设置为 false 即可
      fail-fast: false
      # 启用配置中心
      enabled: true
      # 配置中心地址
      uri: http://dev.hzero.org:8010
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
      # This route rule used to forward request to activity server
      - id: hiam
        uri: lb://hzero-iam
        predicates:
        - Path=/iam/**
        filters:
        - StripPrefix=1
        # 限流配置，使用方式查阅Spring cloud Gateway官网
#        - name: RequestRateLimiter
#          args:
#            redis-rate-limiter.replenishRate: 1
#            redis-rate-limiter.burstCapacity: 1
#            rate-limiter: "#{@redisRateLimiter}"
#            key-resolver: "#{@userKeyResolver}"
#            key-resolver: "#{
#            new org.hzero.gateway.ratelimit.CombinedKeyResolver(
#              new org.hzero.gateway.ratelimit.UserKeyResolver('white-list','1,2'),
#              new org.hzero.gateway.ratelimit.RoleKeyResolver('white-list','1')
#            )}"
      - id: hoth
        uri: lb://hzero-oauth
        predicates:
        - Path=/oauth/**
        filters:
        - StripPrefix=0
        - PreserveHostHeader

# ribbon 配置
ribbon:
  # 读超时
  ReadTimeout: ${RIBBON_READ_TIMEOUT:30000}
  # 连接超时
  ConnectTimeout: ${RIBBON_CONNECT_TIMEOUT:6000}
  # 服务列表更新间隔，默认30秒，设置为5秒，当多个服务实例频繁上下线时，能减少服务不可用时间
  ServerListRefreshInterval: ${SERVER_LIST_REFRESH_INTERVAL:5000}

hzero:
  gateway:
    # 跨域配置
    cors:
      allowed-origins:
        - "*"
      allowed-headers:
        - "*"
      allowed-methods:
        - "*"
    helper:
      enabled: true
      ## 启用API签名
      signature:
        enabled: false
        secrets:
            # 签名ID和签名密钥
          - secretId: hzero
            secretKey: 537509248a3da7804d12905c102d14cd1bec000797a6178a7353a4c3ac23a0b3
      jwt-key: hzero # jwt的密钥
      # 是否打印 JWT
      enabled-jwt-log: ${HZERO_GATEWAY_HELPER_ENABLE_JWT_LOG:false}
      # OAuth服务 context-path
      oauth-context-path: ${HZERO_GATEWAY_HELPER_OAUTH_CONTEXT_PATH:/oauth}
      filter:
        collect-span:
          # 是否统计API访问情况
          enabled: ${HZERO_GATEWAY_FILTER_COLLECT_SPAN_ENABLED:false}
        common-request:
          # 租户级API是否必须检查租户参数
          check-tenant: ${HZERO_GATEWAY_FILTER_COMMON_REQUEST_CHECK_ORGANIZATION:true}
          # 项目级API是否必须检查项目参数
          check-project: ${HZERO_GATEWAY_FILTER_COMMON_REQUEST_CHECK_PROJECT:true}
          # 是否启用标准的权限检查
          enabled: ${HZERO_GATEWAY_FILTER_COMMON_REQUEST_ENABLE:true}
		  # URI租户ID参数名称
          parameter-tenant-id:
            - tenantId
            - organizationId
      permission:
        cache-seconds: 600 # 请求地址和对应的权限缓存时间
        cache-size: 3000  # 请求地址和对应的权限缓存大小
        # 跳过权限校验的路由
		skip-paths:
          - /oauth/**
          - /swagger/swagger-ui.html
          - /swagger/swagger-resources/**
          - /swagger/webjars/**
          - /swagger/docs/**
          - /hwfp/editor-app/**
          - /hwfp/lib/**
          - /hwfp/activiti-editor
          - /*/sock-js/**
          - /*/websocket/**
  ## 服务整体运维配置
  maintain:
    # 全局运维信息
    global-info:
      state: NORMAL
    # 服务运维信息
    service-maintain-info:
      oauth:
        state: NORMAL
    # 运维接口密钥，不配置则在程序启动时生成随机key
    secret-key: hzero
```

- `启动类配置`

@EnableHZeroGateway是必须加上的注解，用于驱动网关服务的自动配置类，从而启用网关服务功能。

@EnableDiscoveryClient使服务作为服务发现客户端注册到注册中心，推荐使用@EnableDiscoveryClient而不是@EnableEurekaClient，@EnableDiscoveryClient更加灵活。

需要注意的是，在编写引导类时需要指定web环境为REACTIVE类型，由于Spring Cloud Gateway基于webflux包开发，其web容器是REACTIVE类型，而hzero-starter-core包中依赖了web包，如果不指定web容器将会默认走webmvc而冲突。

```
@EnableHZeroGateway
@EnableDiscoveryClient
public class GatewayApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(GatewayApplication.class)
            .web(WebApplicationType.REACTIVE)
            .run(args);

    }
}
```
