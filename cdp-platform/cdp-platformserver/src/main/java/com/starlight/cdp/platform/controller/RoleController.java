package com.starlight.cdp.platform.controller;

import com.starlight.cdp.core.exception.CdpControllerException;
import com.starlight.cdp.mvc.controller.ISimpleController;
import com.starlight.cdp.mvc.service.ISimpleService;
import com.starlight.cdp.platformapi.entity.CdpSysRoleEntity;
import com.starlight.cdp.platformapi.entity.CdpSysRolePermissionEntity;
import com.starlight.cdp.platformapi.entity.CdpSysUserEntity;
import com.starlight.cdp.platformapi.entity.CdpSysUserRoleEntity;
import com.starlight.cdp.platformapi.service.inter.IRolePermissionService;
import com.starlight.cdp.platformapi.service.inter.IRoleService;
import com.starlight.cdp.platformapi.service.inter.IUserRoleService;
import com.starlight.cdp.platformapi.service.inter.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description 角色控制器
 *
 * @author fosin
 */
@RestController
@RequestMapping("v1/role")
@Api(value = "v1/role", tags = "角色管理", description = "角色管理相关操作")
public class RoleController implements ISimpleController<CdpSysRoleEntity, Integer> {
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRolePermissionService rolePermissionService;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IUserService userService;

    @ApiOperation("根据角色ID获取角色权限")
    @ApiImplicitParam(name = "roleId", value = "角色ID,取值于CdpSysRoleEntity.id")
    @RequestMapping(value = "/permissions/{roleId}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<List<CdpSysRolePermissionEntity>> permissions(@PathVariable Integer roleId) {
        return ResponseEntity.ok(rolePermissionService.findByRoleId(roleId));
    }

    @ApiOperation(value = "根据角色ID更新角色权限", notes = "根据角色ID更新角色权限，此操作将先删除原权限，再新增新权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entities", value = "角色权限集合(List<CdpSysRolePermissionEntity>)"),
            @ApiImplicitParam(name = "roleId", value = "角色ID,取值于CdpSysRoleEntity.id")
    })
    @PutMapping(value = "/permissions/{roleId}")
    public ResponseEntity<List<CdpSysRolePermissionEntity>> permissions(@RequestBody List<CdpSysRolePermissionEntity> entities,
                                                                        @PathVariable("roleId") Integer roleId) {
        return ResponseEntity.ok(rolePermissionService.updateInBatch(roleId, entities));
    }

    @ApiOperation("根据角色唯一id查找该角色所有用户信息")
    @ApiImplicitParam(name = "roleId", value = "角色ID,取值于CdpSysRoleEntity.id")
    @RequestMapping(value = "/users/{roleId}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<List<CdpSysUserEntity>> getRoleUsers(@PathVariable("roleId") Integer roleId) throws CdpControllerException {
        return ResponseEntity.ok(userService.findRoleUsersByRoleId(roleId));
    }


    @ApiOperation(value = "根据角色ID更新角色拥有的用户", notes = "更新角色拥有的用户，此操作将先删除原用户集合，再新增新用户集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entities", value = "角色用户集合(List<CdpSysUserRoleEntity>)"),
            @ApiImplicitParam(name = "roleId", value = "角色ID,取值于CdpSysRoleEntity.id"),
    })
    @PutMapping(value = "/users/{roleId}")
    public ResponseEntity<List<CdpSysUserRoleEntity>> putUsers(@RequestBody List<CdpSysUserRoleEntity> entities,
                                                               @PathVariable("roleId") Integer roleId) {
        return ResponseEntity.ok(userRoleService.updateInBatchByRoleId(roleId, entities));
    }

    @ApiOperation("根据用户唯一id查找用户目前不拥有的所有角色信息")
    @ApiImplicitParam(name = "roleId", value = "角色ID,取值于CdpSysRoleEntity.id")
    @RequestMapping(value = "/otherUsers/{roleId}", method = {RequestMethod.POST})
    public ResponseEntity<List<CdpSysUserEntity>> getOtherUsers(@PathVariable("roleId") Integer roleId) throws CdpControllerException {
        return ResponseEntity.ok(userService.findOtherUsersByRoleId(roleId));
    }

    @Override
    public ISimpleService<CdpSysRoleEntity, Integer> getService() {
        return roleService;
    }
}
