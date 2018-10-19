package com.starlight.cdp.auth.security;

import com.starlight.cdp.platformapi.dto.CdpUserDetail;
import com.starlight.cdp.platformapi.entity.*;
import com.starlight.cdp.platformapi.service.inter.IPermissionService;
import com.starlight.cdp.platformapi.service.inter.IRolePermissionService;
import com.starlight.cdp.platformapi.service.inter.IUserPermissionService;
import com.starlight.cdp.platformapi.service.inter.IUserService;
import com.starlight.cdp.util.TreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 2017/12/27.
 * Time:16:37
 *
 * @author fosin
 */
@Service
@Slf4j
public class CdpUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private IUserService userService;
    @Autowired
    private IRolePermissionService rolePermissionService;
    @Autowired
    private IUserPermissionService userPermissionService;
    @Autowired
    private IPermissionService permissionService;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        //这里的username对应cdp_sys_user.usercode
        CdpSysUserEntity userEntity = userService.findByUsercode(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException("用户:" + username + "不存在!");
        }
        Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();

        List<CdpSysUserRoleEntity> userRoles = userEntity.getUserRoles();
        Integer userId = userEntity.getId();
        Set<CdpSysPermissionEntity> userPermissions = new TreeSet<>(new Comparator<CdpSysPermissionEntity>() {
            @Override
            public int compare(CdpSysPermissionEntity o1, CdpSysPermissionEntity o2) {
                int subId = o1.getId() - o2.getId();
                if (subId == 0) {
                    return subId;
                }
                int subLevel = o1.getLevel() - o2.getLevel();
                if (subLevel != 0) {
                    return subLevel;
                }
                int subSort = o1.getSort() - o2.getSort();
                if (subSort != 0) {
                    return subSort;
                }

                return o1.getCode().compareToIgnoreCase(o2.getCode());
            }
        });
        for (CdpSysUserRoleEntity userRole : userRoles) {
            CdpSysRoleEntity role = userRole.getRole();
            if (role.getStatus() != 0) {
                continue;
            }
            Integer roleId = role.getId();

            //获取角色权限
            List<CdpSysRolePermissionEntity> rolePermissionList = rolePermissionService.findByRoleId(roleId);
            for (CdpSysRolePermissionEntity rolePermissionEntity : rolePermissionList) {
                Integer permissionId = rolePermissionEntity.getPermissionId();
                CdpSysPermissionEntity entity = permissionService.findOne(permissionId);
                // 只添加状态为启用的权限
                if (entity.getStatus() == 0) {
                    userPermissions.add(entity);
                    grantedAuthoritySet.add(new SimpleGrantedAuthority(permissionId+""));
                }
            }
        }
        //获取用户权限
        List<CdpSysUserPermissionEntity> userPermissionList = userPermissionService.findByUserId(userId);
        for (CdpSysUserPermissionEntity userPermissionEntity : userPermissionList) {
            int addmode = userPermissionEntity.getAddMode();
            Integer permissionId = userPermissionEntity.getPermissionId();
            CdpSysPermissionEntity entity = permissionService.findOne(permissionId);
            // 只操作状态为启用的权限
            if (entity.getStatus() == 0) {
                //获取用户增权限
                if (addmode == 0) {
                    userPermissions.add(entity);
                    grantedAuthoritySet.add(new SimpleGrantedAuthority(permissionId +""));
                } else { //移除用户减权限
                    userPermissions.remove(entity);
                    grantedAuthoritySet.remove(new SimpleGrantedAuthority(permissionId +""));
                }
            }
        }
        //转成ArrayList保证有序
//        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>(grantedAuthoritySet);

        CdpSysPermissionEntity permissionTree = TreeUtil.createTree(userPermissions, 1, "id", "pId", "children");

        CdpUserDetail user = new CdpUserDetail(userEntity, permissionTree, grantedAuthoritySet);
        log.debug("UserDetailsServiceImpl User:" + user.toString());
        return user;
    }
}