package com.api.cs.demo03_556a9653a4c94e5cbcc2a763d1c3c423.service.Impl;

import com.api.cs.demo03_556a9653a4c94e5cbcc2a763d1c3c423.cmd.WeaTableDemoCmd;
import com.api.cs.demo03_556a9653a4c94e5cbcc2a763d1c3c423.cmd.WeatableConditonDemoCmd;
import com.api.cs.demo03_556a9653a4c94e5cbcc2a763d1c3c423.service.DemoService03;
import com.engine.core.impl.Service;


import java.util.Map;

/*
 * @Author      :wyl
 * @Date        :2019/4/19  11:47
 * @Version 1.0 :
 * @Description :
 **/
public class DemoServiceImpl03 extends Service implements DemoService03 {

    @Override
    public Map<String, Object> weatableDemo(Map<String, Object> params) {
        return commandExecutor.execute(new WeaTableDemoCmd(user,params));
    }

    @Override
    public Map<String, Object> weatableConditonDemo(Map<String, Object> params) {
        return commandExecutor.execute(new WeatableConditonDemoCmd(user,params));
    }
}
