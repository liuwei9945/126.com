package com.weaver.esb.package_20231010044326;


import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.resource.ResourceComInfo;

import java.util.*;

public class class_20231010044326 {
    RecordSet rs = new RecordSet();
    String username="";
    ResourceComInfo resinfo ;

    /**
     * @param: param(Map collections)
     * 参数名称不能包含特殊字符+,.[]!"#$%&'()*:;<=>?@\^`{}|~/ 中文字符、标点 U+007F U+0000到U+001F
     */
    public Map execute(Map<String, Object> params) {
        // 示例：data：定义的请求数据，code:定义的响应数据
        // String data = params.get("data");
        // ……
        Map<String, Object> ret = new HashMap<>();


        try {
            resinfo = new ResourceComInfo();
            String requestid = Util.null2String(params.get("requestid"));
            Set<String> userIdList =getUseidByResquestID(requestid);// 接收人id
            String   message = "接收人："+username+"您是否确认发送消息？";
            ret.put("code", 0);
            ret.put("msg", message);
        } catch (Exception s) {
            s.printStackTrace();
            ret.put("code", 1);
            ret.put("msg", s.getMessage());
        }


        return ret;

    }



    /**
     * 根据请求编号获取当前节点用户用于提交使用
     *
     * @param requestid 请求编号
     * @return
     * @throws Exception
     */
    public Set<String> getUseidByResquestID(String requestid) throws Exception {
        String userid = "";
        Set<String> userlist = new HashSet<>();
        StringBuffer tablesql = new StringBuffer(
                "select userid from workflow_currentoperator where requestid='")
                .append(requestid)
                .append("' and nodeid in (select nownodeid from workflow_nownode where requestid='")
                .append(requestid).append("') and isremark in(0)");

        try {
            rs.execute(tablesql.toString());
            while (rs.next()) {
                userid = Util.null2String(rs.getString("userid"));
                userlist.add(userid);
                username+= resinfo.getLastname(userid)+"、";
            }

            return userlist;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

}