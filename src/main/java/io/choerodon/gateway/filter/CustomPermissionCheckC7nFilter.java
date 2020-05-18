package io.choerodon.gateway.filter;

import java.util.List;

import org.hzero.gateway.helper.entity.PermissionDO;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.service.CustomPermissionCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.gateway.infra.dto.ProjectUserDTO;
import io.choerodon.gateway.infra.mapper.ProjectUserMapper;
import io.choerodon.gateway.infra.util.SourceUtil;

/**
 * @author scp
 * @date 2020/5/15
 * @description
 */
@Service
public class CustomPermissionCheckC7nFilter implements CustomPermissionCheckService {
    @Autowired
    private ProjectUserMapper projectUserMapper;
    private static final String PROJECT_PATH_ID = "project_id";
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public void checkPermission(RequestContext context) {
        PermissionDO permission = context.getPermission();

        Long projectId = SourceUtil.getSourceId(context.getTrueUri(), permission.getPath(), PROJECT_PATH_ID, matcher);

        ProjectUserDTO queryDTO = new ProjectUserDTO();
        queryDTO.setProjectId(projectId);
        queryDTO.setMemberId(context.getCustomUserDetails().getUserId());
        List<ProjectUserDTO> list = projectUserMapper.select(queryDTO);
        if (CollectionUtils.isEmpty(list)) {
            throw new CommonException("error.permission.projectNotPass");
        }
    }
}
