package com.starlight.cdp.platformapi.service;


import com.starlight.cdp.core.exception.CdpServiceException;
import com.starlight.cdp.mvc.module.PageModule;
import com.starlight.cdp.mvc.result.Result;
import com.starlight.cdp.mvc.result.ResultUtils;
import com.starlight.cdp.platformapi.constant.TableNameConstant;
import com.starlight.cdp.platformapi.entity.CdpSysOrganizationEntity;
import com.starlight.cdp.platformapi.entity.CdpSysUserEntity;
import com.starlight.cdp.platformapi.repository.OrganizationRepository;
import com.starlight.cdp.platformapi.service.inter.IOrganizationService;
import com.starlight.cdp.platformapi.util.LoginUserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 2017/12/29.
 * Time:12:31
 *
 * @author fosin
 */
@Service
public class OrganizationServiceImpl implements IOrganizationService {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    @CacheEvict(value = TableNameConstant.CDP_SYS_ORGANIZATION, key = "#entity.id")
    public CdpSysOrganizationEntity create(CdpSysOrganizationEntity entity) throws CdpServiceException {
        Assert.notNull(entity, "传入了空对象!");
        Assert.isTrue(entity.getpId() != null, "无效的父机构编码!");
        CdpSysUserEntity loginUser = LoginUserUtil.getUser();
        entity.setCreateBy(loginUser.getId());
        entity.setUpdateBy(loginUser.getId());
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        return organizationRepository.save(entity);
    }

    @Override
    @CacheEvict(value = TableNameConstant.CDP_SYS_ORGANIZATION, key = "#entity.id")
    public CdpSysOrganizationEntity update(CdpSysOrganizationEntity entity) throws CdpServiceException {
        Assert.notNull(entity, "传入了空对象!");
        Assert.isTrue(entity.getId() != null, "无效的ID!");

        CdpSysUserEntity loginUser = LoginUserUtil.getUser();
        entity.setUpdateBy(loginUser.getId());
        entity.setUpdateTime(new Date());
        return organizationRepository.save(entity);
    }

    @Override
    @Cacheable(value = TableNameConstant.CDP_SYS_ORGANIZATION, key = "#id")
    public CdpSysOrganizationEntity findOne(Integer id) {
        return organizationRepository.findOne(id);
    }

    @Override
    @CacheEvict(value = TableNameConstant.CDP_SYS_ORGANIZATION, key = "#id")
    public CdpSysOrganizationEntity delete(Integer id) throws CdpServiceException {
        Assert.notNull(id, "传入了空ID!");
        List<CdpSysOrganizationEntity> entities = findByPid(id);
        Assert.isTrue(entities == null || entities.size() == 0, "该节点还存在子节点不能直接删除!");
        organizationRepository.delete(id);
        return null;
    }

    @Override
    @CacheEvict(value = TableNameConstant.CDP_SYS_ORGANIZATION, key = "#entity.id")
    public Collection<CdpSysOrganizationEntity> delete(CdpSysOrganizationEntity entity) throws CdpServiceException {
        Assert.notNull(entity, "传入了空对象!");
        Assert.notNull(entity.getId(), "传入了空ID!");
        List<CdpSysOrganizationEntity> entities = findByPid(entity.getId());
        Assert.isTrue(entities == null || entities.size() == 0, "该节点还存在子节点不能直接删除!");
        organizationRepository.delete(entity);
        return null;
    }

    @Override
    public Collection<CdpSysOrganizationEntity> findAll() {
        Sort sort = new Sort("code");
        return organizationRepository.findAll(sort);
    }

    public String getCacheName() {
        return "AllData";
    }

    @Override
    public Result findAllPage(PageModule pageModule) {
        PageRequest pageable = new PageRequest(pageModule.getPageNumber() - 1, pageModule.getPageSize(), Sort.Direction.fromString(pageModule.getSortOrder()), pageModule.getSortName());
        String searchCondition = pageModule.getSearchText();

        Specification<CdpSysOrganizationEntity> condition = new Specification<CdpSysOrganizationEntity>() {
            @Override
            public Predicate toPredicate(Root<CdpSysOrganizationEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (StringUtils.isBlank(searchCondition)) {
                    return query.getRestriction();
                }
                Path<String> id = root.get("id");
                Path<String> name = root.get("name");
                Path<String> fullname = root.get("fullname");
                Path<String> address = root.get("address");
                return cb.or(cb.like(id, "%" + searchCondition + "%"), cb.like(name, "%" + searchCondition + "%"), cb.like(fullname, "%" + searchCondition + "%"), cb.like(address, "%" + searchCondition + "%"));
            }
        };
        //分页查找
        Page<CdpSysOrganizationEntity> page = organizationRepository.findAll(condition, pageable);

        return ResultUtils.success(page.getTotalElements(), page.getContent());
    }

    @Override
    public List<CdpSysOrganizationEntity> findByPid(Integer pid) throws CdpServiceException {
        return organizationRepository.findByPIdOrderByCodeAsc(pid);
    }

    @Override
    public List<CdpSysOrganizationEntity> findByCodeStartingWith(String code) throws CdpServiceException {
        return organizationRepository.findByCodeStartingWithOrderByCodeAsc(code);
    }
}
