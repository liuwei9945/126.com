package com.weaver.esb.package_20231010024143;

import java.util.*;

import com.alibaba.fastjson.JSON;
import weaver.conn.RecordSet;
import weaver.general.Util;

import com.cloudstore.dev.api.bean.MessageBean;
import com.cloudstore.dev.api.bean.MessageType;
import com.cloudstore.dev.api.util.Util_Message;
import weaver.hrm.resource.ResourceComInfo;
public class class_20231010024143 {
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
            int userid = Util.getIntValue(Util.null2String(params.get("userid")), 1);//发起人
            String bh = "";
            String cjrname="";
            String cjtime="";
            String title = "待办提醒:";// 标题
            StringBuffer tablesql = new StringBuffer(
                    "select requestnamenew ,creater,createdate,createtime  from workflow_requestbase where requestid=?");
            rs.executeQuery(tablesql.toString(),requestid);
            if (rs.next()) {
                title = Util.null2String(rs.getString("requestnamenew"));
                cjrname= resinfo.getLastname(Util.null2String(rs.getString("creater")));
                cjtime=Util.null2String(rs.getString("createdate"))+" "+Util.null2String(rs.getString("createtime"));
            }


            String linkUrl = "/spa/workflow/static4form/index.html?#/main/workflow/req?requestid="+requestid;
            String linkMobileUrl = "/spa/workflow/static4mobileform/index.html?#/req?requestid="+requestid;
            String context = "你好：你有一条待办需要审核请及时处理 ";// "你好："+xm+ " ! \n\r 您提供的"+gs+
            //添加创建人和创建时间
            context+="<br>\r\n 创建人："+cjrname+"<br>\r\n创建时间："+cjtime;
            // "暂无匹配信息，请确认公司名称后重新提交。";
           // List<String> userIdList = getUseidByResquestID(requestid);// 接收人id

            Set<String> userIdList =getUseidByResquestID(requestid);// 接收人id

            ret.put("userIdList", JSON.toJSON(userIdList));
            /** 模板发送**/
            int messageCode = 1288;//消息来源
            String message = "发送成功";
            boolean issend = true;
            try {
                MessageType messageType = MessageType.newInstance(messageCode); // 消息来源（见文档第四点补充）
                MessageBean messageBean = Util_Message.createMessage(messageType,
                       userIdList, title, context, linkUrl, linkMobileUrl);
                messageBean.setCreater(userid);// 创建人id
                // messageBean.setTargetId("121|22"); //消息来源code +“|”+业务id 需要修改消息状态时传入
                ret.put("messageBean", JSON.toJSON(messageBean));
                issend = Util_Message.store(messageBean);
                ret.put("issend", issend);
            } catch (Exception s) {
                s.printStackTrace();
                ret.put("messageBeanerr", s.getMessage());
            }

            if (issend) {
                message = "接收人："+username+"待办提醒发送成功";
            }

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
