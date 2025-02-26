package com.apihub.voucher.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName voucher_info
 */
@TableName(value = "voucher_info")
@Data
public class VoucherInfo implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 代金券标题
     */
    private String title;
    /**
     * 副标题
     */
    private String subTitle;
    /**
     * 使用规则
     */
    private String rules;
    /**
     * 优惠接口id
     */
    private Long interfaceId;
    /**
     * 秒杀id
     */
    private Long seckillId;
    /**
     * 支付金额，单位是分。例如200代表2元
     */
    private Long payValue;
    /**
     * 抵扣金额，单位是分。例如200代表2元
     */
    private Long actualValue;
    /**
     * 0,余额券；1,时段卡
     */
    private Integer type;
    /**
     * 1,上架; 2,下架; 3,过期
     */
    private Integer status;
    /**
     * 提供者id
     */
    private Long issuerId;
    /**
     * 兑换码
     */
    private String activationCode;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 拓展字段
     */
    private String otherInfo;
    /**
     * 是否删除
     */
    private Integer isDelete;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        VoucherInfo other = (VoucherInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
                && (this.getSubTitle() == null ? other.getSubTitle() == null : this.getSubTitle().equals(other.getSubTitle()))
                && (this.getRules() == null ? other.getRules() == null : this.getRules().equals(other.getRules()))
                && (this.getInterfaceId() == null ? other.getInterfaceId() == null : this.getInterfaceId().equals(other.getInterfaceId()))
                && (this.getSeckillId() == null ? other.getSeckillId() == null : this.getSeckillId().equals(other.getSeckillId()))
                && (this.getPayValue() == null ? other.getPayValue() == null : this.getPayValue().equals(other.getPayValue()))
                && (this.getActualValue() == null ? other.getActualValue() == null : this.getActualValue().equals(other.getActualValue()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getIssuerId() == null ? other.getIssuerId() == null : this.getIssuerId().equals(other.getIssuerId()))
                && (this.getActivationCode() == null ? other.getActivationCode() == null : this.getActivationCode().equals(other.getActivationCode()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getOtherInfo() == null ? other.getOtherInfo() == null : this.getOtherInfo().equals(other.getOtherInfo()))
                && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getSubTitle() == null) ? 0 : getSubTitle().hashCode());
        result = prime * result + ((getRules() == null) ? 0 : getRules().hashCode());
        result = prime * result + ((getInterfaceId() == null) ? 0 : getInterfaceId().hashCode());
        result = prime * result + ((getSeckillId() == null) ? 0 : getSeckillId().hashCode());
        result = prime * result + ((getPayValue() == null) ? 0 : getPayValue().hashCode());
        result = prime * result + ((getActualValue() == null) ? 0 : getActualValue().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getIssuerId() == null) ? 0 : getIssuerId().hashCode());
        result = prime * result + ((getActivationCode() == null) ? 0 : getActivationCode().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getOtherInfo() == null) ? 0 : getOtherInfo().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", title=").append(title);
        sb.append(", subTitle=").append(subTitle);
        sb.append(", rules=").append(rules);
        sb.append(", interfaceId=").append(interfaceId);
        sb.append(", seckillId=").append(seckillId);
        sb.append(", payValue=").append(payValue);
        sb.append(", actualValue=").append(actualValue);
        sb.append(", type=").append(type);
        sb.append(", status=").append(status);
        sb.append(", issuerId=").append(issuerId);
        sb.append(", activationCode=").append(activationCode);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", otherInfo=").append(otherInfo);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}