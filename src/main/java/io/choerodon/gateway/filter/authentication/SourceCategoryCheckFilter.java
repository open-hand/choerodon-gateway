package io.choerodon.gateway.filter.authentication;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.gateway.dto.CategoryMenuDTO;
import io.choerodon.gateway.domain.CheckState;
import io.choerodon.gateway.dto.PermissionDTO;
import io.choerodon.gateway.domain.RequestContext;
import io.choerodon.gateway.mapper.CategoryMenuMapper;
import io.choerodon.gateway.mapper.PermissionMapper;
import io.choerodon.gateway.mapper.ProjectMapper;
import io.choerodon.gateway.util.SourceUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 普通接口(除公共接口，loginAccess接口，内部接口以外的接口)
 * 普通用户(超级管理员之外用户)的权限校验
 */
@Component
public class SourceCategoryCheckFilter implements HelperFilter {

    private static final String PROJECT_PATH_ID = "project_id";

    private final AntPathMatcher matcher = new AntPathMatcher();

    private PermissionMapper permissionMapper;
    private ProjectMapper projectMapper;
    private CategoryMenuMapper categoryMenuMapper;

    public SourceCategoryCheckFilter(PermissionMapper permissionMapper,
                                     ProjectMapper projectMapper,
                                     CategoryMenuMapper categoryMenuMapper) {
        this.permissionMapper = permissionMapper;
        this.projectMapper = projectMapper;
        this.categoryMenuMapper = categoryMenuMapper;
    }

    @Override
    public int filterOrder() {
        return 65;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        PermissionDTO permission = context.getPermission();
        if (ResourceLevel.SITE.value().equalsIgnoreCase(permission.getResourceLevel()) ||
                ResourceLevel.USER.value().equalsIgnoreCase(permission.getResourceLevel())) {
            context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
            context.response.setMessage("Have access to this 'site-level' interface, permission: " + context.getPermission());
            return true;
        }
        Long sourceId = null;
        if(ResourceLevel.PROJECT.value().equalsIgnoreCase(permission.getResourceLevel())){
            sourceId = SourceUtil.getSourceId(context.getTrueUri(), permission.getPath(), PROJECT_PATH_ID, matcher);
        }
        if (sourceId == null && ResourceLevel.PROJECT.value().equalsIgnoreCase(permission.getResourceLevel())) {
            context.response.setStatus(CheckState.API_ERROR_PROJECT_ID);
            context.response.setMessage("Project interface must have 'project_id' in path");
            return false;
        } else if (sourceId != null) {
            List<String> categories = parseCategory(sourceId);
            return checkCategoryMenu(context, permission.getCode(), categories, permission.getResourceLevel());
        }
        return true;
    }

    private Boolean checkCategoryMenu(final RequestContext context,
                                      final String permissionCode,
                                      final List<String> categories,
                                      final String level) {
        List<String> menuCodeList = permissionMapper.selectMenuCodeByPermissionCode(permissionCode);
        if (CollectionUtils.isEmpty(menuCodeList)) {
            return true;
        }
        List<CategoryMenuDTO> select = categoryMenuMapper.selectByMenuCodeList(level, categories, menuCodeList);
        if (CollectionUtils.isEmpty(select) && ResourceLevel.PROJECT.value().equalsIgnoreCase(level)) {
            context.response.setStatus(CheckState.PERMISSION_NOT_PASS_PROJECT);
            context.response.setMessage("No access to this project category,category:" + categories);
            return false;
        } else if (!CollectionUtils.isEmpty(select) && ResourceLevel.PROJECT.value().equalsIgnoreCase(level)) {
            context.response.setStatus(CheckState.SUCCESS_PASS_PROJECT);
            context.response.setMessage("Have access to this 'project-level' interface, permission: " + context.getPermission());
            return true;
        }
        return true;
    }

    private List<String> parseCategory(Long sourceId) {
        return projectMapper.getCategoriesByProjId(sourceId);
    }

}
