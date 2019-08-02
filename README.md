# API Gateway
API 网关是基于Netflix Zuul的封装，集路由和鉴权于一体，为微服务提供统一的网关入口，同时支持从配置中心拉取动态路由。

![Flow chart](screenshot/flow_chart.png)

请求流程图
![Flow workflow](screenshot/filter_workflow.jpg)

## 必须条件
- 该服务是一个eureka client,因此需要一个eureka server注册中心。本地开发可以使用java版的eureka-server，k8s环境使用的是go版本的go-register-server

## 安装和启动
- 启动注册中心(eureka/go-register)
- 项目根目录下执行 `mvn spring-boot:run`

## 依赖
- 注册中心(eureka/go-register)
- 本地启动使用默认配置即可，服务端部署如果使用配置中心的配置，需要依赖配置中心(go-register/config-server)

## How to Contribute
欢迎Pull requests参与Choerodon，[Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)
