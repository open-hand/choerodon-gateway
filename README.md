# choerodon-gateway
网关服务

## Introduction
基于Spring Cloud Gateway的微服务网关服务。此服务是对[hzero-gateway](https://github.com/open-hand/hzero-gateway.git)服务的二开，添加了项目层权限过滤。

## Features

- 用户鉴权
- 限流
- 整体运维
- API监控

## Documentation
- 更多详情请参考`hzero-gateway`[中文文档](http://open.hand-china.com/document-center/doc/application/10039/10149?doc_id=4987)

## Dependencies

* 服务依赖

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-gateway</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

## 更新日志

- [更新日志](./CHANGELOG.zh-CN.md)

## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, CHOERODON
