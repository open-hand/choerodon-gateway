FROM registry.cn-shanghai.aliyuncs.com/c7n/javabase:0.9.0
COPY app.jar /api-gateway.jar
RUN chown -R www-data:www-data /api-gateway.jar
USER www-data
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /api-gateway.jar