package com.github.fosin.anan.pojo.dto.request;

import com.github.fosin.anan.util.RegexUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 系统角色表(AnanRole)更新DTO
 *
 * @author fosin
 * @date 2019-01-27 15:56:07
 * @since 1.0.0
 */
@Data
@ApiModel(value = "系统角色表更新DTO", description = "表(anan_role)的对应的更新DTO")
public class AnanRoleUpdateDto implements Serializable {
    private static final long serialVersionUID = -79223984618301733L;

    @NotNull(message = "角色ID" + "{javax.validation.constraints.NotNull.message}")
    @ApiModelProperty(value = "角色ID, 主键", required = true)
    private Long id;

    @NotNull(message = "机构ID" + "{javax.validation.constraints.NotNull.message}")
    @ApiModelProperty(value = "机构ID", required = true)
    private Long organizId;

    @NotBlank(message = "角色名称" + "{javax.validation.constraints.NotBlank.message}")
    @ApiModelProperty(value = "角色名称", required = true)
    @Pattern(regexp = RegexUtil.SPECIAL, message = "名称不能包含特殊字符")
    private String name;

    @NotBlank(message = "角色标识" + "{javax.validation.constraints.NotBlank.message}")
    @ApiModelProperty(value = "角色标识", required = true)
    @Pattern(regexp = "[\\w]{1,40}", message = "角色标识只能大小写字母、数字、下杠(_)组合而成,长度不超过40位")
    private String value;

    @ApiModelProperty(value = "角色说明")
    private String tips;

    @NotNull(message = "使用状态" + "{javax.validation.constraints.NotNull.message}")
    @Max(value = 1)
    @Min(value = 0)
    @ApiModelProperty(value = "使用状态：0=启用，1=禁用，具体取值于字典表anan_dictionary.id=11", required = true)
    private Integer status;
}
