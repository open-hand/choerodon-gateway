# Choerodon API Gateway
Choerodon API Gateway 是基于Netflix Zuul 的封装，集路由和鉴权于一体，为微服务提供统一的网关入口，同时支持从配置中心拉取动态路由。

## Introduction

## Add Helm chart repository

``` bash    
helm repo add choerodon https://openchart.choerodon.com.cn/choerodon/c7n
helm repo update
```

## Installing the Chart

```bash
$ helm install c7n/api-gateway --name api-gateway
```

Specify each parameter using the `--set key=value[,key=value]` argument to `helm install`.

## Uninstalling the Chart

```bash
$ helm delete api-gateway
```

## Configuration

Parameter | Description	| Default
--- |  ---  |  ---  
`replicaCount` | Replicas count | `1`
`preJob.timeout` | job超时时间 | `300`
`preJob.preConfig.enabled` | 是否初始化配置 | `true`
`preJob.preConfig.configFile` | 初始化到配置中心文件名 | `application.yml`
`preJob.preConfig.configType` | 初始化到配置中心存储方式 | `k8s`
`preJob.preConfig.updatePolicy` | 初始化配置策略（not/add/override/update） | `add`
`preJob.preConfig.registerHost` | 注册中心地址 | `http://register-server:8000`
`deployment.managementPort` | 服务管理端口 | `8081`
`env.open.SPRING_CLOUD_CONFIG_ENABLED` | 是否启用配置中心 | `true`
`env.open.SPRING_CLOUD_CONFIG_URI` | 配置中心地址 | `http://register-server:8000/`
`env.open.SPRING_DATASOURCE_URL` | 数据库连接地址 | `jdbc:mysql://127.0.0.1/iam_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true`
`env.open.SPRING_DATASOURCE_USERNAME` | 数据库用户名 | `choerodon`
`env.open.SPRING_DATASOURCE_PASSWORD` | 数据库密码 | `password`
`env.open.SPRING_CACHE_MULTI_L1_ENABLED` | 是否开启一级缓存 | `true`
`env.open.SPRING_CACHE_MULTI_L2_ENABLED` | 是否开启二级缓存 | `false`
`env.open.SPRING_REDIS_HOST` | redis主机地址 | `localhost`
`env.open.SPRING_REDIS_PORT` | redis端口 | `6379`
`env.open.SPRING_REDIS_DATABASE` | redis db | `4`
`env.open.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | 注册服务地址 | `http://register-server:8000/eureka/`
`env.open.CHOERODON_GATEWAY_ALLOWED_ORIGIN` | 跨域配置 | `*`
`service.enabled` | 是否创建k8s service | `false`
`service.type` |  service类型 | `ClusterIP`
`service.port` | service端口 | `8080`
`service.name` | service名称 | `api-gateway`
`ingress.enabled` | 是否创建k8s ingress | `false`
`ingress.host` | 服务域名地址 | ``
`metrics.path` | 收集应用的指标数据路径 | ``
`metrics.group` | 性能指标应用分组 | `spring-boot`
`logs.parser` | 日志收集格式 | `spring-boot`
`resources.limits` | k8s中容器能使用资源的资源最大值 | `2Gi`
`resources.requests` | k8s中容器使用的最小资源需求 | `1Gi`

### SkyWalking Configuration
Parameter | Description
--- |  --- 
`javaagent` | SkyWalking 代理jar包(添加则开启 SkyWalking，删除则关闭)
`skywalking.agent.application_code` | SkyWalking 应用名称
`skywalking.agent.sample_n_per_3_secs` | SkyWalking 采样率配置
`skywalking.agent.namespace` | SkyWalking 跨进程链路中的header配置
`skywalking.agent.authentication` | SkyWalking 认证token配置
`skywalking.agent.span_limit_per_segment` | SkyWalking 每segment中的最大span数配置
`skywalking.agent.ignore_suffix` | SkyWalking 需要忽略的调用配置
`skywalking.agent.is_open_debugging_class` | SkyWalking 是否保存增强后的字节码文件
`skywalking.collector.backend_service` | SkyWalking OAP 服务地址和端口配置

```bash
$ helm install c7n/api-gateway \
    --set env.open.SKYWALKING_OPTS="-javaagent:/agent/skywalking-agent.jar -Dskywalking.agent.application_code=api-gateway  -Dskywalking.agent.sample_n_per_3_secs=-1 -Dskywalking.collector.backend_service=oap.skywalking:11800" \
    --name api-gateway
```

## 验证部署
```bash
curl -s $(kubectl get po -n c7n-system -l choerodon.io/release=api-gateway -o jsonpath="{.items[0].status.podIP}"):8081/actuator/health | jq -r .status
```
出现以下类似信息即为成功部署

```bash
UP
```