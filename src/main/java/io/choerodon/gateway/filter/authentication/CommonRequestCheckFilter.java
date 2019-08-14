package io.choerodon.gateway.filter.authentication;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.gateway.domain.CheckState;
import io.choerodon.gateway.dto.PermissionDTO;
import io.choerodon.gateway.domain.RequestContext;
import io.choerodon.gateway.mapper.PermissionMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通接口(除公共接口，loginAccess接口，内部接口以外的接口)
 * 普通用户(超级管理员之外用户)的权限校验
 */
@Component
public class CommonRequestCheckFilter implements HelperFilter {

    private PermissionMapper permissionMapper;

    public CommonRequestCheckFilter(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
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
        Long memberId;
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
        } else if (ResourceLevel.SITE.value().equals(permission.getResourceLevel()) || ResourceLevel.ORGANIZATION.value().equals(permission.getResourceLevel())) {
            context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
            context.response.setMessage("Have access to this 'site-level' interface, permission: " + context.getPermission());
        } else {
            context.response.setStatus(CheckState.PERMISSION_DISABLED_NO_SITE_LEVEL);
            context.response.setMessage("No 'site-level' permissions are disabled.");
        }
        return true;
    }

}
