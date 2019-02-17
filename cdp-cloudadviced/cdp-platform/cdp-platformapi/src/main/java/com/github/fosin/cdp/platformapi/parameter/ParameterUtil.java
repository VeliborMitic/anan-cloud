package com.github.fosin.cdp.platformapi.parameter;

import com.github.fosin.cdp.platformapi.dto.request.CdpParameterCreateDto;
import com.github.fosin.cdp.platformapi.dto.request.CdpParameterUpdateDto;
import com.github.fosin.cdp.platformapi.entity.CdpParameterEntity;
import com.github.fosin.cdp.platformapi.service.inter.IParameterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 数据库参数工具类
 *
 * @author fosin
 * @date 2018.8.1
 */
@Component
@Slf4j
public class ParameterUtil {
    private static IParameterService parameterService;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ParameterUtil(IParameterService parameterService) {
        ParameterUtil.parameterService = parameterService;
    }

    public static CdpParameterEntity getOrCreateParameter(Integer type, String scope, String name, String defaultValue, String description) throws RuntimeException {
        CdpParameterEntity parameterEntity = parameterService.findByTypeAndScopeAndName(type, scope, name);
        if (parameterEntity == null || parameterEntity.getId() == null) {
            parameterEntity = setParameter(type, scope, name, defaultValue, description);
        }
        return parameterEntity;
    }

    public static CdpParameterEntity getParameter(Integer type, String scope, String name) throws RuntimeException {
        return parameterService.findByTypeAndScopeAndName(type, scope, name);
    }

    public static synchronized CdpParameterEntity setParameter(Integer type, String scope, String name, String value, String description) throws RuntimeException {
        CdpParameterEntity entity = getParameter(type, scope, name);
        if (value == null) {
            value = "";
        }
        if (entity == null || entity.getId() == null) {
            CdpParameterCreateDto createEntity = new CdpParameterCreateDto();
            createEntity.setScope(scope);
            createEntity.setType(type);
            createEntity.setName(name);
            createEntity.setValue(value);
            createEntity.setDefaultValue(value);
            createEntity.setDescription(description);
            return parameterService.create(createEntity);
        }

        CdpParameterUpdateDto updateEntity = new CdpParameterUpdateDto();
        BeanUtils.copyProperties(entity, updateEntity);
        updateEntity.setValue(value);
        updateEntity.setType(type);
        updateEntity.setScope(scope);
        updateEntity.setName(name);
        updateEntity.setDescription(description);
        return parameterService.update(updateEntity);

    }

    public static IParameterService getParameterService() {
        return parameterService;
    }
}
