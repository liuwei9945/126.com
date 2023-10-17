package com.api.cs.demo01_4d6fd946b40b40ccaf04d6f44e38de5f.service.Impl;

import com.api.cs.demo01_4d6fd946b40b40ccaf04d6f44e38de5f.cmd.GetFormDemoCmd;
import com.api.cs.demo01_4d6fd946b40b40ccaf04d6f44e38de5f.cmd.SaveFormDemoCmd;
import com.api.cs.demo01_4d6fd946b40b40ccaf04d6f44e38de5f.service.DemoService01;
import com.engine.core.impl.Service;


import java.util.Map;

/*
 * @Author      :wyl
 * @Date        :2019/4/19  11:47
 * @Version 1.0 :
 * @Description :
 **/
public class DemoServiceImpl01 extends Service implements DemoService01 {
    @Override
    public Map<String, Object> getFormDemo(Map<String, Object> params) {
        return commandExecutor.execute(new GetFormDemoCmd(user,params));
    }

    @Override
    public Map<String, Object> saveFormDemo(Map<String, Object> params) {
        return commandExecutor.execute(new SaveFormDemoCmd(user,params));
    }
}
