package io.choerodon.gateway.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.gateway.helper.domain.PermissionCheckDTO;

import io.choerodon.gateway.infra.dto.ProjectPermissionDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/5/14
 * @description
 */
public interface ProjectPermissionMapper extends BaseMapper<ProjectPermissionDTO> {

    List<Long> selectSourceIdsByMemberAndRole(@Param("query") PermissionCheckDTO query, @Param("projectId") Long projectId);

    /**
     * 根据组织Id和用户Id查询该用户是否分配了组织管理员角色.
     *
     * @param organizationId 组织Id
     * @param userId         用户Id
     * @return 返回true 表示分配了组织管理员角色;否则, 表示未分配
     */
    Boolean isOrgAdministrator(@Param("organizationId") Long organizationId,
                               @Param("userId") Long userId,
                               @Param("memberType") String memberType);
}