package io.choerodon.gateway.filter;

import java.util.List;

import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.service.CustomPermissionCheckService;
import org.hzero.gateway.helper.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.gateway.infra.dto.ProjectUserDTO;
import io.choerodon.gateway.infra.mapper.ProjectUserMapper;

/**
 * @author scp
 * @date 2020/5/15
 * @description
 */
@Service
public class CustomPermissionCheckC7nFilter implements CustomPermissionCheckService {
    @Autowired
    private ProjectUserMapper projectUserMapper;
    private static final String PROJECT_PERMISSION_CODE_FAILED = "error.permission.projectNotPass";
    private static final String PROJECT_PERMISSION_NAME_FAILED = "PERMISSION_NOT_PASS_PROJECT";
    private static final String PROJECT_PERMISSION_NAME_SUCCESS = "SUCCESS_PASS_PROJECT";
    private static final String PROJECT_PERMISSION_CODE_SUCCESS = "success.permission.projectPass";

    private GatewayHelperProperties properties;

    @Override
    public void checkPermission(RequestContext context) {
        Long projectId = UrlUtils.parseLongValueFromUri(context.getTrueUri(), context.getPermission().getPath(), properties.getFilter().getCommonRequest().getParameterProjectId());
        ProjectUserDTO queryDTO = new ProjectUserDTO();
        queryDTO.setProjectId(projectId);
        queryDTO.setMemberId(context.getCustomUserDetails().getUserId());
        List<ProjectUserDTO> list = projectUserMapper.select(queryDTO);
        CheckState checkState;
        if (CollectionUtils.isEmpty(list)) {
            checkState = CheckState.newState(405, PROJECT_PERMISSION_CODE_FAILED, PROJECT_PERMISSION_NAME_FAILED);
        } else {
            checkState = CheckState.newState(202, PROJECT_PERMISSION_CODE_SUCCESS, PROJECT_PERMISSION_NAME_SUCCESS);

        }
        context.response.setStatus(checkState);

    }
}
