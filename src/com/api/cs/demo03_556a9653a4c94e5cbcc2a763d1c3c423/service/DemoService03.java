package com.api.cs.demo03_556a9653a4c94e5cbcc2a763d1c3c423.service;

import java.util.Map;

public interface DemoService03 {
    /**
     * 获取form表单
     * @param params
     * @return
     */
    Map<String, Object> weatableDemo(Map<String, Object> params);

    /**
     * 获取高级搜索条件
     * @param params
     * @return
     */
    Map<String,Object> weatableConditonDemo(Map<String, Object> params);
}
