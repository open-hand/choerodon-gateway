package io.choerodon.gateway.filter;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.util.UrlUtils;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.domain.PermissionCheckDTO;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.PermissionDO;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.service.CustomPermissionCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.gateway.infra.mapper.ProjectPermissionMapper;

/**
 * @author scp
 * @date 2020/5/15
 * @description
 */
@Component
public class CustomPermissionCheckC7nFilter implements CustomPermissionCheckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomPermissionCheckC7nFilter.class);

    private static final String PROJECT_PERMISSION_CODE_FAILED = "error.permission.projectNotPass";
    private static final String PROJECT_PERMISSION_NAME_FAILED = "PERMISSION_NOT_PASS_PROJECT";
    private static final String PROJECT_PERMISSION_NAME_SUCCESS = "SUCCESS_PASS_PROJECT";
    private static final String PROJECT_PERMISSION_CODE_SUCCESS = "success.permission.projectPass";

    List<String> parameterProjectId;
    private ProjectPermissionMapper projectPermissionMapper;
    private boolean checkCurrentRole;

    public CustomPermissionCheckC7nFilter(ProjectPermissionMapper projectPermissionMapper, GatewayHelperProperties properties) {
        this.projectPermissionMapper = projectPermissionMapper;
        this.checkCurrentRole = properties.getFilter().getCommonRequest().isCheckCurrentRole();
        this.parameterProjectId = properties.getFilter().getCommonRequest().getParameterProjectId();
    }

    @Override
    public void checkPermission(RequestContext context) {
        Long projectId = UrlUtils.parseLongValueFromUri(context.getTrueUri(), context.getPermission().getPath(), parameterProjectId);
        if (Objects.isNull(projectId)) {
            return;
        }
        PermissionDO permission = context.getPermission();
        CustomUserDetails details = context.getCustomUserDetails();
        Long memberId = null;
        String memberType = null;
        List<Long> roleIds = null;
        Long tenantId = details.getTenantId();
        String permissionCode = permission.getCode();
        boolean lov = StringUtils.isNotEmpty(context.getLovCode());
        String sourceType;
        if (details.getClientId() != null) {
            memberId = details.getClientId();
            memberType = "client";
        }
        if (details.getUserId() != null) {
            memberId = details.getUserId();
            memberType = "user";
            roleIds = details.roleMergeIds();
        }
        if (lov) {
            permissionCode = context.getLovCode();
            sourceType = null;
        } else {
            sourceType = StringUtils.equals(ResourceLevel.SITE.value(), permission.getFdLevel()) ? ResourceLevel.SITE.value() : ResourceLevel.ORGANIZATION.value();
        }
        PermissionCheckDTO queryDTO = new PermissionCheckDTO(memberId, memberType, tenantId, roleIds, permissionCode, sourceType, checkCurrentRole);
        LOGGER.debug("Project Common request check: {}", queryDTO);
        CheckState checkState;
        // 组织管理员跳过项目层权限校验
        if (projectPermissionMapper.isOrgAdministrator(tenantId, memberId)) {
            checkState = CheckState.newState(202, PROJECT_PERMISSION_CODE_SUCCESS, PROJECT_PERMISSION_NAME_SUCCESS);
        } else {
            List<Long> list = projectPermissionMapper.selectSourceIdsByMemberAndRole(queryDTO, projectId);
            if (CollectionUtils.isEmpty(list)) {
                checkState = CheckState.newState(405, PROJECT_PERMISSION_CODE_FAILED, PROJECT_PERMISSION_NAME_FAILED);
            } else {
                checkState = CheckState.newState(202, PROJECT_PERMISSION_CODE_SUCCESS, PROJECT_PERMISSION_NAME_SUCCESS);
            }
        }
        context.response.setStatus(checkState);
    }
}
