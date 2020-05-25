package io.choerodon.gateway.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.gateway.helper.domain.PermissionCheckDTO;

import io.choerodon.gateway.infra.dto.ProjectUserDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/5/14
 * @description
 */
public interface ProjectUserMapper extends BaseMapper<ProjectUserDTO> {

    List<Long> selectSourceIdsByMemberAndRole(PermissionCheckDTO query, @Param("projectId") Long projectId);

}