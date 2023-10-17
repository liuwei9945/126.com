package com.api.cs.demo04_3b8e7ad2f737484d8fca0561a8b0dc1a.service;

import java.util.Map;

public interface DemoService04 {
    /**
     * 获取form表单
     * @param params
     * @return
     */
    Map<String, Object> weatableDemo(Map<String, Object> params);

    /**
     * 获取高级搜索条件(含tab页)
     * @param params
     * @return
     */
    Map<String,Object> weatableConditonDemo(Map<String, Object> params);
}
