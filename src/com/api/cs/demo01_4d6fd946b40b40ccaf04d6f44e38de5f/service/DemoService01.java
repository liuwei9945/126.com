package com.api.cs.demo01_4d6fd946b40b40ccaf04d6f44e38de5f.service;

import java.util.Map;

public interface DemoService01 {
    /**
     * 获取form表单
     * @param params
     * @return
     */
    Map<String, Object> getFormDemo(Map<String, Object> params);


    /**
     * 保存数据
     * @param params
     * @return
     */
    Map<String, Object> saveFormDemo(Map<String, Object> params);


}
