package com.apihub.common.common;

import lombok.Data;

/**
 * 分页请求
 *

 */
@Data
public class PageRequest {

    //升序
    private static final String SORT_ORDER_ASC = "ascend";

    //降序
    private static final String SORT_ORDER_DESC = "descend";

    /**
     * 当前页号
     */
    private Long current = 1L;

    /**
     * 页面大小
     */
    private Long pageSize = 10L;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = SORT_ORDER_ASC;
}
