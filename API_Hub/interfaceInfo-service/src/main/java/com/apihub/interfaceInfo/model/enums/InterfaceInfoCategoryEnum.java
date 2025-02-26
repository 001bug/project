package com.apihub.interfaceInfo.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息状态枚举
 */
public enum InterfaceInfoCategoryEnum {

    THIRD_PARTY_API("第三方接口", 0),
    SELF_API("自营接口", 1);

    private final String text;

    private final int value;

    InterfaceInfoCategoryEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
