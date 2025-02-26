package com.apihub.pay.model.dto.pay.payvoucherorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "支付下单表单实体")
public class VoucherBalancePayDTO {
    @ApiModelProperty(value = "VoucherOrder", required = true)
    @NotNull(message = "VoucherOrder不能为空")
    private VoucherOrderVO voucherOrderVO;

    @ApiModelProperty(value = "支付金额", required = true)
    @Min(value = 1, message = "支付金额必须为正数")
    private Integer amount;

}