package com.apihub.pay.model.dto.pay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@ApiModel(description = "充值下单表单实体")
public class ChargePayDTO {
    @ApiModelProperty("充值金额必须为正数")
    @Min(value = 1, message = "支付金额必须为正数")
    private Integer amount;

}