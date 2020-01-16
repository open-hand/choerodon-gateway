package io.choerodon.gateway.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.gateway.dto.UserDTO;
import io.choerodon.mybatis.common.Mapper;

/**
 * @author: 25499
 * @date: 2020/1/16 9:38
 * @description:
 */
public interface UserMapper extends Mapper<UserDTO> {
    Boolean isOrgAdministrator(@Param("organizationId") Long organizationId,
                               @Param("userId") Long userId);
}
