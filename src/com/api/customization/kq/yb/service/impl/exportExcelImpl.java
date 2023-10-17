package com.api.customization.kq.yb.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocReplaceComponent;
import com.weaverboot.frame.ioc.anno.methodAnno.WeaReplaceAfter;
import com.weaverboot.frame.ioc.handler.replace.weaReplaceParam.impl.WeaAfterReplaceParam;

/**
 * 考勤数组自定义开发逻辑处理重写
 */
@WeaIocReplaceComponent("exportExcelService") //如不标注名称，则按类的全路径注入
public class exportExcelImpl {
    //这个是接口后置方法，大概的用法跟前置方法差不多，稍有差别
    //注解名称为WeaReplaceAfter
    //返回类型必须为String
    //参数叫WeaAfterReplaceParam，这个类前四个参数跟前置方法的那个相同，不同的是多了一个叫data的String，这个是那个接口执行完返回的报文
    //你可以对那个报文进行操作，然后在这个方法里return回去
    @WeaReplaceAfter(value = "/api/kq/report/exportExcel",order = 1,description = "考勤报表增加导出夜班数据")
    public String after(WeaAfterReplaceParam weaAfterReplaceParam){

        String data = weaAfterReplaceParam.getData();//这个就是接口执行完的报文
       // data = "修改后的报文";
        System.out.println("weaAfterReplaceParam = " + data);

        //
        JSONObject newdata= JSONObject.parseObject(data);
        //修改内容
        JSONArray datas=newdata.getJSONArray("datas");
        for (int i = 0; i < datas.size(); i++) {
            // 获取当前位置的 JSON 对象
            JSONObject jsonObject = datas.getJSONObject(i);

            // 在 JSON 对象中获取想要的属性值
            String name = jsonObject.getString("yb");
            int resourceId = jsonObject.getIntValue("resourceId");

            // 进行相应的操作
            // ...
            if(resourceId>0) {
                jsonObject.put("yb", i);
                System.out.println("设置用户夜班只 = " + resourceId +"i"+i+"name"+name);
            }
        }

        System.out.println("newdata = " + newdata.toJSONString());

        return newdata.toJSONString();
    }
}
