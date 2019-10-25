FROM registry.cn-shanghai.aliyuncs.com/choerodon-tools/javabase:0.8.2
COPY app.jar /api-gateway.jar
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /api-gateway.jar
