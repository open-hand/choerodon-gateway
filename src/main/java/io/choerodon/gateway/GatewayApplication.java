package io.choerodon.gateway;


import org.hzero.autoconfigure.gateway.EnableHZeroGateway;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableHZeroGateway
@EnableDiscoveryClient
@ComponentScan
public class GatewayApplication {

    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder(GatewayApplication.class)
                    .web(WebApplicationType.REACTIVE)
                    .run(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
