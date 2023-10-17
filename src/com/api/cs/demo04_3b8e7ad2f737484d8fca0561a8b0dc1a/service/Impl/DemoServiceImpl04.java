package com.api.cs.demo04_3b8e7ad2f737484d8fca0561a8b0dc1a.service.Impl;

import com.api.cs.demo04_3b8e7ad2f737484d8fca0561a8b0dc1a.cmd.WeaTableDemoCmd;
import com.api.cs.demo04_3b8e7ad2f737484d8fca0561a8b0dc1a.cmd.WeatableConditonDemoCmd;
import com.api.cs.demo04_3b8e7ad2f737484d8fca0561a8b0dc1a.service.DemoService04;
import com.engine.core.impl.Service;

import java.util.Map;

/*
 * @Author      :wyl
 * @Date        :2019/4/19  11:47
 * @Version 1.0 :
 * @Description :
 **/
public class DemoServiceImpl04 extends Service implements DemoService04 {

    @Override
    public Map<String, Object> weatableDemo(Map<String, Object> params) {
        return commandExecutor.execute(new WeaTableDemoCmd(user,params));
    }

    @Override
    public Map<String, Object> weatableConditonDemo(Map<String, Object> params) {
        return commandExecutor.execute(new WeatableConditonDemoCmd(user,params));
    }
}
