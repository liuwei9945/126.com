package com.api.cs.demo02_22c545406a174abfaea9deabcc930fa2.service.Impl;

import com.api.cs.demo02_22c545406a174abfaea9deabcc930fa2.cmd.GetFormDemoCmd;
import com.api.cs.demo02_22c545406a174abfaea9deabcc930fa2.service.DemoService02;
import com.engine.core.impl.Service;

import java.util.Map;

/*
 * @Author      :wyl
 * @Date        :2019/4/19  11:47
 * @Version 1.0 :
 * @Description :
 **/
public class DemoServiceImpl02 extends Service implements DemoService02 {
    @Override
    public Map<String, Object> getFormDemo(Map<String, Object> params) {
        return commandExecutor.execute(new GetFormDemoCmd(user,params));
    }
}
