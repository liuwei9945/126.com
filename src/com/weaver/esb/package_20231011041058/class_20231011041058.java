package com.weaver.esb.package_20231011041058;

import weaver.conn.RecordSet;
import weaver.general.Util;

import java.util.*;

public class class_20231011041058 {

    /**
     * @param:  param(Map collections)
     * 参数名称不能包含特殊字符+,.[]!"#$%&'()*:;<=>?@\^`{}|~/ 中文字符、标点 U+007F U+0000到U+001F
     */
    public Map execute(Map<String,Object> params) {
        Map<String,Object> apidatas  = new HashMap<>();



        apidatas.put("hasRight", true);

        /**
         * 获取表单页面参数
         * 这里只取了表单的有一个参数
         */
        String input =  Util.null2String(params.get("input"));

        /**
         * 数据库操作的工具类
         */
        RecordSet recordSet = new RecordSet();

        String sql = "insert into ECOLOGY_PC_DEMO (demo_input) values (?)";
        recordSet.executeUpdate(sql,UUID.randomUUID().toString(),input);

        return apidatas;

    }
}