package com.weaver.esb.package_20230920044323;


import com.alibaba.fastjson.JSONObject;

import com.engine.common.util.ServiceUtil;
import com.engine.workflow.biz.freeNode.FreeNodeBiz;
import com.engine.workflow.biz.publicApi.RequestOperateBiz;
import com.engine.workflow.entity.publicApi.PAResponseEntity;
import com.engine.workflow.entity.publicApi.ReqOperateRequestEntity;
import com.engine.workflow.entity.publicApi.WorkflowDetailTableInfoEntity;
import com.engine.workflow.publicApi.WorkflowRequestOperatePA;
import com.engine.workflow.publicApi.impl.WorkflowRequestOperatePAImpl;
import com.engine.workflow.util.CommonUtil;
import com.engine.workflow.util.HttpServletRequestUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.lang3.StringUtils;
import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.workflow.webservices.WorkflowRequestTableField;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class class_20230920044323 {
    RecordSet rs = new RecordSet();
    static Map<String, String> workcodemap = new HashMap<>();
    static Map<String, String> telephonemap = new HashMap<>();
    static Map<String, String> certificatenummap = new HashMap<>();
    static Map<String, String> deptmmap = new HashMap<>();
    static Map<String, String> companymmap = new HashMap<>();


    /**
     * @param: param(Map collections)
     * 参数名称不能包含特殊字符+,.[]!"#$%&'()*:;<=>?@\^`{}|~/ 中文字符、标点 U+007F U+0000到U+001F
     */
    public Map execute(Map<String, Object> params) throws Exception {
        // 示例：data：定义的请求数据，code:定义的响应数据
        //转换规则默认不转换工号0手机1身份证号2
        String usercode = Util.null2String(params.get("usercode"));//创建用户
        String data = Util.null2String(params.get("data"));
        Map<String, Object> ret = new HashMap<>();
       // ret.put("ysdata", data);

        if ("".equals(data)) {
            ret.put("code", "PARAM_ERROR");
            ret.put("errMsg", "请求数据不存在任何内容请调整！！！");
            return ret;
        }

        // ……

        // rules="0";
        //初始化人员
        getinitHrm();
        getinitDept();
        getinitSubCompay();

        ret.put("workcodemap", workcodemap);
        //根据转换规则计算OAID
        int userid = Util.getIntValue(getHrmidByRules(usercode));
        if (userid <= 0) {
            ret.put("code", "PARAM_ERROR");
            ret.put("errMsg", "用户" + usercode + "转换失败或不存在该用户信息！！！");
            return ret;

        } else {
            ret.put("userid", userid);
            //用户存在验证表单字段解析data
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(data);

            replaceFieldValue(jsonNode);

            String updatedJson = objectMapper.writeValueAsString(jsonNode);
            ret.put("newdata", updatedJson);
            JSONObject dataobj = JSONObject.parseObject(updatedJson);
            //获取传递参数
            WorkflowRequestOperatePA requestId = this.getRequestOperatePA();
            User requestName = new User(userid);
            //  log.info("~~~~doCreateRequest接口报文~~~~userId:" + (requestName == null ? null : requestName.getUID()));
            PAResponseEntity secLevel = requestId.doCreateRequest(requestName, request2Entity(dataobj));

            ret.put("dataobj", JSONObject.toJSONString(request2Entity(dataobj)));
            ret.put("secLevel", JSONObject.toJSONString(secLevel));
            ret.put("code", secLevel.getCode());
            ret.put("data", secLevel.getData());
            if ("SUCCESS".equals(secLevel.getCode())) {
               // ret.put("data", secLevel.getData());
                //获取requestid
                ret.put("requestid", ((JSONObject)secLevel.getData()).getString("requestid"));
            }

            ret.put("errMsg", secLevel.getErrMsg());
            if (secLevel.getErrMsg().size()==0) {
                ret.put("errMsg", secLevel.getReqFailMsg());
            }


        }

        return ret;

    }

    public static ReqOperateRequestEntity request2Entity(JSONObject dataobj) {
        ReqOperateRequestEntity reqEntity = new ReqOperateRequestEntity();
        try {
            int workflowId = Util.getIntValue(dataobj.getString("workflowId"));
            int requestId = Util.getIntValue(dataobj.getString("requestId"));
            String requestName = Util.null2String(dataobj.getString("requestName"));
            String secLevel = Util.null2String(dataobj.getString("secLevel"));
            int userId = Util.getIntValue(dataobj.getString("userId"));
            int forwardFlag = Util.getIntValue(dataobj.getString("forwardFlag"));
            String forwardResourceIds = Util.null2String(dataobj.getString("forwardResourceIds"));
            String mainData = Util.null2String(dataobj.getString("mainData"));
            int isremind = Util.getIntValue(Util.null2String(dataobj.getString("isremind")), 1);
            if (!"".equals(mainData)) {
                try {
                    List var11 = JSONObject.parseArray(mainData, WorkflowRequestTableField.class);
                    reqEntity.setMainData(var11);
                } catch (Exception var18) {
                    var18.printStackTrace();
                }
            }

            String detailData = Util.null2String(dataobj.getString("detailData"));
            if (!"".equals(detailData)) {
                try {
                    List var12 = JSONObject.parseArray(detailData, WorkflowDetailTableInfoEntity.class);
                    reqEntity.setDetailData(var12);
                } catch (Exception var17) {
                    reqEntity.setIsDetailParseError("1");
                    var17.printStackTrace();
                }
            }

            String var23 = Util.null2String(dataobj.getString("remark"));
            String var13 = Util.null2String(dataobj.getString("requestLevel"));
            String var14 = Util.null2String(dataobj.getString("otherParams"));
            if (!"".equals(var14)) {
                try {
                    Map var15 = (Map) JSONObject.parseObject(var14, Map.class);
                    reqEntity.setOtherParams(var15);
                } catch (Exception var16) {
                    var16.printStackTrace();
                }
            }

            reqEntity.setWorkflowId(workflowId);
            reqEntity.setRequestId(requestId);
            reqEntity.setRequestName(requestName);
            reqEntity.setSecLevel(secLevel);
            reqEntity.setUserId(userId);
            reqEntity.setRemark(var23);
            reqEntity.setRequestLevel(var13);
            reqEntity.setForwardFlag(forwardFlag);
            reqEntity.setForwardResourceIds(forwardResourceIds);
            reqEntity.setClientIp("");
            reqEntity.setIsremind(isremind);
            int var24 = Util.getIntValue(Util.null2String(dataobj.getString("submitNodeId")));
            if (var24 > 0 || FreeNodeBiz.isFreeNode(var24)) {
                reqEntity.setSubmitNodeId(var24);
                reqEntity.setEnableIntervenor("1".equals(Util.null2s(dataobj.getString("enableIntervenor"), "1")));
                reqEntity.setSignType(Util.getIntValue(Util.null2String(dataobj.getString("SignType")), 0));
                reqEntity.setIntervenorid(Util.null2String(dataobj.getString("Intervenorid")));
            }

            // log.info("流程rest接口,调用参数如下: workflowId:" + workflowId + "  requestId:" + requestId + "  requestName:" + requestName + "  secLevel" + secLevel + " userId:" + userId + "  forwardFlag:" + forwardFlag + "  forwardResourceIds:" + forwardResourceIds + "remark:" + var23 + "  submitNodeId:" + var24 + "  mainDataStr:" + mainData + "  detailDataStr:" + detailData + "  otherParamStr:" + var14);
        } catch (Exception var20) {
            var20.printStackTrace();
            //  log.error(var20);
        }

        return reqEntity;
    }

    private WorkflowRequestOperatePA getRequestOperatePA() {
        return (WorkflowRequestOperatePAImpl) ServiceUtil.getService(WorkflowRequestOperatePAImpl.class);
    }

    /**
     * 转换规则默认不转换工号0手机1身份证号2
     *
     * @param fieldValue
     * @return
     */
    private String getHrmidByRules(String fieldValue) {
        String oaid = "";
        if (fieldValue.contains("_workcode")) {
            // 对fieldValue字段进行替换 根据工号转换
            fieldValue = fieldValue.replace("_workcode", "");
            //工号转换
            oaid = Util.null2String(workcodemap.get(fieldValue));//根据工号转换
        } else if (fieldValue.contains("_telephone")) {
            // 对fieldValue字段进行替换 根据手机号码转换
            fieldValue = fieldValue.replace("_telephone", "");
            //工号转换
            oaid = Util.null2String(telephonemap.get(fieldValue));//根据手机号码转换
        } else if (fieldValue.contains("_certificatenum")) {
            // 对fieldValue字段进行替换 根据手机号码转换
            fieldValue = fieldValue.replace("_certificatenum", "");
            //工号转换
            oaid = Util.null2String(certificatenummap.get(fieldValue));//根据手机号码转换
        } else {
            oaid = fieldValue;
        }


        return oaid;
    }

    /**
     * 人员缓存基础数据
     */
    private void getinitHrm() {
        String sql = "select  id,workcode, mobile telephone,certificatenum,lastname   from hrmresource where status >0 and status<=3";
        rs.executeQuery(sql);
        while (rs.next()) {
            if (!"".equals(Util.null2String(rs.getString("workcode")))) {
                workcodemap.put(Util.null2String(rs.getString("workcode")), Util.null2String(rs.getString("id")));
                workcodemap.put(Util.null2String(rs.getString("workcode")) + "name", Util.null2String(rs.getString("lastname")));

            }
            if (!"".equals(Util.null2String(rs.getString("telephone")))) {
                telephonemap.put(Util.null2String(rs.getString("telephone")), Util.null2String(rs.getString("id")));
                telephonemap.put(Util.null2String(rs.getString("telephone")) + "name", Util.null2String(rs.getString("lastname")));

            }
            if (!"".equals(Util.null2String(rs.getString("certificatenum")))) {
                certificatenummap.put(Util.null2String(rs.getString("certificatenum")), Util.null2String(rs.getString("id")));
                certificatenummap.put(Util.null2String(rs.getString("certificatenum")) + "name", Util.null2String(rs.getString("lastname")));

            }
        }

    }

    /**
     * 部门缓存基础数据
     */
    private void getinitDept() {
        String sql = "select id,departmentcode,departmentname from HrmDepartment where (canceled is null or canceled=0) and departmentcode is not null and departmentcode !=''";
        rs.executeQuery(sql);
        while (rs.next()) {
            if (!"".equals(Util.null2String(rs.getString("departmentcode")))) {
                deptmmap.put(Util.null2String(rs.getString("departmentcode")), Util.null2String(rs.getString("id")));
                deptmmap.put(Util.null2String(rs.getString("departmentcode")) + "name", Util.null2String(rs.getString("departmentname")));
            }

        }

    }

    /**
     * 部门缓存基础数据
     */
    private void getinitSubCompay() {
        String sql = "select id,subcompanycode,subcompanyname  from HrmSubCompany where (canceled is null or canceled=0) and subcompanycode is not null and subcompanycode !=''";
        rs.executeQuery(sql);
        while (rs.next()) {
            if (!"".equals(Util.null2String(rs.getString("subcompanycode")))) {
                companymmap.put(Util.null2String(rs.getString("subcompanycode")), Util.null2String(rs.getString("id")));
                companymmap.put(Util.null2String(rs.getString("subcompanycode")) + "name", Util.null2String(rs.getString("subcompanyname")));
            }

        }

    }


    private static void replaceFieldValue(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            JsonNode fieldValueNode = objectNode.get("fieldValue");

            // 如果fieldValue字段存在并且值包含_workcode，则进行替换
            if (fieldValueNode != null && fieldValueNode.isTextual()) {
                String fieldValue = fieldValueNode.asText();

                if (fieldValue.contains("_workcode")) {
                    System.out.println("fieldValue=" + fieldValue);
                    // 对fieldValue字段进行替换 根据工号转换
                    fieldValue = fieldValue.replace("_workcode", "");
                    //工号转换
                    System.out.println("fieldValueth=" + fieldValue);
                    fieldValue = Util.null2String(workcodemap.get(fieldValue));//根据工号转换
                    // 更新fieldValue字段的值
                    objectNode.put("fieldValue", fieldValue);
                    System.out.println("fieldValuezh=" + fieldValue);
                } else if (fieldValue.contains("_telephone")) {
                    // 对fieldValue字段进行替换 根据手机号码转换
                    fieldValue = fieldValue.replace("_telephone", "");
                    //工号转换
                    fieldValue = Util.null2String(telephonemap.get(fieldValue));//根据手机号码转换
                    // 更新fieldValue字段的值
                    objectNode.put("fieldValue", fieldValue);
                } else if (fieldValue.contains("_certificatenum")) {
                    // 对fieldValue字段进行替换 根据手机号码转换
                    fieldValue = fieldValue.replace("_certificatenum", "");
                    //工号转换
                    fieldValue = Util.null2String(certificatenummap.get(fieldValue));//根据手机号码转换
                    // 更新fieldValue字段的值
                    objectNode.put("fieldValue", fieldValue);
                } else if (fieldValue.contains("_departmentcode")) {
                    // 对fieldValue字段进行替换 根据手机号码转换
                    fieldValue = fieldValue.replace("_departmentcode", "");
                    //工号转换
                    fieldValue = Util.null2String(deptmmap.get(fieldValue));//根据手机号码转换
                    // 更新fieldValue字段的值
                    objectNode.put("fieldValue", fieldValue);
                } else if (fieldValue.contains("_subcompanycode")) {
                    // 对fieldValue字段进行替换 根据手机号码转换
                    fieldValue = fieldValue.replace("_subcompanycode", "");
                    //工号转换
                    fieldValue = Util.null2String(companymmap.get(fieldValue));//根据手机号码转换
                    // 更新fieldValue字段的值
                    objectNode.put("fieldValue", fieldValue);
                }
            }

            // 递归处理子节点
          //  objectNode.fields().forEachRemaining(entry -> replaceFieldValue(entry.getValue()));
        } else if (node.isArray()) {
            // 递归处理数组中的每个元素
            node.forEach(class_20230920044323::replaceFieldValue);
        }
    }


}
