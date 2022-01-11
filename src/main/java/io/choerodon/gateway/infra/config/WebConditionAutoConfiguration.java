package io.choerodon.gateway.infra.config;

import org.hzero.gateway.filter.IpCheckedFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.choerodon.gateway.filter.ChoerodonIpNonCheckedFilter;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2022/1/11 16:26
 */
@Configuration
public class WebConditionAutoConfiguration {

    @Bean
    @Primary
    public IpCheckedFilter ipCheckedFilter() {
        return new ChoerodonIpNonCheckedFilter(null);
    }
}
