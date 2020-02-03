package io.choerodon.gateway.filter.authentication;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.gateway.domain.CheckState;
import io.choerodon.gateway.domain.RequestContext;
import io.choerodon.gateway.dto.PermissionDTO;
import io.choerodon.gateway.dto.ProjectDTO;
import io.choerodon.gateway.mapper.ProjectMapper;
import io.choerodon.gateway.mapper.UserMapper;
import io.choerodon.gateway.util.SourceUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * 组织管理员的权限校验（拥有组织下所有项目权限）
 */
@Component
public class OrgAdminUserPermissionFilter implements HelperFilter {
    private static final String PROJECT_PATH_ID = "project_id";
    private final AntPathMatcher matcher = new AntPathMatcher();

    private ProjectMapper projectMapper;
    private UserMapper userMapper;


    public OrgAdminUserPermissionFilter(ProjectMapper projectMapper, UserMapper userMapper) {
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
    }

    @Override
    public int filterOrder() {
        return 75;
    }

    /**
     * 项目层接口才过滤
     * @param context 请求上下文
     * @return 是否执行过滤器
     */
    @Override
    public boolean shouldFilter(RequestContext context) {
        return ResourceLevel.PROJECT.value().equals(context.getPermission().getResourceLevel());
    }

    @Override
    public boolean run(RequestContext context) {
        PermissionDTO permission = context.getPermission();
        CustomUserDetails customUserDetails = context.getCustomUserDetails();
        Long projectId = SourceUtil.getSourceId(context.getTrueUri(), permission.getPath(), PROJECT_PATH_ID, matcher);
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);

        Boolean isOrgAdmin = userMapper.isOrgAdministrator(projectDTO.getOrganizationId(), customUserDetails.getUserId());
        if (isOrgAdmin) {
            context.response.setStatus(CheckState.SUCCESS_ADMIN);
            context.response.setMessage("Org admin user have access to the interface, username: "
                    + context.getCustomUserDetails().getUsername());
            return false;
        }
        return true;
    }
}
