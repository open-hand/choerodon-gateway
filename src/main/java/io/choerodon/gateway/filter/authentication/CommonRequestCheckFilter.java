package io.choerodon.gateway.filter.authentication;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.gateway.domain.CheckState;
import io.choerodon.gateway.dto.PermissionDTO;
import io.choerodon.gateway.domain.RequestContext;
import io.choerodon.gateway.dto.ProjectDTO;
import io.choerodon.gateway.mapper.PermissionMapper;
import io.choerodon.gateway.mapper.ProjectMapper;
import io.choerodon.gateway.mapper.UserMapper;
import io.choerodon.gateway.util.SourceUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通接口(除公共接口，loginAccess接口，内部接口以外的接口)
 * 普通用户(超级管理员之外用户)的权限校验
 */
@Component
public class CommonRequestCheckFilter implements HelperFilter {

    private static final String PROJECT_PATH_ID = "project_id";

    private final AntPathMatcher matcher = new AntPathMatcher();

    private PermissionMapper permissionMapper;

    private ProjectMapper projectMapper;

    private UserMapper userMapper;

    public CommonRequestCheckFilter(PermissionMapper permissionMapper,ProjectMapper projectMapper,UserMapper userMapper) {
        this.permissionMapper = permissionMapper;
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
    }

    @Override
    public int filterOrder() {
        return 80;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        PermissionDTO permission = context.getPermission();
        Long memberId = null;
        String memberType;
        List<Long> sourceIds = new ArrayList<>();
        if (context.getCustomUserDetails().getClientId() != null) {
            memberId = context.getCustomUserDetails().getClientId();
            memberType = "client";
            List<Long> longs = permissionMapper.selectSourceIdsByUserIdAndPermission(
                    memberId, memberType,
                    context.getPermission().getId(), context.getPermission().getResourceLevel());
            sourceIds.addAll(longs);
        }

        if (context.getCustomUserDetails().getUserId() != null) {
            memberId = context.getCustomUserDetails().getUserId();
            memberType = "user";
            List<Long> longs = permissionMapper.selectSourceIdsByUserIdAndPermission(
                    memberId, memberType,
                    context.getPermission().getId(), context.getPermission().getResourceLevel());
            sourceIds.addAll(longs);
        }
        if (sourceIds.isEmpty()) {
            context.response.setStatus(CheckState.PERMISSION_NOT_PASS);
            context.response.setMessage("No access to this interface");
        } else if (ResourceLevel.ORGANIZATION.value().equals(permission.getResourceLevel())) {
            context.response.setStatus(CheckState.SUCCESS_PASS_ORG);
            context.response.setMessage("Have access to this 'organization-level' interface, permission: " + context.getPermission());
        } else if (ResourceLevel.PROJECT.value().equals(permission.getResourceLevel())) {
            checkProjectPermission(context, sourceIds, permission.getPath(),memberId);
        }
        return true;
    }

    private Boolean checkProjectPermission(final RequestContext context,
                                        final List<Long> sourceIds,
                                        final String matchPath,
                                        final Long memberId) {
        Long projectId = SourceUtil.getSourceId(context.getTrueUri(), matchPath, PROJECT_PATH_ID, matcher);
        if (projectId == null) {
            context.response.setStatus(CheckState.API_ERROR_PROJECT_ID);
            context.response.setMessage("Project interface must have 'project_id' in path");
        } else {
            ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
            boolean isOrgAdmin = userMapper.isOrgAdministrator(projectDTO.getOrganizationId(), memberId);
            if(isOrgAdmin){
                return false;
            }
            Boolean isEnabled = permissionMapper.projectEnabled(projectId);
            if (isEnabled != null && !isEnabled) {
                context.response.setStatus(CheckState.PERMISSION_DISABLED_PROJECT);
                context.response.setMessage("The project has been disabled, projectId: " + projectId);
            } else if (sourceIds.stream().anyMatch(t -> t.equals(projectId))) {
                context.response.setStatus(CheckState.SUCCESS_PASS_PROJECT);
                context.response.setMessage("Have access to this 'project-level' interface, permission: " + context.getPermission());
            } else {
                context.response.setStatus(CheckState.PERMISSION_NOT_PASS_PROJECT);
                context.response.setMessage("No access to this this project, projectId: " + projectId);
            }
        }
        return true;
    }

}
