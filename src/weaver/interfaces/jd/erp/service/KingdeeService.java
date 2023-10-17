package weaver.interfaces.jd.erp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloudstore.dev.api.util.HttpManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weaver.general.BaseBean;
import weaver.interfaces.jd.erp.util.ERPUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KingdeeService extends BaseBean {


    ERPUtil erpUtil = new ERPUtil();

    private static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }

    private static String byteArrayToBase64(byte[] bytes) {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
    }

    private String login() throws Exception {
        HttpManager httpManager = new HttpManager();
        String sessionId = "";
        Map<String, Object> map = new LinkedHashMap();
        map.put("Acctid", erpUtil.AcctId);
        map.put("userName", erpUtil.UserName);
        map.put("password", erpUtil.PassWord);
        map.put("lcid", erpUtil.LCID);

        writeLog(JSON.toJSONString(map));
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.LoginUrl, JSON.toJSONString(map));
        JSONObject hfJson = JSONObject.parseObject(hf);
        if (hfJson.getBoolean("IsSuccessByAPI")) {
            sessionId = hfJson.getString("KDSVCSessionId");
        } else {
            throw new Exception("ERP抛错:" + hfJson.getString("Message"));
        }
        return sessionId;
    }

//    public JSONObject postFhsl(String requestid, String djnm, String flnm, String sl) throws Exception {
//
//        StringBuffer stringBuffer = new StringBuffer();
//
//        stringBuffer.append("{");
//        stringBuffer.append("    \"formid\": \"SAL_DELIVERYNOTICE\",");
//        stringBuffer.append("    \"data\": {");
//        stringBuffer.append("    \"NeedUpDateFields\": [\"FQty\"],");
//
//        stringBuffer.append("    \"IsDeleteEntry\": \"false\",");
//        stringBuffer.append("    \"IsAutoAdjustField\": \"true\",");
//        stringBuffer.append("    \"IsAutoSubmitAndAudit\": \"false\",");
//        stringBuffer.append("    \"Model\": {");
//        stringBuffer.append("        \"FID\": "+djnm+",");
//        stringBuffer.append("        \"FEntity\": [\n");
//        stringBuffer.append("            {");
//        stringBuffer.append("                \"FEntryID\": "+flnm+",");
//        stringBuffer.append("                \"FQty\": "+sl+"");
//        stringBuffer.append("            }");
//        stringBuffer.append("        ]");
//        stringBuffer.append("    }");
//        stringBuffer.append("}");
//        stringBuffer.append("}");
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        String sessionId = login();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("采购申请推送内容:" + stringBuffer);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, stringBuffer.toString(), head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("采购申请返回:" + hfJson.toJSONString());
//        erpUtil.setERPLog(requestid, "发货申请回传", stringBuffer.toString(), hfJson.toJSONString());
//        return hfJson;
//    }

    /**
     * 返回发货申请结果和内容
     * @param requestid 流程id
     * @param djnm erp内码
     * @param flnm 明细内码
     * @param sl 数量
     * @param wlgs 物流公司
     * @param wldh 物料单号
     * @param fhrq 发货日期
     * @return
     * @throws Exception
     */
    public JSONObject postFhsl(String requestid, String djnm, String flnm, String sl,String wlgs,String wldh,String fhrq) throws Exception {
    Map<String, Object> requestData = new HashMap<>();
    requestData.put("formid", "SAL_DELIVERYNOTICE");
    Map<String, Object> data = new HashMap<>();
    List<String> needUpDateFields = new ArrayList<>();
    needUpDateFields.add("FQty");
    needUpDateFields.add("F_QIGM_Text2");
    needUpDateFields.add("F_QIGM_Text3");
    needUpDateFields.add("F_QIGM_Date");
    data.put("NeedUpDateFields", needUpDateFields);
    data.put("IsDeleteEntry", "false");
    data.put("IsAutoAdjustField", "true");
    data.put("IsAutoSubmitAndAudit", "false");
    Map<String, Object> model = new HashMap<>();
    model.put("FID", djnm);
    List<Map<String, Object>> entityList = new ArrayList<>();
    Map<String, Object> entity = new HashMap<>();
    entity.put("FEntryID", flnm);
    entity.put("FQty", sl);
    entityList.add(entity);
    model.put("FEntity", entityList);

    List<Map<String, Object>> f_QIGM_Entity = new ArrayList<>();
    Map<String, Object> qigm_entity = new HashMap<>();
    qigm_entity.put("F_QIGM_Text2",wlgs); //物流公司
    qigm_entity.put("F_QIGM_Text3",wldh);//物流单号
    qigm_entity.put("F_QIGM_Date",fhrq+" 00:00:00");///发货时间
    f_QIGM_Entity.add(qigm_entity);
    model.put("F_QIGM_Entity", f_QIGM_Entity);
    data.put("Model", model);
    requestData.put("data", data);
    HttpManager httpManager = new HttpManager();
    Map<String, String> head = new HashMap<>();
    String sessionId = login();
    head.put("kdservice-sessionid", sessionId);
    writeLog("发货申请推送内容:" + JSON.toJSONString(requestData));
    String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, JSON.toJSONString(requestData), head);
    JSONObject hfJson = JSONObject.parseObject(hf);
    writeLog("发货申请返回:" + hfJson.toJSONString());
    erpUtil.setERPLog(requestid, "发货申请回传", JSON.toJSONString(requestData), hfJson.toJSONString());
    return hfJson;
}
    /**
     * 任务分解书推送erp
     *
     * @param taskDecompositionBill
     * @param taskDecompositionDto1s
     * @return
     * @throws Exception
     */
//    public JSONObject potQuest(String requestid, TaskDecompositionBill taskDecompositionBill, List<TaskDecompositionDto1> taskDecompositionDto1s) throws Exception {
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("{");
//        stringBuffer.append("  \"formid\": \"QIGM_XMRWFJS\",");
//        stringBuffer.append("  \"data\": {");
//        stringBuffer.append("    \"NeedUpDateFields\": [],");
//        stringBuffer.append("    \"NeedReturnFields\": [],");
//        stringBuffer.append("    \"IsDeleteEntry\": \"true\",");
//        stringBuffer.append("    \"SubSystemId\": \"\",");
//        stringBuffer.append("    \"IsVerifyBaseDataField\": \"false\",");
//        stringBuffer.append("    \"IsEntryBatchFill\": \"true\",");
//        stringBuffer.append("    \"ValidateFlag\": \"true\",");
//        stringBuffer.append("    \"NumberSearch\": \"true\",");
//        stringBuffer.append("    \"IsAutoAdjustField\": \"false\",");
//        stringBuffer.append("    \"InterationFlags\": \"\",");
//        stringBuffer.append("    \"IgnoreInterationFlag\": \"\",");
//        stringBuffer.append("    \"IsControlPrecision\": \"false\",");
//        stringBuffer.append("    \"IsAutoSubmitAndAudit\": \"true\",");
//        stringBuffer.append("    \"ValidateRepeatJson\": \"false\",");
//        stringBuffer.append("    \"Model\": {");
//        stringBuffer.append("      \"FID\": 0,");
//        stringBuffer.append("      \"F_QIGM_Date\": \"" + taskDecompositionBill.getF_QIGM_Date() + " 00:00:00\",");
//        stringBuffer.append("      \"F_QIGM_Text\": \"" + taskDecompositionBill.getF_QIGM_Text() + "\",");
//        stringBuffer.append("      \"F_QIGM_Text1\": \"" + taskDecompositionBill.getF_QIGM_Text1() + "\",");
//        stringBuffer.append("      \"F_QIGM_LCID\": \"" + taskDecompositionBill.getF_QIGM_LCID() + "\",");
//        stringBuffer.append("      \"F_QIGM_OADH\": \"" + taskDecompositionBill.getF_QIGM_OADH() + "\",");
//        stringBuffer.append("      \"FOADH1\": \"" + taskDecompositionBill.getFOADH1() + "\",");
//        stringBuffer.append("      \"FORGID\": {");
//        stringBuffer.append("        \"FNUMBER\": \"100\"");
//        stringBuffer.append("      },");
//        stringBuffer.append("      \"F_QIGM_Base1\": {");
//        stringBuffer.append("        \"FNUMBER\": \"" + taskDecompositionBill.getF_QIGM_Base1() + "\"");
//        stringBuffer.append("      },");
//        stringBuffer.append("      \"FEntity\": [");
//        int i = 0;
//        for (TaskDecompositionDto1 taskDecompositionDto1 : taskDecompositionDto1s) {
//            stringBuffer.append("        {");
//            stringBuffer.append("          \"F_QIGM_Base2\": {");
//            stringBuffer.append("            \"FNUMBER\": \"" + taskDecompositionDto1.getF_QIGM_Base2() + "\"");
//            stringBuffer.append("          },");
//            stringBuffer.append("          \"F_QIGM_Text2\": \"" + taskDecompositionDto1.getF_QIGM_Text2() + "\",");
//            stringBuffer.append("          \"F_QIGM_Text3\": \"" + taskDecompositionDto1.getF_QIGM_Text3() + "\",");
//            stringBuffer.append("          \"F_QIGM_Date1\": \"" + taskDecompositionDto1.getF_QIGM_Date1() + " 00:00:00\",");
//            stringBuffer.append("          \"F_QIGM_Date2\": \"" + taskDecompositionDto1.getF_QIGM_Date2() + " 00:00:00\",");
//            stringBuffer.append("          \"FEMPID\": {");
//            stringBuffer.append("            \"FSTAFFNUMBER\": \"" + taskDecompositionDto1.getF_QIGM_UserId() + "\"");
//            stringBuffer.append("          },");
//            stringBuffer.append("          \"F_QIGM_Base\": {");
//            stringBuffer.append("            \"FNUMBER\": \"" + taskDecompositionDto1.getF_QIGM_Base() + "\"");
//            stringBuffer.append("          }");
//            stringBuffer.append("        }");
//            if (i != taskDecompositionDto1s.size() - 1) {
//                stringBuffer.append("        ,");
//            }
//            i++;
//        }
//
//        stringBuffer.append("      ]");
//        stringBuffer.append("    }");
//        stringBuffer.append("  }");
//        stringBuffer.append("}");
//
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        String sessionId = login();
//        head.put("kdservice-sessionid", sessionId);
//
//        writeLog("任务分解推送内容:" + stringBuffer);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, stringBuffer.toString(), head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("任务分解返回:" + hfJson.toJSONString());
//        erpUtil.setERPLog(requestid, "任务分解", stringBuffer.toString(), hfJson.toJSONString());
//        return hfJson;
//    }
    /*
    public JSONObject potQuest(String requestid, TaskDecompositionBill taskDecompositionBill, List<TaskDecompositionDto1> taskDecompositionDto1s) throws Exception {
        Map<String, Object> jsonMap = new HashMap<>();

        // 设置顶层字段
        jsonMap.put("formid", "QIGM_XMRWFJS");
        Map<String, Object> dataMap = new HashMap<>();
        jsonMap.put("data", dataMap);

        // 设置data字段
        dataMap.put("NeedUpDateFields", Collections.emptyList());
        dataMap.put("NeedReturnFields", Collections.emptyList());
        dataMap.put("IsDeleteEntry", "true");
        dataMap.put("SubSystemId", "");
        dataMap.put("IsVerifyBaseDataField", "false");
        dataMap.put("IsEntryBatchFill", "true");
        dataMap.put("ValidateFlag", "true");
        dataMap.put("NumberSearch", "true");
        dataMap.put("IsAutoAdjustField", "true");
        dataMap.put("InterationFlags", "");
        dataMap.put("IgnoreInterationFlag", "");
        dataMap.put("IsControlPrecision", "false");
        dataMap.put("IsAutoSubmitAndAudit", "true");
        dataMap.put("ValidateRepeatJson", "false");

        Map<String, Object> modelMap = new HashMap<>();
        dataMap.put("Model", modelMap);

        // 设置Model字段
        modelMap.put("FID", 0);
        modelMap.put("F_QIGM_Date", taskDecompositionBill.getF_QIGM_Date() + " 00:00:00");
        modelMap.put("F_QIGM_Text", taskDecompositionBill.getF_QIGM_Text());
        modelMap.put("F_QIGM_Text1", taskDecompositionBill.getF_QIGM_Text1());
        modelMap.put("F_QIGM_LCID", taskDecompositionBill.getF_QIGM_LCID());
        modelMap.put("F_QIGM_OADH", taskDecompositionBill.getF_QIGM_OADH());
        modelMap.put("FOADH1", taskDecompositionBill.getFOADH1());

        Map<String, Object> forgidMap = new HashMap<>();
        forgidMap.put("FNUMBER", "100");
        modelMap.put("FORGID", forgidMap);

        Map<String, Object> fQIGM_Base1Map = new HashMap<>();
        fQIGM_Base1Map.put("FNUMBER", taskDecompositionBill.getF_QIGM_Base1());
        modelMap.put("F_QIGM_Base1", fQIGM_Base1Map);

        List<Map<String, Object>> fEntityList = new ArrayList<>();
        modelMap.put("FEntity", fEntityList);

        // 设置FEntity字段
        for (TaskDecompositionDto1 taskDecompositionDto1 : taskDecompositionDto1s) {
            Map<String, Object> fEntityMap = new HashMap<>();
            fEntityMap.put("F_QIGM_Base2", Collections.singletonMap("FNUMBER", taskDecompositionDto1.getF_QIGM_Base2()));
            fEntityMap.put("F_QIGM_Text2", taskDecompositionDto1.getF_QIGM_Text2());
            fEntityMap.put("F_QIGM_Text3", taskDecompositionDto1.getF_QIGM_Text3());
            fEntityMap.put("F_QIGM_Date1", taskDecompositionDto1.getF_QIGM_Date1() + " 00:00:00");
            fEntityMap.put("F_QIGM_Date2", taskDecompositionDto1.getF_QIGM_Date2() + " 00:00:00");
            fEntityMap.put("FEMPID", Collections.singletonMap("FSTAFFNUMBER", taskDecompositionDto1.getF_QIGM_UserId()));
            fEntityMap.put("F_QIGM_Base", Collections.singletonMap("FNUMBER", taskDecompositionDto1.getF_QIGM_Base()));

            fEntityList.add(fEntityMap);
        }

        String jsonString = JSONObject.toJSONString(jsonMap);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        String sessionId = login();
        head.put("kdservice-sessionid", sessionId);

        writeLog("任务分解推送内容:" + jsonString);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, jsonString, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("任务分解返回:" + hfJson.toJSONString());
        erpUtil.setERPLog(requestid, "任务分解", jsonString, hfJson.toJSONString());
        return hfJson;
    }
    /**
     * 采购申请创建
     *
     * @param purchaseRequestBill
     * @param purchaseRequestDto1s
     * @return
     * @throws Exception
     */
    /**
    public JSONObject potPurchase(String requestid, PurchaseRequestBill purchaseRequestBill, List<PurchaseRequestDto1> purchaseRequestDto1s) throws Exception {
        Map<String, Object> jsonMap = new HashMap<>();

        // 设置顶层字段
        jsonMap.put("formid", "PUR_Requisition");
        Map<String, Object> dataMap = new HashMap<>();
        jsonMap.put("data", dataMap);

        // 设置data字段
        dataMap.put("NeedUpDateFields", Collections.emptyList());
        dataMap.put("NeedReturnFields", Collections.emptyList());
        dataMap.put("IsDeleteEntry", "true");
        dataMap.put("SubSystemId", "");
        dataMap.put("IsVerifyBaseDataField", "false");
        dataMap.put("IsEntryBatchFill", "true");
        dataMap.put("ValidateFlag", "true");
        dataMap.put("NumberSearch", "true");
        dataMap.put("IsAutoAdjustField", "true");
        dataMap.put("IsAutoSubmitAndAudit", "true");
        dataMap.put("InterationFlags", "");
        dataMap.put("IgnoreInterationFlag", "");
        dataMap.put("IsControlPrecision", "false");
        dataMap.put("ValidateRepeatJson", "false");

        Map<String, Object> modelMap = new HashMap<>();
        dataMap.put("Model", modelMap);

        // 设置Model字段
        modelMap.put("FID", 0);

        Map<String, Object> fBillTypeIDMap = new HashMap<>();
        fBillTypeIDMap.put("FNUMBER", purchaseRequestBill.getFBillTypeID());
        modelMap.put("FBillTypeID", fBillTypeIDMap);

        modelMap.put("FApplicationDate", purchaseRequestBill.getFApplicationDate() + " 00:00:00");
        modelMap.put("FRequestType", "Material");

        Map<String, Object> fApplicationOrgIdMap = new HashMap<>();
        fApplicationOrgIdMap.put("FNumber", "100");
        modelMap.put("FApplicationOrgId", fApplicationOrgIdMap);

        Map<String, Object> fApplicantIdMap = new HashMap<>();
        fApplicantIdMap.put("FStaffNumber", purchaseRequestBill.getFApplicantId());
        modelMap.put("FApplicantId", fApplicantIdMap);

        Map<String, Object> fCurrencyIdMap = new HashMap<>();
        fCurrencyIdMap.put("FNumber", "CNY");
        modelMap.put("FCurrencyId", fCurrencyIdMap);

        modelMap.put("FNote", purchaseRequestBill.getFNote());
        modelMap.put("FISPRICEEXCLUDETAX", true);

        Map<String, Object> fExchangeTypeIdMap = new HashMap<>();
        fExchangeTypeIdMap.put("FNumber", "HLTX01_SYS");
        modelMap.put("FExchangeTypeId", fExchangeTypeIdMap);

        modelMap.put("FIsConvert", false);
        modelMap.put("FACCTYPE", "Q");

        Map<String, Object> fSRMZZFLMap = new HashMap<>();
        fSRMZZFLMap.put("FNumber", purchaseRequestBill.getFSRMZZFL());
        modelMap.put("FSRMZZFL", fSRMZZFLMap);

        modelMap.put("FOAliuchengID", purchaseRequestBill.getRequestid());
        modelMap.put("FOAliuchengBH", purchaseRequestBill.getBh());

        Map<String, Object> fMobBillHeadMap = new HashMap<>();
        fMobBillHeadMap.put("FIsMobBill", false);
        fMobBillHeadMap.put("FMobIsPending", false);
        modelMap.put("FMobBillHead", fMobBillHeadMap);

        List<Map<String, Object>> fEntityList = new ArrayList<>();
        modelMap.put("FEntity", fEntityList);

        // 设置FEntity字段
        for (PurchaseRequestDto1 purchaseRequestDto1 : purchaseRequestDto1s) {
            Map<String, Object> fEntityMap = new HashMap<>();

            Map<String, Object> fRequireOrgIdMap = new HashMap<>();
            fRequireOrgIdMap.put("FNumber", "100");
            fEntityMap.put("FRequireOrgId", fRequireOrgIdMap);

            Map<String, Object> fMaterialIdMap = new HashMap<>();
            fMaterialIdMap.put("FNumber", purchaseRequestDto1.getFMaterialId());
            fEntityMap.put("FMaterialId", fMaterialIdMap);

            Map<String, Object> fQIGM_Base1Map = new HashMap<>();
            fQIGM_Base1Map.put("FSTAFFNUMBER", purchaseRequestDto1.getFPurchaserId());
            fEntityMap.put("F_QIGM_Base1", fQIGM_Base1Map);

            fEntityMap.put("FReqQty", purchaseRequestDto1.getFReqQty());
            fEntityMap.put("FApproveQty", purchaseRequestDto1.getFReqQty());
            fEntityMap.put("FArrivalDate", purchaseRequestDto1.getFArrivalDate() + " 00:00:00");
            fEntityMap.put("FEntryNote", purchaseRequestDto1.getFEntryNote());

            Map<String, Object> fPurchaseOrgIdMap = new HashMap<>();
            fPurchaseOrgIdMap.put("FNumber", "100");
            fEntityMap.put("FPurchaseOrgId", fPurchaseOrgIdMap);

            Map<String, Object> fReceiveOrgIdMap = new HashMap<>();
            fReceiveOrgIdMap.put("FNumber", "100");
            fEntityMap.put("FReceiveOrgId", fReceiveOrgIdMap);

            fEntityMap.put("FPriceUnitQty", purchaseRequestDto1.getFReqQty());
            fEntityMap.put("FREQSTOCKQTY", purchaseRequestDto1.getFReqQty());
            fEntityMap.put("FBaseReqQty", purchaseRequestDto1.getFReqQty());
            fEntityMap.put("FSalQty", purchaseRequestDto1.getFReqQty());
            fEntityMap.put("FSalBaseQty", purchaseRequestDto1.getFReqQty());
            fEntityMap.put("FIsVmiBusiness", false);

            Map<String, Object> fYFXMHMap = new HashMap<>();
            fYFXMHMap.put("FNumber", purchaseRequestBill.getXmbh());
            fEntityMap.put("FYFXMH", fYFXMHMap);

            fEntityList.add(fEntityMap);
        }

        String jsonString = JSONObject.toJSONString(jsonMap);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        String sessionId = login();
        head.put("kdservice-sessionid", sessionId);

        writeLog("采购申请推送内容:" + jsonString);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, jsonString, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("采购申请返回:" + hfJson.toJSONString());
        erpUtil.setERPLog(requestid, "采购申请", jsonString, hfJson.toJSONString());
        return hfJson;
    }

    /**
      public JSONObject potPurchase(String requestid, PurchaseRequestBill purchaseRequestBill, List<PurchaseRequestDto1> purchaseRequestDto1s) throws Exception {
          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("{");
          stringBuffer.append("  \"formid\": \"PUR_Requisition\",\n");
          stringBuffer.append("  \"data\": {\n");
          stringBuffer.append("    \"NeedUpDateFields\": [],\n");
          stringBuffer.append("   \"NeedReturnFields\": [],\n");
          stringBuffer.append("   \"IsDeleteEntry\": \"true\",\n");
          stringBuffer.append("    \"SubSystemId\": \"\",\n");
          stringBuffer.append("    \"IsVerifyBaseDataField\": \"false\",\n");
          stringBuffer.append("    \"IsEntryBatchFill\": \"true\",\n");
          stringBuffer.append("    \"ValidateFlag\": \"true\",\n");
          stringBuffer.append("    \"NumberSearch\": \"true\",\n");
          stringBuffer.append("   \"IsAutoAdjustField\": \"false\",\n");
          stringBuffer.append("        \"IsAutoSubmitAndAudit\": \"true\",");
          stringBuffer.append("    \"InterationFlags\": \"\",\n");
          stringBuffer.append("    \"IgnoreInterationFlag\": \"\",\n");
          stringBuffer.append("    \"IsControlPrecision\": \"false\",\n");
          stringBuffer.append("    \"ValidateRepeatJson\": \"false\",\n");
          stringBuffer.append("    \"Model\": {\n");
          stringBuffer.append("      \"FID\": 0,\n");
          stringBuffer.append("      \"FBillTypeID\": {\n");
          stringBuffer.append("        \"FNUMBER\": \"" + purchaseRequestBill.getFBillTypeID() + "\"\n");//单据类型 CGSQD006_SYS
          stringBuffer.append("      },\n");
          stringBuffer.append("      \"FApplicationDate\": \"" + purchaseRequestBill.getFApplicationDate() + " 00:00:00\",\n");//申请日期
          stringBuffer.append("      \"FRequestType\": \"Material\",\n"); // 申请类型
          stringBuffer.append("      \"FApplicationOrgId\": {\n"); //申请组织
          stringBuffer.append("        \"FNumber\": \"100\"\n");
          stringBuffer.append("      },\n");
          stringBuffer.append("      \"FApplicantId\": {\n"); //申请人
          stringBuffer.append("        \"FStaffNumber\": \"" + purchaseRequestBill.getFApplicantId() + "\"\n");
          stringBuffer.append("      },\n");
          stringBuffer.append("      \"FCurrencyId\": {\n");
          stringBuffer.append("        \"FNumber\": \"CNY\"\n"); //币种
          stringBuffer.append("      },\n");
          stringBuffer.append("      \"FNote\": \"" + purchaseRequestBill.getFNote() + "\",\n"); //备注
          stringBuffer.append("      \"FISPRICEEXCLUDETAX\": true,\n");// 价外税
          stringBuffer.append("      \"FExchangeTypeId\": {\n");
          stringBuffer.append("        \"FNumber\": \"HLTX01_SYS\"\n"); //汇率类型
          stringBuffer.append("      },\n");
          stringBuffer.append("      \"FIsConvert\": false,\n");//是否是单据转换
          stringBuffer.append("      \"FACCTYPE\": \"Q\",\n");//验收方式
          stringBuffer.append("      \"FSRMZZFL\": {\n");
          stringBuffer.append("        \"FNumber\": \"" + purchaseRequestBill.getFSRMZZFL() + "\"\n");//SRM组织分类 6002
          stringBuffer.append("      },\n");
          stringBuffer.append("      \"FOAliuchengID\": \"" + purchaseRequestBill.getRequestid() + "\",\n");//OA流程ID
          stringBuffer.append("      \"FOAliuchengBH\": \"" + purchaseRequestBill.getBh() + "\",\n");//OA流程编号
          stringBuffer.append("      \"FMobBillHead\": {\n");
          stringBuffer.append("        \"FIsMobBill\": false,\n");
          stringBuffer.append("        \"FMobIsPending\": false\n");
          stringBuffer.append("      },\n");
          stringBuffer.append("      \"FEntity\": [\n");
          int i = 0;
          for (PurchaseRequestDto1 purchaseRequestDto1 : purchaseRequestDto1s) {
              stringBuffer.append("        {\n");
              stringBuffer.append("          \"FRequireOrgId\": {\n");
              stringBuffer.append("            \"FNumber\": \"100\"\n");//需求组织
              stringBuffer.append("          },\n");
              stringBuffer.append("          \"FMaterialId\": {\n");
              stringBuffer.append("            \"FNumber\": \"" + purchaseRequestDto1.getFMaterialId() + "\"\n");//物料编码
              stringBuffer.append("          },\n");
              stringBuffer.append("          \"F_QIGM_Base1\": {\n");
              stringBuffer.append("            \"FSTAFFNUMBER\": \"" + purchaseRequestDto1.getFPurchaserId() + "\"\n");//采购员
              stringBuffer.append("          },\n");
              stringBuffer.append("          \"FReqQty\": " + purchaseRequestDto1.getFReqQty() + ",\n");//申请数量
              stringBuffer.append("         \"FApproveQty\": " + purchaseRequestDto1.getFReqQty() + ",\n");//批准数量
              stringBuffer.append("         \"FArrivalDate\": \"" + purchaseRequestDto1.getFArrivalDate() + " 00:00:00\",\n");//要求到货日期
              stringBuffer.append("         \"FEntryNote\": \"" + purchaseRequestDto1.getFEntryNote() + "\",\n");//备注
              stringBuffer.append("          \"FPurchaseOrgId\": {\n");
              stringBuffer.append("            \"FNumber\": \"100\"\n");//采购组织
              stringBuffer.append("          },\n");
              stringBuffer.append("          \"FReceiveOrgId\": {\n");
              stringBuffer.append("           \"FNumber\": \"100\"\n");//收料组织
              stringBuffer.append("          },\n");
              stringBuffer.append("         \"FPriceUnitQty\": " + purchaseRequestDto1.getFReqQty() + ",\n");// 计价数量
              stringBuffer.append("          \"FREQSTOCKQTY\": " + purchaseRequestDto1.getFReqQty() + ",\n");//库存单位数量
              stringBuffer.append("          \"FBaseReqQty\": " + purchaseRequestDto1.getFReqQty() + ",\n");// 申请数量(基本单位)
              stringBuffer.append("         \"FSalQty\": " + purchaseRequestDto1.getFReqQty() + ",\n");//销售数量
              stringBuffer.append("          \"FSalBaseQty\": " + purchaseRequestDto1.getFReqQty() + ",\n");//销售基本数量
              stringBuffer.append("          \"FIsVmiBusiness\": false,\n");
              stringBuffer.append("          \"FYFXMH\": {\n");
              stringBuffer.append("            \"FNumber\": \"" + purchaseRequestBill.getXmbh() + "\"\n");//研发项目
              stringBuffer.append("          }\n");
              stringBuffer.append("        }\n");
              if (i != purchaseRequestDto1s.size() - 1) {
                  stringBuffer.append("        ,");
              }
              i++;

          }
          stringBuffer.append("      ]\n");
          stringBuffer.append("    }\n");
          stringBuffer.append("  }\n");
          stringBuffer.append("}");
          HttpManager httpManager = new HttpManager();
          Map<String, String> head = new HashMap<>();
          String sessionId = login();
          head.put("kdservice-sessionid", sessionId);
          writeLog("采购申请推送内容:" + stringBuffer);
          String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, stringBuffer.toString(), head);
          JSONObject hfJson = JSONObject.parseObject(hf);
          writeLog("采购申请返回:" + hfJson.toJSONString());
          erpUtil.setERPLog(requestid, "采购申请", stringBuffer.toString(), hfJson.toJSONString());
          return hfJson;
      }*/
  /**
     * 附件上传
     *
     * @param imagefile      文件流
     * @param FileNamer      文件名
     * @param FormId         表单唯一标识
     * @param InterId        单据内码
     * @param BillNO         单据编号
     * @param AliasFileNamer 名称
     */
  public void putFile(String requestid, InputStream imagefile, String FileNamer, String FormId, String InterId, String BillNO, String AliasFileNamer) {
      try {
          // 将输入流转换为 byte 数组
          byte[] bytes = inputStreamToByteArray(imagefile);
          // 将 byte 数组转换为 base64 字符串
          String base64String = byteArrayToBase64(bytes);

          Map<String, Object> jsonMap = new HashMap<>();
          Map<String, Object> dataMap = new HashMap<>();
          jsonMap.put("data", dataMap);

          dataMap.put("FileName", FileNamer);
          dataMap.put("FormId", FormId);
          dataMap.put("IsLast", true);
          dataMap.put("InterId", InterId);
          dataMap.put("BillNO", BillNO);
          dataMap.put("AliasFileName", AliasFileNamer);
          dataMap.put("SendByte", base64String);

          String jsonString = JSONObject.toJSONString(jsonMap);

          HttpManager httpManager = new HttpManager();
          Map<String, String> head = new HashMap<>();
          String sessionId = login();
          head.put("kdservice-sessionid", sessionId);

          writeLog("推送附件:" + jsonString);
          String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.FileUrl, jsonString, head);
          JSONObject hfJson = JSONObject.parseObject(hf);
          writeLog("推送附件返回:" + hfJson);
          erpUtil.setERPLog(requestid, "推送附件", jsonString, hfJson.toJSONString());
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

//    public void putFile(String requestid, InputStream imagefile, String FileNamer, String FormId, String InterId, String BillNO, String AliasFileNamer) {
//        String base64String = "";
//        try {
//            // 将输入流转换为 byte 数组
//            byte[] bytes = inputStreamToByteArray(imagefile);
//            // 将 byte 数组转换为 base64 字符串
//            base64String = byteArrayToBase64(bytes);
//
//            String json = "{\n" + "  \"data\": {" + "    \"FileName\": \"" + FileNamer + "\",\n" + "    \"FormId\": \"" + FormId + "\",\n" + "    \"IsLast\": true,\n" + "    \"InterId\": \"" + InterId + "\",\n" + "    \"BillNO\": \"" + BillNO + "\",\n" + "    \"AliasFileName\": \"" + AliasFileNamer + "\",\n" + "    \"SendByte\": \"" + base64String + "\"\n" + "}" + "}";
//            HttpManager httpManager = new HttpManager();
//            Map<String, String> head = new HashMap<>();
//            String sessionId = login();
//            head.put("kdservice-sessionid", sessionId);
//            writeLog("推送附件:" + json);
//            String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.FileUrl, json, head);
//            JSONObject hfJson = JSONObject.parseObject(hf);
//            writeLog("推送附件返回:" + hfJson);
//            erpUtil.setERPLog(requestid, "推送附件", json, hfJson.toJSONString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
   /**
     * 传输部门数据
     *
     * @param bmmc 部门名称
     * @param bmid 部门id
     * @param sjbm 上级部门id
     * @param sszz 所属组织
     * @throws Exception
     */
//    public void postDepartment(String bmmc, String bmid, String sjbm, String sszz, String bmbm) throws Exception {
//        String sessionId = login();
//        LocalDate currentDate = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String formattedDate = currentDate.format(formatter);
//        String json = "{\n" + "  \"formid\": \"BD_Department\",\n" + "  \"data\": {\n" + "    \"NeedUpDateFields\": [],\n" + "    \"NeedReturnFields\": [],\n" + "    \"IsDeleteEntry\": \"true\",\n" + "    \"SubSystemId\": \"\",\n" + "    \"IsVerifyBaseDataField\": \"false\",\n" + "    \"IsEntryBatchFill\": \"true\",\n" + "    \"ValidateFlag\": \"true\",\n" + "    \"NumberSearch\": \"true\",\n" + "    \"IsAutoAdjustField\": \"false\",\n" + "    \"InterationFlags\": \"\",\n" + "    \"IgnoreInterationFlag\": \"\",\n" + "    \"IsControlPrecision\": \"false\",\n" + "        \"IsAutoSubmitAndAudit\": \"true\"," + "    \"ValidateRepeatJson\": \"false\",\n" + "    \"Model\": {\n" + "      \"FDEPTID\": 0,\n" + "      \"FCreateOrgId\": {\n" + "        \"FNumber\": \"" + sszz + "\"\n" +
////                "        \"FNumber\": \"100\"\n" +
//                "      },\n" + "    \"FNumber\": \"" + bmbm + "\"," + "      \"FUseOrgId\": {\n" + "        \"FNumber\": \"" + sszz + "\"\n" +
////                "        \"FNumber\": \"100\"\n" +
//                "      },\n" + "      \"FName\": \"" + bmmc + "\",\n" + "      \"FParentID\": {\n" + "        \"FNumber\": \"" + sjbm + "\"\n" + "      },\n" + "      \"FEffectDate\": \"" + formattedDate + " 00:00:00\",\n" + "      \"FLapseDate\": \"9999-12-31 00:00:00\",\n" + "      \"FDeptProperty\": {\n" + "        \"FNumber\": \"DP02_SYS\"\n" + "      },\n" + "      \"FIsCopyFlush\": false,\n" + "      \"FIsDetailDpt\": false,\n" + "\"F_OABMID\": \"" + bmid + "\"," + "      \"FSHRMapEntity\": {},\n" + "      \"FAcctOrgType\": \"1\"\n" + "    }\n" + "  }\n" + "}";
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("推送部门:" + json);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, json, head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("推送部门返回:" + hfJson);
//        erpUtil.setERPLog("0", "新建部门", json, hfJson.toJSONString());
//    }
    public void postDepartment(String bmmc, String bmid, String sjbm, String sszz, String bmbm) throws Exception {
        String sessionId = login();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("formid", "BD_Department");

        Map<String, Object> dataMap = new HashMap<>();
        jsonMap.put("data", dataMap);

        dataMap.put("NeedUpDateFields", new ArrayList<>());
        dataMap.put("NeedReturnFields", new ArrayList<>());
        dataMap.put("IsDeleteEntry", "true");
        dataMap.put("SubSystemId", "");
        dataMap.put("IsVerifyBaseDataField", "false");
        dataMap.put("IsEntryBatchFill", "true");
        dataMap.put("ValidateFlag", "true");
        dataMap.put("NumberSearch", "true");
        dataMap.put("IsAutoAdjustField", "true");
        dataMap.put("InterationFlags", "");
        dataMap.put("IgnoreInterationFlag", "");
        dataMap.put("IsControlPrecision", "false");
        dataMap.put("IsAutoSubmitAndAudit", "true");
        dataMap.put("ValidateRepeatJson", "false");

        Map<String, Object> modelMap = new HashMap<>();
        dataMap.put("Model", modelMap);

        modelMap.put("FDEPTID", 0);

        Map<String, Object> fCreateOrgIdMap = new HashMap<>();
        fCreateOrgIdMap.put("FNumber", sszz);
        modelMap.put("FCreateOrgId", fCreateOrgIdMap);

        modelMap.put("FNumber", bmbm);

        Map<String, Object> fUseOrgIdMap = new HashMap<>();
        fUseOrgIdMap.put("FNumber", sszz);
        modelMap.put("FUseOrgId", fUseOrgIdMap);

        modelMap.put("FName", bmmc);

        Map<String, Object> fParentIDMap = new HashMap<>();
        fParentIDMap.put("FNumber", sjbm);
        modelMap.put("FParentID", fParentIDMap);

        modelMap.put("FEffectDate", formattedDate + " 00:00:00");
        modelMap.put("FLapseDate", "9999-12-31 00:00:00");

        Map<String, Object> fDeptPropertyMap = new HashMap<>();
        fDeptPropertyMap.put("FNumber", "DP02_SYS");
        modelMap.put("FDeptProperty", fDeptPropertyMap);

        modelMap.put("FIsCopyFlush", false);
        modelMap.put("FIsDetailDpt", false);
        modelMap.put("F_OABMID", bmid);
        modelMap.put("FSHRMapEntity", new HashMap<>());
        modelMap.put("FAcctOrgType", "1");

        String jsonString = JSONObject.toJSONString(jsonMap);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        head.put("kdservice-sessionid", sessionId);

        writeLog("推送部门:" + jsonString);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, jsonString, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("推送部门返回:" + hfJson);
        erpUtil.setERPLog("0", "新建部门", jsonString, hfJson.toJSONString());
    }
    /**
     * @param bmmc
     * @param bmid
     * @param sjbm
     * @param sszz
     * @param bmbm
     */
    public void editDepartment(String id, String bmmc, String bmid, String sjbm, String sszz, String bmbm) throws Exception {
        String sessionId = login();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("formid", "BD_Department");

        Map<String, Object> dataMap = new HashMap<>();
        jsonMap.put("data", dataMap);

        dataMap.put("NeedUpDateFields", Arrays.asList("FName", "FParentID", "FNumber", "F_OABMID"));
        dataMap.put("NeedReturnFields", new ArrayList<>());
        dataMap.put("IsDeleteEntry", "true");
        dataMap.put("SubSystemId", "");
        dataMap.put("IsVerifyBaseDataField", "false");
        dataMap.put("IsEntryBatchFill", "true");
        dataMap.put("ValidateFlag", "true");
        dataMap.put("NumberSearch", "true");
        dataMap.put("IsAutoAdjustField", "true");
        dataMap.put("InterationFlags", "");
        dataMap.put("IgnoreInterationFlag", "");
        dataMap.put("IsControlPrecision", "false");
        dataMap.put("ValidateRepeatJson", "false");

        Map<String, Object> modelMap = new HashMap<>();
        dataMap.put("Model", modelMap);

        modelMap.put("FDEPTID", Integer.parseInt(id));
        modelMap.put("FName", bmmc);

        Map<String, Object> fParentIDMap = new HashMap<>();
        fParentIDMap.put("FNumber", sjbm);
        modelMap.put("FParentID", fParentIDMap);

        modelMap.put("FNumber", bmbm);
        modelMap.put("F_OABMID", bmid);

        String jsonString = JSONObject.toJSONString(jsonMap);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        head.put("kdservice-sessionid", sessionId);

        writeLog("修改部门推送:" + jsonString);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, jsonString, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("修改部门返回:" + hfJson);
        erpUtil.setERPLog("0", "修改部门", jsonString, hfJson.toJSONString());
    }
//    public void editDepartment(String id, String bmmc, String bmid, String sjbm, String sszz, String bmbm) throws Exception {
//        String sessionId = login();
//        String json = "\n" + "\n" + "{\n" + "  \"formid\": \"BD_Department\",\n" + "  \"data\": {\n" + "    \"NeedUpDateFields\": [\"FName\",\"FParentID\",\"FNumber\",\"F_OABMID\"],\n" + "    \"NeedReturnFields\": [],\n" + "    \"IsDeleteEntry\": \"true\",\n" + "    \"SubSystemId\": \"\",\n" + "    \"IsVerifyBaseDataField\": \"false\",\n" + "    \"IsEntryBatchFill\": \"true\",\n" + "    \"ValidateFlag\": \"true\",\n" + "    \"NumberSearch\": \"true\",\n" + "    \"IsAutoAdjustField\": \"true\",\n" + "    \"InterationFlags\": \"\",\n" + "    \"IgnoreInterationFlag\": \"\",\n" + "    \"IsControlPrecision\": \"false\",\n" +
////                "        \"IsAutoSubmitAndAudit\": \"true\","+
//                "    \"ValidateRepeatJson\": \"false\",\n" + "    \"Model\":{\n" + "      \"FDEPTID\": " + id + ",\n" + "      \"FName\": \"" + bmmc + "\",\n" + "      \"FParentID\": {\n" + "        \"FNumber\": \"" + sjbm + "\"\n" + "      },\n" + "      \"FNumber\": \"" + bmbm + "\",\n" + "      \"F_OABMID\": \"" + bmid + "\"\n" + "    }\n" + "  }\n" + "}";
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("修改部门推送:" + json);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, json, head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("修改部门返回:" + hfJson);
//        erpUtil.setERPLog("0", "修改部门", json, hfJson.toJSONString());
//    }

    /**
     * 人员修改
     *
     * @param erpid
     * @param name
     * @param workcode
     * @param fbcode
     * @param ryid
     * @throws Exception
     */
    public void editResource(String erpid, String name, String workcode, String fbcode, String ryid) throws Exception {
        String sessionId = login();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("formid", "BD_Empinfo");

        Map<String, Object> dataMap = new HashMap<>();
        jsonMap.put("data", dataMap);

        dataMap.put("NeedUpDateFields", Arrays.asList("FName", "FStaffNumber", "F_OAYGID"));
        dataMap.put("NeedReturnFields", new ArrayList<>());
        dataMap.put("IsDeleteEntry", "true");
        dataMap.put("SubSystemId", "");
        dataMap.put("IsVerifyBaseDataField", "false");
        dataMap.put("IsEntryBatchFill", "true");
        dataMap.put("ValidateFlag", "true");
        dataMap.put("NumberSearch", "true");
        dataMap.put("IsAutoAdjustField", "true");
        dataMap.put("InterationFlags", "");
        dataMap.put("IgnoreInterationFlag", "");
        dataMap.put("IsControlPrecision", "false");
        dataMap.put("ValidateRepeatJson", "false");

        Map<String, Object> modelMap = new HashMap<>();
        dataMap.put("Model", modelMap);

        modelMap.put("FID", Integer.parseInt(erpid));
        modelMap.put("FName", name);
        modelMap.put("FStaffNumber", workcode);
        modelMap.put("F_OAYGID", ryid);

        String jsonString = JSONObject.toJSONString(jsonMap);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        head.put("kdservice-sessionid", sessionId);

        writeLog("修改人员推送:" + jsonString);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, jsonString, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("修改人员返回:" + hfJson);
        erpUtil.setERPLog("0", "修改人员", jsonString, hfJson.toJSONString());
    }
//    public void editResource(String erpid, String name, String workcode, String fbcode, String ryid) throws Exception {
//        String sessionId = login();
//
//
//        String json = "{\n" + "  \"formid\": \"BD_Empinfo\",\n" + "  \"data\": {\n" + "    \"NeedUpDateFields\": [\n" + "      \"FName\",\n" + "      \"FStaffNumber\",\n" + "      \"F_OAYGID\"\n" + "    ],\n" + "    \"NeedReturnFields\": [],\n" + "    \"IsDeleteEntry\": \"true\",\n" + "    \"SubSystemId\": \"\",\n" + "    \"IsVerifyBaseDataField\": \"false\",\n" + "    \"IsEntryBatchFill\": \"true\",\n" + "    \"ValidateFlag\": \"true\",\n" + "    \"NumberSearch\": \"true\",\n" + "    \"IsAutoAdjustField\": \"true\",\n" + "    \"InterationFlags\": \"\",\n" + "    \"IgnoreInterationFlag\": \"\",\n" + "    \"IsControlPrecision\": \"false\",\n" +
////                "        \"IsAutoSubmitAndAudit\": \"true\","+
//                "    \"ValidateRepeatJson\": \"false\",\n" + "    \"Model\": \n" + "      {\n" + "        \"FID\": " + erpid + ",\n" + "        \"FName\": \"" + name + "\",\n" + "        \"FStaffNumber\": \"" + workcode + "\",\n" + "        \"F_OAYGID\": \"" + ryid + "\"\n" + "      }\n" + "    \n" + "  }\n" + "}\n" + "\n" + "\n" + "\n";
//
//
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("修改人员推送" + json);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, json, head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("修改人员返回:" + hfJson);
//        erpUtil.setERPLog("0", "修改人员", json, hfJson.toJSONString());
//    }

    /**
     * @param name     姓名
     * @param workcode 工号
     * @throws Exception
     */
    public void postResource(String name, String workcode, String fbcode, String ryid) throws Exception {

        String sessionId = login();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("formid", "BD_Empinfo");

        Map<String, Object> dataMap = new HashMap<>();
        jsonMap.put("data", dataMap);

        dataMap.put("NeedUpDateFields", new ArrayList<>());
        dataMap.put("NeedReturnFields", new ArrayList<>());
        dataMap.put("IsDeleteEntry", "true");
        dataMap.put("SubSystemId", "");
        dataMap.put("IsVerifyBaseDataField", "false");
        dataMap.put("IsEntryBatchFill", "true");
        dataMap.put("ValidateFlag", "true");
        dataMap.put("NumberSearch", "true");
        dataMap.put("IsAutoAdjustField", "true");
        dataMap.put("InterationFlags", "");
        dataMap.put("IgnoreInterationFlag", "");
        dataMap.put("IsControlPrecision", "false");
        dataMap.put("IsAutoSubmitAndAudit", "true");
        dataMap.put("ValidateRepeatJson", "false");

        Map<String, Object> modelMap = new HashMap<>();
        dataMap.put("Model", modelMap);

        modelMap.put("FID", 0);
        modelMap.put("FName", name);
        modelMap.put("FStaffNumber", workcode);

        Map<String, Object> fUseOrgIdMap = new HashMap<>();
        fUseOrgIdMap.put("FNumber", fbcode);
        modelMap.put("FUseOrgId", fUseOrgIdMap);

        Map<String, Object> fCreateOrgIdMap = new HashMap<>();
        fCreateOrgIdMap.put("FNumber", fbcode);
        modelMap.put("FCreateOrgId", fCreateOrgIdMap);

        modelMap.put("FCreateSaler", false);
        modelMap.put("FCreateUser", false);
        modelMap.put("FCreateCashier", false);
        modelMap.put("FJoinDate", formattedDate + " 00:00:00");
        modelMap.put("F_OAYGID", ryid);
        modelMap.put("FSHRMapEntity", new HashMap<>());

        String jsonString = JSONObject.toJSONString(jsonMap);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        head.put("kdservice-sessionid", sessionId);

        writeLog("新建人员推送:" + jsonString);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, jsonString, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("新建人员返回:" + hfJson);
        erpUtil.setERPLog("0", "新建人员", jsonString, hfJson.toJSONString());
    }
//    public void postResource(String name, String workcode, String fbcode, String ryid) throws Exception {
//
//        String sessionId = login();
//        LocalDate currentDate = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String formattedDate = currentDate.format(formatter);
//        String json = "{\n" + "  \"formid\": \"BD_Empinfo\",\n" + "  \"data\": {\n" + "    \"NeedUpDateFields\": [],\n" + "    \"NeedReturnFields\": [],\n" + "    \"IsDeleteEntry\": \"true\",\n" + "    \"SubSystemId\": \"\",\n" + "    \"IsVerifyBaseDataField\": \"false\",\n" + "    \"IsEntryBatchFill\": \"true\",\n" + "    \"ValidateFlag\": \"true\",\n" + "    \"NumberSearch\": \"true\",\n" + "    \"IsAutoAdjustField\": \"false\",\n" + "    \"InterationFlags\": \"\",\n" + "    \"IgnoreInterationFlag\": \"\",\n" + "    \"IsControlPrecision\": \"false\",\n" + "    \"IsAutoSubmitAndAudit\": \"true\"," + "    \"ValidateRepeatJson\": \"false\",\n" + "    \"Model\": {\n" + "      \"FID\": 0,\n" + "      \"FName\": \"" + name + "\",\n" + "      \"FStaffNumber\": \"" + workcode + "\",\n" + "      \"FUseOrgId\": {\n" + "        \"FNumber\": \"" + fbcode + "\"\n" +
//                "      },\n" + "      \"FCreateOrgId\": {\n" + "        \"FNumber\": \"" + fbcode + "\"\n" +
//                "      },\n" + "      \"FCreateSaler\": false,\n" + "      \"FCreateUser\": false,\n" + "      \"FCreateCashier\": false,\n" + "      \"FJoinDate\": \"" + formattedDate + " 00:00:00\",\n" + "\"F_OAYGID\": \"" + ryid + "\"," + "      \"FSHRMapEntity\": {},\n" + "    }\n" + "  }\n" + "}";
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("新建人员推送:" + json);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, json, head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("新建人员返回:" + hfJson);
//        erpUtil.setERPLog("0", "新建人员", json, hfJson.toJSONString());
//    }

    /**
     * 新建客户
     *
     * @param customerBill 客户对象
     * @throws Exception
     */
//    public JSONObject postClient(CustomerBill customerBill) throws Exception {
//        String sessionId = login();
//        String json = "\n" + "{\n" + "  \"formid\": \"BD_Customer\",\n" + "  \"data\": {\n" + "    \"NeedUpDateFields\": [],\n" + "    \"NeedReturnFields\": [],\n" + "    \"IsDeleteEntry\": \"true\",\n" + "    \"SubSystemId\": \"\",\n" + "    \"IsVerifyBaseDataField\": \"false\",\n" + "    \"IsEntryBatchFill\": \"true\",\n" + "    \"ValidateFlag\": \"true\",\n" + "    \"NumberSearch\": \"true\",\n" + "    \"IsAutoAdjustField\": \"false\",\n" + "    \"InterationFlags\": \"\",\n" + "    \"IgnoreInterationFlag\": \"\",\n" + "    \"IsControlPrecision\": \"false\",\n" + "        \"IsAutoSubmitAndAudit\": \"true\"," + "    \"ValidateRepeatJson\": \"false\",\n" + "    \"Model\": {\n" + "      \"FCUSTID\": 0,\n" + "      \"FCreateOrgId\": {\n" + "        \"FNumber\": \"100\"\n" + "      },\n" + "      \"FNumber\": \"" + customerBill.getKhbm() + "\",\n" + "      \"FUseOrgId\": {\n" + "        \"FNumber\": \"100\"\n" + "      },\n" + "      \"FName\": \"" + customerBill.getKhmc() + "\",\n" + "      \"FShortName\": \"" + customerBill.getKhmc() + "\",\n" + "      \"FCOUNTRY\": {\n" + "        \"FNumber\": \"" + customerBill.getGj() + "\"\n" + "      },\n" + "      \"FTEL\": \"" + customerBill.getDh() + "\",\n" +//联系电话
//                "      \"FINVOICETITLE\": \"" + customerBill.getKhmc() + "\",\n" +//发票抬头
//                "      \"FINVOICEBANKNAME\": \"" + customerBill.getKhx() + "\",\n" +//开户银行
//                "      \"FINVOICEBANKACCOUNT\": \"" + customerBill.getZh() + "\",\n" +//银行账号
//                "        \"FSELLER\": {\n" + "            \"FNumber\": \"" + customerBill.getSzywy() + "\"\n" + //销售员
//                "        }," + "      \"F_QIGM_Text\": \"" + customerBill.getLxr() + "\",\n" +//联系人
//                "      \"FINVOICEADDRESS\": \"" + customerBill.getDzjlxdh() + "\",\n" +//开票地址
//                "      \"F_QIGM_INVOICEADDRESS\": \"" + customerBill.getFpyjdz() + "\",\n" +//F_QIGM_INVOICEADDRESS 发票邮寄地址
//                "      \"F_QIGM_INVOICEADDRESS1\": \"" + customerBill.getHtyjdz() + "\",\n" +//F_QIGM_INVOICEADDRESS1 合同邮寄地址
//                "      \"F_QIGM_INVOICEADDRESS11\": \"" + customerBill.getKhdz() + "\",\n" +//F_QIGM_INVOICEADDRESS11 发货地址
//                "      \"FTAXREGISTERCODE\": \"" + customerBill.getKhsh() + "\",\n" +//FTAXREGISTERCODE 纳税登记号
//                "      \"F_QIGM_Text1\": \"" + customerBill.getYxxh() + "\",\n" +//F_QIGM_Text1 银行行号
//                "      \"FIsGroup\": false,\n" + "      \"FIsDefPayer\": false,\n" + "      \"FCustTypeId\": {\n" + "        \"FNumber\": \"KHLB001_SYS\"\n" + "      },\n" + "      \"FTRADINGCURRID\": {\n" + "        \"FNumber\": \"CNY\"\n" + "      },\n" + "      \"FInvoiceType\": \"1\",\n" + "      \"FTaxType\": {\n" + "        \"FNumber\": \"SFL02_SYS\"\n" + "      },\n" + "      \"FPriority\": 1,\n" + "      \"FTaxRate\": {\n" + "        \"FNumber\": \"SL02_SYS\"\n" + "      },\n" + "      \"FISCREDITCHECK\": true,\n" + "      \"FIsTrade\": true,\n" + "      \"FUncheckExpectQty\": false,\n" + "      \"FSOCIALCRECODE\": \"" + customerBill.getKhsh() + "\",\n" +//统一社会信用代码
//                "      \"FSFYTB\": false,\n" + "      \"FT_BD_CUSTOMEREXT\": {\n" + " \"FPROVINCE\": {\n" + "                \"FNumber\": \"" + customerBill.getSfinfo() + "\"\n" + "            },\n" + "            \"FCITY\": {\n" + "                \"FNumber\": \"" + customerBill.getCs() + "\"\n" + "            }," + "           \"FEnableSL\": false,\n" + "           \"FALLOWJOINZHJ\": false\n" + "      }\n" + "    }\n" + "  }\n" + "}";
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("新建客户推送:" + json);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, json, head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("新建客户返回:" + hfJson);
//        erpUtil.setERPLog("0", "新建客户", json, hfJson.toJSONString());
//        return hfJson;
//    }
    /**
    public JSONObject postClient(CustomerBill customerBill) throws Exception {
        String sessionId = login();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("formid", "BD_Customer");

        Map<String, Object> dataMap = new HashMap<>();
        jsonMap.put("data", dataMap);

        dataMap.put("NeedUpDateFields", new ArrayList<>());
        dataMap.put("NeedReturnFields", new ArrayList<>());
        dataMap.put("IsDeleteEntry", "true");
        dataMap.put("SubSystemId", "");
        dataMap.put("IsVerifyBaseDataField", "false");
        dataMap.put("IsEntryBatchFill", "true");
        dataMap.put("ValidateFlag", "true");
        dataMap.put("NumberSearch", "true");
        dataMap.put("IsAutoAdjustField", "true");
        dataMap.put("InterationFlags", "");
        dataMap.put("IgnoreInterationFlag", "");
        dataMap.put("IsControlPrecision", "false");
        dataMap.put("IsAutoSubmitAndAudit", "true");
        dataMap.put("ValidateRepeatJson", "false");

        Map<String, Object> modelMap = new HashMap<>();
        dataMap.put("Model", modelMap);

        modelMap.put("FCUSTID", 0);

        Map<String, Object> fCreateOrgIdMap = new HashMap<>();
        fCreateOrgIdMap.put("FNumber", "100");
        modelMap.put("FCreateOrgId", fCreateOrgIdMap);

        modelMap.put("FNumber", customerBill.getKhbm());

        Map<String, Object> fUseOrgIdMap = new HashMap<>();
        fUseOrgIdMap.put("FNumber", "100");
        modelMap.put("FUseOrgId", fUseOrgIdMap);

        modelMap.put("FName", customerBill.getKhmc());
        modelMap.put("FShortName", customerBill.getKhmc());

        Map<String, Object> fCOUNTRYMap = new HashMap<>();
        fCOUNTRYMap.put("FNumber", customerBill.getGj());
        modelMap.put("FCOUNTRY", fCOUNTRYMap);

        modelMap.put("FTEL", customerBill.getDh());
        modelMap.put("FINVOICETITLE", customerBill.getKhmc());
        modelMap.put("FINVOICEBANKNAME", customerBill.getKhx());
        modelMap.put("FINVOICEBANKACCOUNT", customerBill.getZh());

        Map<String, Object> fSELLERMap = new HashMap<>();
        fSELLERMap.put("FNumber", customerBill.getSzywy());
        modelMap.put("FSELLER", fSELLERMap);

        modelMap.put("F_QIGM_Text", customerBill.getLxr());
        modelMap.put("FINVOICEADDRESS", customerBill.getDzjlxdh());
        modelMap.put("F_QIGM_INVOICEADDRESS", customerBill.getFpyjdz());
        modelMap.put("F_QIGM_INVOICEADDRESS1", customerBill.getHtyjdz());
        modelMap.put("F_QIGM_INVOICEADDRESS11", customerBill.getKhdz());
        modelMap.put("FTAXREGISTERCODE", customerBill.getKhsh());
        modelMap.put("F_QIGM_Text1", customerBill.getYxxh());
        modelMap.put("FIsGroup", false);
        modelMap.put("FIsDefPayer", false);

        Map<String, Object> fCustTypeIdMap = new HashMap<>();
        fCustTypeIdMap.put("FNumber", "KHLB001_SYS");
        modelMap.put("FCustTypeId", fCustTypeIdMap);

        Map<String, Object> fTRADINGCURRIDMap = new HashMap<>();
        fTRADINGCURRIDMap.put("FNumber", "CNY");
        modelMap.put("FTRADINGCURRID", fTRADINGCURRIDMap);

        modelMap.put("FInvoiceType", "1");

        Map<String, Object> fTaxTypeMap = new HashMap<>();
        fTaxTypeMap.put("FNumber", "SFL02_SYS");
        modelMap.put("FTaxType", fTaxTypeMap);

        modelMap.put("FPriority", 1);

        Map<String, Object> fTaxRateMap = new HashMap<>();
        fTaxRateMap.put("FNumber", "SL02_SYS");
        modelMap.put("FTaxRate", fTaxRateMap);

        modelMap.put("FISCREDITCHECK", true);
        modelMap.put("FIsTrade", true);
        modelMap.put("FUncheckExpectQty", false);
        modelMap.put("FSOCIALCRECODE", customerBill.getKhsh());
        modelMap.put("FSFYTB", false);

        Map<String, Object> fT_BD_CUSTOMEREXTMap = new HashMap<>();
        modelMap.put("FT_BD_CUSTOMEREXT", fT_BD_CUSTOMEREXTMap);

        Map<String, Object> fPROVINCEMap = new HashMap<>();
        fPROVINCEMap.put("FNumber", customerBill.getSfinfo());
        fT_BD_CUSTOMEREXTMap.put("FPROVINCE", fPROVINCEMap);

        Map<String, Object> fCITYMap = new HashMap<>();
        fCITYMap.put("FNumber", customerBill.getCs());
        fT_BD_CUSTOMEREXTMap.put("FCITY", fCITYMap);

        fT_BD_CUSTOMEREXTMap.put("FEnableSL", false);
        fT_BD_CUSTOMEREXTMap.put("FALLOWJOINZHJ", false);

        String jsonString = JSONObject.toJSONString(jsonMap);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        head.put("kdservice-sessionid", sessionId);

        writeLog("新建客户推送:" + jsonString);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, jsonString, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("新建客户返回:" + hfJson);
        erpUtil.setERPLog("0", "新建客户", jsonString, hfJson.toJSONString());
        return hfJson;
    }

    /**
     * 客户修改
     *
     * @param erpid
     * @param customerBill
     * @return
     * @throws Exception
     */
    /**
    public JSONObject editClient(String erpid, CustomerBill customerBill) throws Exception {
        String sessionId = login();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("formid", "BD_Customer");

        Map<String, Object> dataMap = new HashMap<>();
        jsonMap.put("data", dataMap);

        List<String> needUpDateFields = Arrays.asList(
                "FName", "FShortName", "FCOUNTRY", "FTEL", "FINVOICETITLE",
                "FINVOICEBANKNAME", "FINVOICEBANKACCOUNT", "FSELLER", "F_QIGM_Text",
                "FINVOICEADDRESS", "F_QIGM_INVOICEADDRESS", "F_QIGM_INVOICEADDRESS1",
                "F_QIGM_INVOICEADDRESS11", "FTAXREGISTERCODE", "F_QIGM_Text1", "FPROVINCE",
                "FCITY", "FT_BD_CUSTOMEREXT"
        );
        dataMap.put("NeedUpDateFields", needUpDateFields);
        dataMap.put("NeedReturnFields", new ArrayList<>());
        dataMap.put("IsDeleteEntry", "true");
        dataMap.put("SubSystemId", "");
        dataMap.put("IsVerifyBaseDataField", "false");
        dataMap.put("IsEntryBatchFill", "true");
        dataMap.put("ValidateFlag", "true");
        dataMap.put("NumberSearch", "true");
        dataMap.put("IsAutoAdjustField", "true");
        dataMap.put("InterationFlags", "");
        dataMap.put("IgnoreInterationFlag", "");
        dataMap.put("IsControlPrecision", "false");

        Map<String, Object> modelMap = new HashMap<>();
        dataMap.put("Model", modelMap);

        modelMap.put("FCUSTID", erpid);
        modelMap.put("FName", customerBill.getKhmc());
        modelMap.put("FShortName", customerBill.getKhmc());

        Map<String, Object> fCOUNTRYMap = new HashMap<>();
        fCOUNTRYMap.put("FNumber", customerBill.getGj());
        modelMap.put("FCOUNTRY", fCOUNTRYMap);

        modelMap.put("FTEL", customerBill.getDh());
        modelMap.put("FINVOICETITLE", customerBill.getKhmc());
        modelMap.put("FINVOICEBANKNAME", customerBill.getKhx());
        modelMap.put("FINVOICEBANKACCOUNT", customerBill.getZh());

        Map<String, Object> fSELLERMap = new HashMap<>();
        fSELLERMap.put("FNumber", customerBill.getSzywy());
        modelMap.put("FSELLER", fSELLERMap);

        modelMap.put("F_QIGM_Text", customerBill.getLxr());
        modelMap.put("FINVOICEADDRESS", customerBill.getDzjlxdh());
        modelMap.put("F_QIGM_INVOICEADDRESS", customerBill.getFpyjdz());
        modelMap.put("F_QIGM_INVOICEADDRESS1", customerBill.getHtyjdz());
        modelMap.put("F_QIGM_INVOICEADDRESS11", customerBill.getKhdz());
        modelMap.put("FTAXREGISTERCODE", customerBill.getKhsh());
        modelMap.put("F_QIGM_Text1", customerBill.getYxxh());

        Map<String, Object> fT_BD_CUSTOMEREXTMap = new HashMap<>();
        modelMap.put("FT_BD_CUSTOMEREXT", fT_BD_CUSTOMEREXTMap);

        Map<String, Object> fPROVINCEMap = new HashMap<>();
        fPROVINCEMap.put("FNumber", customerBill.getSfinfo());
        fT_BD_CUSTOMEREXTMap.put("FPROVINCE", fPROVINCEMap);

        Map<String, Object> fCITYMap = new HashMap<>();
        fCITYMap.put("FNumber", customerBill.getCs());
        fT_BD_CUSTOMEREXTMap.put("FCITY", fCITYMap);

        fT_BD_CUSTOMEREXTMap.put("FEnableSL", false);
        fT_BD_CUSTOMEREXTMap.put("FALLOWJOINZHJ", false);

        String jsonString = JSONObject.toJSONString(jsonMap);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        head.put("kdservice-sessionid", sessionId);

        writeLog("客户修改推送" + jsonString);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, jsonString, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("客户修改返回" + hfJson);
        erpUtil.setERPLog("0", "客户修改", jsonString, hfJson.toJSONString());
        return hfJson;
    }
//    public JSONObject editClient(String erpid, CustomerBill customerBill) throws Exception {
//        String json = "{\n" + "  \"formid\": \"BD_Customer\",\n" + "  \"data\": {\n" + "    \"NeedUpDateFields\": [\n" + "      \"FName\",\"FShortName\",\"FCOUNTRY\",\"FTEL\",\"FINVOICETITLE\",\"FINVOICEBANKNAME\",\"FINVOICEBANKACCOUNT\",\"FSELLER\",\"F_QIGM_Text\",\"FINVOICEADDRESS\",\"F_QIGM_INVOICEADDRESS\",\"F_QIGM_INVOICEADDRESS1\",\"F_QIGM_INVOICEADDRESS11\",\"FTAXREGISTERCODE\",\"F_QIGM_Text1\",\"FPROVINCE\",\"FCITY\",\"FT_BD_CUSTOMEREXT\"\n" + "    ],\n" + "    \"NeedReturnFields\": [],\n" + "    \"IsDeleteEntry\": \"true\",\n" + "    \"SubSystemId\": \"\",\n" + "    \"IsVerifyBaseDataField\": \"false\",\n" + "    \"IsEntryBatchFill\": \"true\",\n" + "    \"ValidateFlag\": \"true\",\n" + "    \"NumberSearch\": \"true\",\n" + "    \"IsAutoAdjustField\": \"false\",\n" + "    \"InterationFlags\": \"\",\n" + "    \"IgnoreInterationFlag\": \"\",\n" + "    \"IsControlPrecision\": \"false\",\n" + "    \"Model\": {\n" + "      \"FCUSTID\": \"" + erpid + "\",\n" + "      \"FName\": \"" + customerBill.getKhmc() + "\",\n" + "      \"FShortName\": \"" + customerBill.getKhmc() + "\",\n" + "      \"FCOUNTRY\": {\n" + "        \"FNumber\": \"" + customerBill.getGj() + "\"\n" + "      },\n" + "      \"FTEL\": \"" + customerBill.getDh() + "\",\n" +//联系电话
//                "      \"FINVOICETITLE\": \"" + customerBill.getKhmc() + "\",\n" +//发票抬头
//                "      \"FINVOICEBANKNAME\": \"" + customerBill.getKhx() + "\",\n" +//开户银行
//                "      \"FINVOICEBANKACCOUNT\": \"" + customerBill.getZh() + "\",\n" +//银行账号
//                "        \"FSELLER\": {\n" + "            \"FNumber\": \"" + customerBill.getSzywy() + "\"\n" + //销售员
//                "        }," + "      \"F_QIGM_Text\": \"" + customerBill.getLxr() + "\",\n" +//联系人
//                "      \"FINVOICEADDRESS\": \"" + customerBill.getDzjlxdh() + "\",\n" +//开票地址
//                "      \"F_QIGM_INVOICEADDRESS\": \"" + customerBill.getFpyjdz() + "\",\n" +//F_QIGM_INVOICEADDRESS 发票邮寄地址
//                "      \"F_QIGM_INVOICEADDRESS1\": \"" + customerBill.getHtyjdz() + "\",\n" +//F_QIGM_INVOICEADDRESS1 合同邮寄地址
//                "      \"F_QIGM_INVOICEADDRESS11\": \"" + customerBill.getKhdz() + "\",\n" +//F_QIGM_INVOICEADDRESS11 发货地址
//                "      \"FTAXREGISTERCODE\": \"" + customerBill.getKhsh() + "\",\n" +//FTAXREGISTERCODE 纳税登记号
//                "      \"F_QIGM_Text1\": \"" + customerBill.getYxxh() + "\",\n" +//F_QIGM_Text1 银行行号
//                "      \"FT_BD_CUSTOMEREXT\": {\n" + " \"FPROVINCE\": {\n" + "                \"FNumber\": \"" + customerBill.getSfinfo() + "\"\n" + "            },\n" + "            \"FCITY\": {\n" + "                \"FNumber\": \"" + customerBill.getCs() + "\"\n" + "            }," + "           \"FEnableSL\": false,\n" + "           \"FALLOWJOINZHJ\": false\n" + "      }\n" + "    }\n" + "  }\n" + "}";
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        String sessionId = login();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("客户修改推送" + json);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, json, head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("客户修改返回" + hfJson);
//        erpUtil.setERPLog("0", "客户修改", json, hfJson.toJSONString());
//        return hfJson;
//    }


    /**
     * 物料新建
     *
     * @param wlfz 物料分组
     * @param name 物料名称
     * @param ggxh 规格型号
     * @param chlb 存货类别
     */
    public JSONObject putMateriel(String requestid, String wlfz, String name, String ggxh, String chlb) throws Exception {
        String sessionId = login();
        if ("".equals(chlb)) {
            if (wlfz.contains(".")) {
                // 如果字符串中包含"."，则截取出前两部分并拼接
                String[] arr = wlfz.split("\\.");
                if (arr.length >= 2) {
                    String result = arr[0] + "." + arr[1];
                    chlb = result;
                } else {
                    chlb = "";
                }
            } else {
                // 如果字符串中不包含"."，则直接输出原字符串
                chlb = wlfz;
            }
        }

        Map<String, Object> jsonMap = new HashMap<>();
        Map<String, Object> formMap = new HashMap<>();
        List<Map<String, Object>> entityInvPtyList = new ArrayList<>();

        formMap.put("NeedUpDateFields", new ArrayList<>());
        formMap.put("NeedReturnFields", new ArrayList<>());
        formMap.put("IsDeleteEntry", "true");
        formMap.put("SubSystemId", "");
        formMap.put("IsVerifyBaseDataField", "false");
        formMap.put("IsEntryBatchFill", "true");
        formMap.put("ValidateFlag", "true");
        formMap.put("NumberSearch", "true");
        formMap.put("IsAutoAdjustField", "true");
        formMap.put("InterationFlags", "");
        formMap.put("IgnoreInterationFlag", "");
        formMap.put("IsControlPrecision", "false");
        formMap.put("IsAutoSubmitAndAudit", "true");
        formMap.put("ValidateRepeatJson", "false");

        Map<String, Object> modelMap = new HashMap<>();
        Map<String, Object> subHeadEntity5Map = new HashMap<>();
        subHeadEntity5Map.put("FIsMainPrd", true);

        modelMap.put("SubHeadEntity5", subHeadEntity5Map);
        modelMap.put("FMATERIALID", 0);
        modelMap.put("FCreateOrgId", Collections.singletonMap("FNumber", "100"));
        modelMap.put("FUseOrgId", Collections.singletonMap("FNumber", "100"));
        modelMap.put("FName", name);
        modelMap.put("FSpecification", ggxh);
        modelMap.put("FMaterialGroup", Collections.singletonMap("FNumber", wlfz));
        modelMap.put("FDSMatchByLot", false);
        modelMap.put("FImgStorageType", "B");
        modelMap.put("FIsSalseByNet", false);
        modelMap.put("FSFYTB", false);
        modelMap.put("F_ZKXJ", 1);

        Map<String, Object> FsubHeadEntityMap = new HashMap<>();
        FsubHeadEntityMap.put("FIsControlSal", false);
        FsubHeadEntityMap.put("FIsAutoRemove", false);
        FsubHeadEntityMap.put("FIsMailVirtual", false);
        FsubHeadEntityMap.put("FTimeUnit", "H");
        FsubHeadEntityMap.put("FIsPrinttAg", false);
        FsubHeadEntityMap.put("FIsAccessory", false);

        modelMap.put("FSubHeadEntity", FsubHeadEntityMap);

        Map<String, Object> subHeadEntityMap = new HashMap<>();
        subHeadEntityMap.put("FErpClsID","2");
        subHeadEntityMap.put("FFeatureItem","1");
        subHeadEntityMap.put("FCategoryID", Collections.singletonMap("FNumber", chlb));
        subHeadEntityMap.put("FTaxType",Collections.singletonMap("FNumber", "WLDSFL01_SYS"));
        subHeadEntityMap.put("FTaxRateId",Collections.singletonMap("FNUMBER", "SL02_SYS"));
        subHeadEntityMap.put("FBaseUnitId",Collections.singletonMap("FNUMBER", "Pcs"));
        subHeadEntityMap.put("FIsPurchase",true);
        subHeadEntityMap.put("FIsInventory",true);
        subHeadEntityMap.put("FIsSubContract",false);
        subHeadEntityMap.put("FIsSale",true);
        subHeadEntityMap.put("FIsProduce",true);
        subHeadEntityMap.put("FIsAsset",false);
        subHeadEntityMap.put("FWEIGHTUNITID",Collections.singletonMap("FNUMBER", "kg"));
        subHeadEntityMap.put("FVOLUMEUNITID",Collections.singletonMap("FNUMBER", "m"));

        modelMap.put("SubHeadEntity", subHeadEntityMap);

        Map<String, Object> subHeadEntity1Map = new HashMap<>();
        subHeadEntity1Map.put("FStoreUnitID", Collections.singletonMap("FNumber", "Pcs"));
        subHeadEntity1Map.put("FUnitConvertDir", "1");
        subHeadEntity1Map.put("FIsLockStock", true);
        subHeadEntity1Map.put("FIsCycleCounting", false);
        subHeadEntity1Map.put("FCountCycle", "1");
        subHeadEntity1Map.put("FCountDay", 1);
        subHeadEntity1Map.put("FIsMustCounting", false);
        subHeadEntity1Map.put("FIsBatchManage", false);
        subHeadEntity1Map.put("FIsKFPeriod", false);
        subHeadEntity1Map.put("FIsExpParToFlot", false);
        subHeadEntity1Map.put("FCurrencyId", Collections.singletonMap("FNumber", "CNY"));
        subHeadEntity1Map.put("FIsEnableMinStock", false);
        subHeadEntity1Map.put("FIsEnableMaxStock", false);
        subHeadEntity1Map.put("FIsEnableSafeStock", false);
        subHeadEntity1Map.put("FIsEnableReOrder", false);
        subHeadEntity1Map.put("FIsSNManage", false);
        subHeadEntity1Map.put("FIsSNPRDTracy", false);
        subHeadEntity1Map.put("FSNManageType", "1");
        subHeadEntity1Map.put("FSNGenerateTime", "1");

        modelMap.put("SubHeadEntity1", subHeadEntity1Map);

        Map<String, Object> subHeadEntity2Map = new HashMap<>();
        subHeadEntity2Map.put("FSaleUnitId", Collections.singletonMap("FNumber", "Pcs"));
        subHeadEntity2Map.put("FSalePriceUnitId", Collections.singletonMap("FNumber", "Pcs"));
        subHeadEntity2Map.put("FMaxQty", 100000.0);
        subHeadEntity2Map.put("FIsATPCheck", false);
        subHeadEntity2Map.put("FIsReturnPart", false);
        subHeadEntity2Map.put("FIsInvoice", false);
        subHeadEntity2Map.put("FIsReturn", true);
        subHeadEntity2Map.put("FAllowPublish", false);
        subHeadEntity2Map.put("FISAFTERSALE", true);
        subHeadEntity2Map.put("FISPRODUCTFILES", true);
        subHeadEntity2Map.put("FISWARRANTED", false);
        subHeadEntity2Map.put("FWARRANTYUNITID", "D");
        subHeadEntity2Map.put("FOutLmtUnit", "SAL");
        subHeadEntity2Map.put("FIsTaxEnjoy", false);
        subHeadEntity2Map.put("FUnValidateExpQty", false);

        modelMap.put("SubHeadEntity2", subHeadEntity2Map);

        Map<String, Object> subHeadEntity3Map = new HashMap<>();
        subHeadEntity3Map.put("FPurchaseUnitId", Collections.singletonMap("FNumber", "Pcs"));
        subHeadEntity3Map.put("FPurchasePriceUnitId", Collections.singletonMap("FNumber", "Pcs"));
        subHeadEntity3Map.put("FIsQuota", false);
        subHeadEntity3Map.put("FQuotaType", "1");
        subHeadEntity3Map.put("FIsVmiBusiness", false);
        subHeadEntity3Map.put("FEnableSL", false);
        subHeadEntity3Map.put("FIsPR", false);
        subHeadEntity3Map.put("FIsReturnMaterial", true);
        subHeadEntity3Map.put("FIsSourceControl", false);
        subHeadEntity3Map.put("FPOBillTypeId", Collections.singletonMap("FNUMBER", "WXCS1"));
        subHeadEntity3Map.put("FPrintCount", 1);
        subHeadEntity3Map.put("FMinPackCount", 1.0);
        subHeadEntity3Map.put("FIsEnableScheduleSub", false);

        modelMap.put("SubHeadEntity3", subHeadEntity3Map);

        Map<String, Object> subHeadEntity4Map = new HashMap<>();
        subHeadEntity4Map.put("FPlanningStrategy", "1");
        subHeadEntity4Map.put("FMfgPolicyId", Collections.singletonMap("FNumber", "ZZCL001_SYS"));
        subHeadEntity4Map.put("FFixLeadTimeType", "1");
        subHeadEntity4Map.put("FVarLeadTimeType", "1");
        subHeadEntity4Map.put("FCheckLeadTimeType", "1");
        subHeadEntity4Map.put("FOrderIntervalTimeType", "3");
        subHeadEntity4Map.put("FMaxPOQty", 100000.0);
        subHeadEntity4Map.put("FEOQ", 1.0);
        subHeadEntity4Map.put("FVarLeadTimeLotSize", 1.0);
        subHeadEntity4Map.put("FIsMrpComBill", true);
        subHeadEntity4Map.put("FIsMrpComReq", false);
        subHeadEntity4Map.put("FReserveType", "1");
        subHeadEntity4Map.put("FAllowPartAhead", false);
        subHeadEntity4Map.put("FCanDelayDays", 999);
        subHeadEntity4Map.put("FAllowPartDelay", true);
        subHeadEntity4Map.put("FPlanOffsetTimeType", "1");
        subHeadEntity4Map.put("FWriteOffQty", 1.0);

        modelMap.put("SubHeadEntity4", subHeadEntity4Map);

        Map<String, Object> subHeadEntity5Map2 = new HashMap<>();
        subHeadEntity5Map2.put("FProduceUnitId", Collections.singletonMap("FNumber", "Pcs"));
        subHeadEntity5Map2.put("FProduceBillType", Collections.singletonMap("FNUMBER", "SCDD01_SYS"));
        subHeadEntity5Map2.put("FIsSNCarryToParent", false);
        subHeadEntity5Map2.put("FIsProductLine", false);
        subHeadEntity5Map2.put("FBOMUnitId", Collections.singletonMap("FNumber", "Pcs"));
        subHeadEntity5Map2.put("FIsMainPrd", true);
        subHeadEntity5Map2.put("FIsCoby", false);
        subHeadEntity5Map2.put("FIsECN", false);
        subHeadEntity5Map2.put("FIssueType", "1");
        subHeadEntity5Map2.put("FOverControlMode", "1");
        subHeadEntity5Map2.put("FMinIssueQty", 1.0);
        subHeadEntity5Map2.put("FISMinIssueQty", false);
        subHeadEntity5Map2.put("FIsKitting", false);
        subHeadEntity5Map2.put("FIsCompleteSet", false);
        subHeadEntity5Map2.put("FMinIssueUnitId", Collections.singletonMap("FNUMBER", "Pcs"));
        subHeadEntity5Map2.put("FStandHourUnitId", "3600");
        subHeadEntity5Map2.put("FBackFlushType", "1");
        subHeadEntity5Map2.put("FIsEnableSchedule", false);

        modelMap.put("SubHeadEntity5", subHeadEntity5Map2);

        Map<String, Object> subHeadEntity7Map = new HashMap<>();
        subHeadEntity7Map.put("FSubconUnitId", Collections.singletonMap("FNumber", "Pcs"));
        subHeadEntity7Map.put("FSubconPriceUnitId", Collections.singletonMap("FNumber", "Pcs"));
        subHeadEntity7Map.put("FSubBillType", Collections.singletonMap("FNUMBER", "WWDD01_SYS"));

        modelMap.put("SubHeadEntity7", subHeadEntity7Map);

        Map<String, Object> subHeadEntity6Map = new HashMap<>();
        subHeadEntity6Map.put("FCheckIncoming", false);
        subHeadEntity6Map.put("FCheckProduct", false);
        subHeadEntity6Map.put("FCheckStock", false);
        subHeadEntity6Map.put("FCheckReturn", false);
        subHeadEntity6Map.put("FCheckDelivery", false);
        subHeadEntity6Map.put("FEnableCyclistQCSTK", false);
        subHeadEntity6Map.put("FEnableCyclistQCSTKEW", false);
        subHeadEntity6Map.put("FCheckEntrusted", false);
        subHeadEntity6Map.put("FCheckOther", false);
        subHeadEntity6Map.put("FIsFirstInspect", false);
        subHeadEntity6Map.put("FCheckReturnMtrl", false);
        subHeadEntity6Map.put("FCheckSubRtnMtrl", false);

        modelMap.put("SubHeadEntity6", subHeadEntity6Map);
        for(int i = 1 ; i<=6 ; i++){
            Map<String,Object> fEntityInvPty = new HashMap<>();
            fEntityInvPty.put("FInvPtyId",Collections.singletonMap("FNumber", "0"+i));
            fEntityInvPty.put("FIsEnable",true);
            fEntityInvPty.put("FIsAffectPrice",false);
            fEntityInvPty.put("FIsAffectPlan",false);
            fEntityInvPty.put("FIsAffectCost",false);
            entityInvPtyList.add(fEntityInvPty);
        }
        jsonMap.put("formid", "BD_MATERIAL");
        jsonMap.put("data", formMap);
        formMap.put("Model", modelMap);
        formMap.put("FEntityInvPty", entityInvPtyList);

        String json = JSON.toJSONString(jsonMap);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        head.put("kdservice-sessionid", sessionId);
        writeLog("新建物料推送:" + json);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, json, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("新建物料返回:" + hfJson);
        erpUtil.setERPLog(requestid, "新建物料", json, hfJson.toJSONString());
        return hfJson;
    }
//    public JSONObject putMateriel(String requestid, String wlfz, String name, String ggxh, String chlb) throws Exception {
//        String sessionId = login();
//        if ("".equals(chlb)) {
//            if (wlfz.contains(".")) {
//                // 如果字符串中包含"."，则截取出前两部分并拼接
//                String[] arr = wlfz.split("\\.");
//                if (arr.length >= 2) {
//                    String result = arr[0] + "." + arr[1];
//                    chlb = result;
//                } else {
//                    chlb = "";
//                }
//            } else {
//                // 如果字符串中不包含"."，则直接输出原字符串
//                chlb = wlfz;
//            }
//        }
//
//        String json = "{\n" +
//                "  \"formid\": \"BD_MATERIAL\",\n"
//                + "  \"data\": {"
//                + "  \"NeedUpDateFields\": [],\n"
//                + "  \"NeedReturnFields\": [],\n"
//                + "  \"IsDeleteEntry\": \"true\",\n"
//                + "  \"SubSystemId\": \"\",\n"
//                + "  \"IsVerifyBaseDataField\": \"false\",\n"
//                + "  \"IsEntryBatchFill\": \"true\",\n"
//                + "  \"ValidateFlag\": \"true\",\n"
//                + "  \"NumberSearch\": \"true\",\n"
//                + "  \"IsAutoAdjustField\": \"false\",\n"
//                + "  \"InterationFlags\": \"\",\n"
//                + "  \"IgnoreInterationFlag\": \"\",\n"
//                + "  \"IsControlPrecision\": \"false\",\n"
//                + "        \"IsAutoSubmitAndAudit\": \"true\","
//                + "  \"ValidateRepeatJson\": \"false\",\n"
//                + "  \"Model\": {\n"
//                + "   \"SubHeadEntity5\": {\n"
//                + "     \"FIsMainPrd\": true\n"
//                + "    },"
//                + "    \"FMATERIALID\": 0,\n"
//                + "    \"FCreateOrgId\": {\n"
//                + "      \"FNumber\": \"100\"\n"
//                + "    },\n"
//                + "    \"FUseOrgId\": {\n"
//                + "      \"FNumber\": \"100\"\n"
//                + "    },\n"
//                + "    \"FName\": \"" + name + "\",\n"
//                + "    \"FSpecification\": \"" + ggxh + "\",\n"
//                + "    \"FMaterialGroup\": {\n"
//                + "      \"FNumber\": \"" + wlfz + "\"\n"
//                + "    },\n"
//                + "    \"FDSMatchByLot\": false,\n"
//                + "    \"FImgStorageType\": \"B\",\n"
//                + "    \"FIsSalseByNet\": false,\n"
//                + "    \"FSFYTB\": false,\n"
//                + "    \"F_ZKXJ\": 1,\n"
//                + "    \"FSubHeadEntity\": {\n"
//                + "      \"FIsControlSal\": false,\n"
//                + "      \"FIsAutoRemove\": false,\n"
//                + "      \"FIsMailVirtual\": false,\n"
//                + "      \"FTimeUnit\": \"H\",\n"
//                + "      \"FIsPrinttAg\": false,\n"
//                + "      \"FIsAccessory\": false\n"
//                + "    },\n"
//                + "    \"SubHeadEntity\": {\n"
//                + "      \"FErpClsID\": \"2\",\n"
//                + "      \"FFeatureItem\": \"1\",\n"
//                + "      \"FCategoryID\": {\n"
//                + "        \"FNumber\": \"" + chlb + "\"\n"
//                + "      },\n"
//                + "      \"FTaxType\": {\n"
//                + "        \"FNumber\": \"WLDSFL01_SYS\"\n"
//                + "      },\n"
//                + "      \"FTaxRateId\": {\n"
//                + "        \"FNUMBER\": \"SL02_SYS\"\n"
//                + "      },\n"
//                + "      \"FBaseUnitId\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FIsPurchase\": true,\n"
//                + "      \"FIsInventory\": true,\n"
//                + "      \"FIsSubContract\": false,\n"
//                + "      \"FIsSale\": true,\n"
//                + "      \"FIsProduce\": true,\n"
//                + "      \"FIsAsset\": false,\n"
//                + "      \"FWEIGHTUNITID\": {\n"
//                + "        \"FNUMBER\": \"kg\"\n"
//                + "      },\n"
//                + "      \"FVOLUMEUNITID\": {\n"
//                + "        \"FNUMBER\": \"m\"\n"
//                + "      }\n"
//                + "    },\n"
//                + "    \"SubHeadEntity1\": {\n"
//                + "      \"FStoreUnitID\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FUnitConvertDir\": \"1\",\n"
//                + "      \"FIsLockStock\": true,\n"
//                + "      \"FIsCycleCounting\": false,\n"
//                + "      \"FCountCycle\": \"1\",\n"
//                + "      \"FCountDay\": 1,\n"
//                + "      \"FIsMustCounting\": false,\n"
//                + "      \"FIsBatchManage\": false,\n"
//                + "      \"FIsKFPeriod\": false,\n"
//                + "      \"FIsExpParToFlot\": false,\n"
//                + "      \"FCurrencyId\": {\n"
//                + "        \"FNumber\": \"CNY\"\n"
//                + "      },\n"
//                + "      \"FIsEnableMinStock\": false,\n"
//                + "      \"FIsEnableMaxStock\": false,\n"
//                + "      \"FIsEnableSafeStock\": false,\n"
//                + "      \"FIsEnableReOrder\": false,\n"
//                + "      \"FIsSNManage\": false,\n"
//                + "      \"FIsSNPRDTracy\": false,\n"
//                + "      \"FSNManageType\": \"1\",\n"
//                + "      \"FSNGenerateTime\": \"1\"\n"
//                + "    },\n"
//                + "    \"SubHeadEntity2\": {\n"
//                + "      \"FSaleUnitId\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FSalePriceUnitId\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FMaxQty\": 100000.0,\n"
//                + "      \"FIsATPCheck\": false,\n"
//                + "      \"FIsReturnPart\": false,\n"
//                + "      \"FIsInvoice\": false,\n"
//                + "      \"FIsReturn\": true,\n"
//                + "      \"FAllowPublish\": false,\n"
//                + "      \"FISAFTERSALE\": true,\n"
//                + "      \"FISPRODUCTFILES\": true,\n"
//                + "      \"FISWARRANTED\": false,\n"
//                + "      \"FWARRANTYUNITID\": \"D\",\n"
//                + "      \"FOutLmtUnit\": \"SAL\",\n"
//                + "      \"FIsTaxEnjoy\": false,\n"
//                + "      \"FUnValidateExpQty\": false\n"
//                + "    },\n"
//                + "    \"SubHeadEntity3\": {\n"
//                + "      \"FPurchaseUnitId\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FPurchasePriceUnitId\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FIsQuota\": false,\n"
//                + "      \"FQuotaType\": \"1\",\n"
//                + "      \"FIsVmiBusiness\": false,\n"
//                + "      \"FEnableSL\": false,\n"
//                + "      \"FIsPR\": false,\n"
//                + "      \"FIsReturnMaterial\": true,\n"
//                + "      \"FIsSourceControl\": false,\n"
//                + "      \"FPOBillTypeId\": {\n"
//                + "        \"FNUMBER\": \"WXCS1\"\n"
//                + "      },\n"
//                + "      \"FPrintCount\": 1,\n"
//                + "      \"FMinPackCount\": 1.0,\n"
//                + "      \"FIsEnableScheduleSub\": false\n"
//                + "    },\n"
//                + "    \"SubHeadEntity4\": {\n"
//                + "      \"FPlanningStrategy\": \"1\",\n"
//                + "      \"FMfgPolicyId\": {\n"
//                + "        \"FNumber\": \"ZZCL001_SYS\"\n"
//                + "      },\n"
//                + "      \"FFixLeadTimeType\": \"1\",\n"
//                + "      \"FVarLeadTimeType\": \"1\",\n"
//                + "      \"FCheckLeadTimeType\": \"1\",\n"
//                + "      \"FOrderIntervalTimeType\": \"3\",\n"
//                + "      \"FMaxPOQty\": 100000.0,\n"
//                + "      \"FEOQ\": 1.0,\n"
//                + "      \"FVarLeadTimeLotSize\": 1.0,\n"
//                + "      \"FIsMrpComBill\": true,\n"
//                + "      \"FIsMrpComReq\": false,\n"
//                + "      \"FReserveType\": \"1\",\n"
//                + "      \"FAllowPartAhead\": false,\n"
//                + "      \"FCanDelayDays\": 999,\n"
//                + "      \"FAllowPartDelay\": true,\n"
//                + "      \"FPlanOffsetTimeType\": \"1\",\n"
//                + "      \"FWriteOffQty\": 1.0\n"
//                + "    },\n"
//                + "    \"SubHeadEntity5\": {\n"
//                + "      \"FProduceUnitId\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FProduceBillType\": {\n"
//                + "        \"FNUMBER\": \"SCDD01_SYS\"\n"
//                + "      },\n"
//                + "      \"FIsSNCarryToParent\": false,\n"
//                + "      \"FIsProductLine\": false,\n"
//                + "      \"FBOMUnitId\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FIsMainPrd\": false,\n"
//                + "      \"FIsCoby\": false,\n"
//                + "      \"FIsECN\": false,\n"
//                + "      \"FIssueType\": \"1\",\n"
//                + "      \"FOverControlMode\": \"1\",\n"
//                + "      \"FMinIssueQty\": 1.0,\n"
//                + "      \"FISMinIssueQty\": false,\n"
//                + "      \"FIsKitting\": false,\n"
//                + "      \"FIsCompleteSet\": false,\n"
//                + "      \"FMinIssueUnitId\": {\n"
//                + "        \"FNUMBER\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FStandHourUnitId\": \"3600\",\n"
//                + "      \"FBackFlushType\": \"1\",\n"
//                + "      \"FIsEnableSchedule\": false\n"
//                + "    },\n"
//                + "    \"SubHeadEntity7\": {\n"
//                + "      \"FSubconUnitId\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FSubconPriceUnitId\": {\n"
//                + "        \"FNumber\": \"Pcs\"\n"
//                + "      },\n"
//                + "      \"FSubBillType\": {\n"
//                + "        \"FNUMBER\": \"WWDD01_SYS\"\n"
//                + "      }\n"
//                + "    },\n"
//                + "    \"SubHeadEntity6\": {\n"
//                + "      \"FCheckIncoming\": false,\n"
//                + "      \"FCheckProduct\": false,\n"
//                + "      \"FCheckStock\": false,\n"
//                + "      \"FCheckReturn\": false,\n"
//                + "      \"FCheckDelivery\": false,\n"
//                + "      \"FEnableCyclistQCSTK\": false,\n"
//                + "      \"FEnableCyclistQCSTKEW\": false,\n"
//                + "      \"FCheckEntrusted\": false,\n"
//                + "      \"FCheckOther\": false,\n"
//                + "      \"FIsFirstInspect\": false,\n"
//                + "      \"FCheckReturnMtrl\": false,\n"
//                + "      \"FCheckSubRtnMtrl\": false\n"
//                + "    },\n"
//                + "    \"FEntityInvPty\": [\n"
//                + "      {\n"
//                + "        \"FInvPtyId\": {\n"
//                + "          \"FNumber\": \"01\"\n"
//                + "        },\n"
//                + "        \"FIsEnable\": true,\n"
//                + "        \"FIsAffectPrice\": false,\n"
//                + "        \"FIsAffectPlan\": false,\n"
//                + "        \"FIsAffectCost\": false\n"
//                + "      },\n"
//                + "      {\n"
//                + "        \"FInvPtyId\": {\n"
//                + "          \"FNumber\": \"02\"\n"
//                + "        },\n"
//                + "        \"FIsEnable\": true,\n"
//                + "        \"FIsAffectPrice\": false,\n"
//                + "        \"FIsAffectPlan\": false,\n"
//                + "        \"FIsAffectCost\": false\n"
//                + "      },\n"
//                + "      {\n"
//                + "        \"FInvPtyId\": {\n"
//                + "          \"FNumber\": \"03\"\n"
//                + "        },\n"
//                + "        \"FIsEnable\": false,\n"
//                + "        \"FIsAffectPrice\": false,\n"
//                + "        \"FIsAffectPlan\": false,\n"
//                + "        \"FIsAffectCost\": false\n"
//                + "      },\n"
//                + "      {\n"
//                + "        \"FInvPtyId\": {\n"
//                + "          \"FNumber\": \"04\"\n"
//                + "        },\n"
//                + "        \"FIsEnable\": false,\n"
//                + "        \"FIsAffectPrice\": false,\n"
//                + "        \"FIsAffectPlan\": false,\n"
//                + "        \"FIsAffectCost\": false\n"
//                + "      },\n"
//                + "      {\n"
//                + "        \"FInvPtyId\": {\n"
//                + "          \"FNumber\": \"06\"\n"
//                + "        },\n"
//                + "        \"FIsEnable\": false,\n"
//                + "        \"FIsAffectPrice\": false,\n"
//                + "        \"FIsAffectPlan\": false,\n"
//                + "        \"FIsAffectCost\": false\n"
//                + "      }\n"
//                + "    ]\n"
//                + "  }\n"
//                + "}"
//                + "}";
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("新建物料推送:" + json);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, json, head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("新建物料返回:" + hfJson);
//        erpUtil.setERPLog(requestid, "新建物料", json, hfJson.toJSONString());
//        return hfJson;
//    }


    /**
     * 销售合同修改
     *
     * @param id
     * @param wlbms
     * @param salesSontractBill
     * @return
     * @throws Exception
     */
    /**
    public JSONObject editSalesSontract(String requestid, String id, List<OAMaterialDto> wlbms, SalesSontractBill salesSontractBill) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("{");
        stringBuffer.append("    \"formid\": \"CRM_XContract\",");
        stringBuffer.append("    \"data\": {");
        stringBuffer.append("        \"NeedUpDateFields\": [],");
        stringBuffer.append("        \"NeedReturnFields\": [],");
        stringBuffer.append("        \"IsDeleteEntry\": \"true\",");
        stringBuffer.append("        \"SubSystemId\": \"\",");
        stringBuffer.append("        \"IsVerifyBaseDataField\": \"false\",");
        stringBuffer.append("        \"IsEntryBatchFill\": \"true\",");
        stringBuffer.append("        \"ValidateFlag\": \"true\",");
        stringBuffer.append("        \"NumberSearch\": \"true\",");
        stringBuffer.append("        \"IsAutoAdjustField\": \"true\",");
        stringBuffer.append("        \"InterationFlags\": \"\",");
        stringBuffer.append("        \"IgnoreInterationFlag\": \"\",");
        stringBuffer.append("        \"IsControlPrecision\": \"false\",");
        stringBuffer.append("        \"ValidateRepeatJson\": \"false\",");
        stringBuffer.append("        \"Model\": {");
        stringBuffer.append("            \"FID\": " + id + ",");
        stringBuffer.append("            \"FBillTypeID\": {");
        stringBuffer.append("                \"FNUMBER\": \"XSHT01_SYS\"");
        stringBuffer.append("            },");
        stringBuffer.append("            \"FName\": \"" + salesSontractBill.getHtmc() + "\",");
        stringBuffer.append("            \"FSALEORGID\": {");
//        stringBuffer.append("                \"FNumber\": \""+salesSontractBill.getZz()+"\"");
        stringBuffer.append("                \"FNumber\": \"100\"");
        stringBuffer.append("            },");

        stringBuffer.append("            \"FCustId\": {");
        stringBuffer.append("                \"FNUMBER\": \"" + salesSontractBill.getKh() + "\"");
        stringBuffer.append("            },");
        stringBuffer.append("            \"FBDCUSTID\": {");
        stringBuffer.append("                \"FNUMBER\": \"" + salesSontractBill.getKh() + "\"");
        stringBuffer.append("            },");
        LocalDate currentDate = LocalDate.now();
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 格式化输出当前日期
        String formattedDate = currentDate.format(formatter);

        stringBuffer.append("            \"FDate\": \"" + formattedDate + "\",");
        stringBuffer.append("        ");
        stringBuffer.append("            \"FSalerId\": {");
        stringBuffer.append("                \"FNumber\": \"" + salesSontractBill.getXsy() + "\"");
        stringBuffer.append("            },");
        stringBuffer.append("            \"F_QIGM_Text1\": \"" + salesSontractBill.getOahtbh() + "\",");
        stringBuffer.append("            \"F_QIGM_Text11\": \"" + salesSontractBill.getKhhtbh() + "\",");
        stringBuffer.append("            \"F_QIGM_LargeText\": \"" + salesSontractBill.getKpfssm() + "\",");
        stringBuffer.append("            \"F_QIGM_Amount2\": " + Util.getIntValue(salesSontractBill.getWbfyje(), 0) + ",");
        stringBuffer.append("            \"F_qdrq\": \"" + salesSontractBill.getQdrq() + "\",");
        stringBuffer.append("\"FCRMContractFIN\":{\"FExchangeRate\":1,\"FRecConditionId\": {\n" + "                \"FNumber\": \"" + salesSontractBill.getSktj() + "\"\n" + "            },\"FSettleModeId\": {\n" + "                \"FNumber\": \"" + salesSontractBill.getJsfs() + "\"\n" + "            }" + "},");
        stringBuffer.append("            \"FContractClause\": [");
        stringBuffer.append("                {");
        stringBuffer.append("                    \"FEntryID\": 0,");
        stringBuffer.append("                    \"FClauseId\": {");
        stringBuffer.append("                        \"FNumber\": \"\"");
        stringBuffer.append("                    },");
        stringBuffer.append("                    \"FClauseDesc\": \"\"");
        stringBuffer.append("                }");
        stringBuffer.append("            ],");
        stringBuffer.append("            \"FCRMContractEntry\": [");
        int i = 0;
        for (OAMaterialDto wlbm : wlbms) {
            stringBuffer.append("                {");
            stringBuffer.append("                    \"FMaterialId\": {");
            stringBuffer.append("                        \"FNumber\": \"" + wlbm.getWlbm() + "\"");
            stringBuffer.append("                    },");
            stringBuffer.append("              ");
            stringBuffer.append("                    \"FQty\": " + wlbm.getSl() + ",");
            stringBuffer.append("                    \"FTaxPrice\": " + wlbm.getDj() + ",");
            stringBuffer.append("                    \"FSettleOrgId\": {");
            stringBuffer.append("                        \"FNumber\": \"100\"");
            stringBuffer.append("                    },");

            stringBuffer.append("\"FSRCTYPE\": \"CRM_Contract\",\n" + "                    \"FSRCBILLNO\": \"" + salesSontractBill.getJdhtbh() + "\",\n" + "                    \"FCRMContractEntry_Link\": {\n" + "                        \"FCRMContractEntry_Link_FRuleId\": \"CRM_Contract-CRM_XContract\",\n" + "                        \"FCRMContractEntry_Link_FSTableName\": \"T_CRM_CONTRACTENTRY\",\n" + "                        \"FCRMContractEntry_Link_FSBillId\": " + id + ",\n" + "                        \"FCRMContractEntry_Link_FSId\": " + wlbm.getFlid() + "\n" + "                    }");
            stringBuffer.append("                }");

            if (i != wlbms.size() - 1) {
                stringBuffer.append("        ,");
            }
            i++;
        }
        stringBuffer.append("            ],");
        stringBuffer.append("            \"F_QIGM_SaleOrderClause\": [");
        stringBuffer.append("                {");
        stringBuffer.append("                    \"FEntryID\": 0,");
        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
        stringBuffer.append("                        \"FNumber\": \"1\"");
        stringBuffer.append("                    },");
        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getZbtk() + "\",");
        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
        stringBuffer.append("                },");
        stringBuffer.append("                {");
        stringBuffer.append("                    \"FEntryID\": 0,");
        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
        stringBuffer.append("                        \"FNumber\": \"2\"");
        stringBuffer.append("                    },");
        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getYstk() + "\",");
        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
        stringBuffer.append("                },");
        stringBuffer.append("                {");
        stringBuffer.append("                    \"FEntryID\": 0,");
        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
        stringBuffer.append("                        \"FNumber\": \"3\"");
        stringBuffer.append("                    },");
        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getWytk() + "\",");
        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
        stringBuffer.append("                },");
        stringBuffer.append("                {");
        stringBuffer.append("                    \"FEntryID\": 0,");
        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
        stringBuffer.append("                        \"FNumber\": \"4\"");
        stringBuffer.append("                    },");
        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getJsyq() + "\",");
        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
        stringBuffer.append("                },");
        stringBuffer.append("                {");
        stringBuffer.append("                    \"FEntryID\": 0,");
        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
        stringBuffer.append("                        \"FNumber\": \"5\"");
        stringBuffer.append("                    },");
        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getFhyq() + "\",");
        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
        stringBuffer.append("                }");
        stringBuffer.append("            ]");
        stringBuffer.append("        }");
        stringBuffer.append("    }");
        stringBuffer.append("}");


        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        String sessionId = login();
        head.put("kdservice-sessionid", sessionId);
        writeLog("销售合同修改推送内容:" + stringBuffer);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, stringBuffer.toString(), head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("销售合同修改返回:" + hfJson.toJSONString());
        erpUtil.setERPLog(requestid, "销售合同修改", stringBuffer.toString(), hfJson.toJSONString());
        return hfJson;
    }

    /**
     * erp单据审核
     *
     * @param id
     * @param from
     * @return
     * @throws Exception
     */
    public JSONObject audit(String requestid, String id, String from) throws Exception {
        Map<String, Object> requestData = new HashMap<>();

        requestData.put("formid", from);

        Map<String, Object> data = new HashMap<>();
        data.put("Ids", id);
        requestData.put("data", data);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        String sessionId = login();
        head.put("kdservice-sessionid", sessionId);

        writeLog("审核推送内容:" + JSON.toJSONString(requestData));
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.AuditUrl, JSON.toJSONString(requestData), head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("审核返回:" + hfJson.toJSONString());
        erpUtil.setERPLog(requestid, "单据审核", JSON.toJSONString(requestData), hfJson.toJSONString());
        return hfJson;
    }

//    public JSONObject audit(String requestid, String id, String from) throws Exception {
//        String json = "{\n" + "\t\"formid\": \"" + from + "\",\n" + "\t\"data\": {\n" + "\t\t\"Ids\": \"" + id + "\"\n" + "\t}\n" + "}";
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        String sessionId = login();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("审核推送内容:" + json);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.AuditUrl, json, head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("审核返回:" + hfJson.toJSONString());
//        erpUtil.setERPLog(requestid, "单据审核", json, hfJson.toJSONString());
//        return hfJson;
//    }


    /**
     * erp单据提交
     *
     * @param id
     * @param from
     * @return
     * @throws Exception
     */
    public JSONObject submit(String requestid, String id, String from) throws Exception {
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("formid", from);

        Map<String, Object> data = new HashMap<>();
        data.put("Ids", id);
        jsonData.put("data", data);

        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        String sessionId = login();
        head.put("kdservice-sessionid", sessionId);
        writeLog("提交推送内容: " + JSON.toJSONString(jsonData));

        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SubmitUrl, JSON.toJSONString(jsonData), head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("提交返回: " + hfJson.toJSONString());
        erpUtil.setERPLog(requestid, "单据提交", jsonData.toString(), hfJson.toJSONString());

        return hfJson;
    }
//    public JSONObject submit(String requestid, String id, String from) throws Exception {
//        String json = "{\n" + "\t\"formid\": \"" + from + "\",\n" + "\t\"data\": {\n" + "\t\t\"Ids\": \"" + id + "\"\n" + "\t}\n" + "}";
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        String sessionId = login();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("提交推送内容:" + json);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SubmitUrl, json, head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("提交返回:" + hfJson.toJSONString());
//        erpUtil.setERPLog(requestid, "单据提交", json, hfJson.toJSONString());
//        return hfJson;
//    }

    /**
     * 销售合同新建
     *
     * @param wlbms
     * @param salesSontractBill
     * @return
     * @throws Exception
     */
    /**
    public JSONObject PostSalesSontract(String requestid, List<OAMaterialDto> wlbms, SalesSontractBill salesSontractBill) throws Exception {
        Map<String, Object> jsonData = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("NeedUpDateFields", new ArrayList<>());
        data.put("NeedReturnFields", new ArrayList<>());
        data.put("IsDeleteEntry", "true");
        data.put("SubSystemId", "");
        data.put("IsVerifyBaseDataField", "false");
        data.put("IsEntryBatchFill", "true");
        data.put("ValidateFlag", "true");
        data.put("NumberSearch", "true");
        data.put("IsAutoAdjustField", "true");
        data.put("InterationFlags", "");
        data.put("IgnoreInterationFlag", "");
        data.put("IsControlPrecision", "false");
        data.put("ValidateRepeatJson", "false");

        Map<String, Object> model = new HashMap<>();
        model.put("FID", 0);
        Map<String, Object> fBillTypeID = new HashMap<>();
        fBillTypeID.put("FNUMBER", salesSontractBill.getDjlx());
        model.put("FBillTypeID", fBillTypeID);
        Map<String, Object> fSaleGroupId = new HashMap<>();
        fSaleGroupId.put("FNUMBER", salesSontractBill.getFSaleGroupId());
        model.put("FSaleGroupId", fSaleGroupId);
        Map<String, Object> fOASQ = new HashMap<>();
        fOASQ.put("FSTAFFNUMBER", salesSontractBill.getFOASQ());
        model.put("FOASQ", fOASQ);
        model.put("FSYB", salesSontractBill.getFSYB());
        model.put("FSRQRZL", salesSontractBill.getFOADX());
        model.put("FName", salesSontractBill.getHtmc());
        Map<String, Object> fSaleOrgId = new HashMap<>();
        fSaleOrgId.put("FNumber", "100");
        model.put("FSALEORGID", fSaleOrgId);
        Map<String, Object> fCustId = new HashMap<>();
        fCustId.put("FNUMBER", salesSontractBill.getKh());
        model.put("FCustId", fCustId);
        Map<String, Object> fBDCustId = new HashMap<>();
        fBDCustId.put("FNUMBER", salesSontractBill.getKh());
        model.put("FBDCUSTID", fBDCustId);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        model.put("FDate", formattedDate);
        Map<String, Object> fSalerId = new HashMap<>();
        fSalerId.put("FNumber", salesSontractBill.getXsy());
        model.put("FSalerId", fSalerId);
        model.put("F_QIGM_Text1", salesSontractBill.getOahtbh());
        model.put("F_QIGM_Text11", salesSontractBill.getKhhtbh());
        Map<String, Object> fCRMContractFIN = new HashMap<>();
        fCRMContractFIN.put("FExchangeRate", 1);
        Map<String, Object> fRecConditionId = new HashMap<>();
        fRecConditionId.put("FNumber", salesSontractBill.getSktj());
        fCRMContractFIN.put("FRecConditionId", fRecConditionId);
        Map<String, Object> fSettleModeId = new HashMap<>();
        fSettleModeId.put("FNumber", salesSontractBill.getJsfs());
        fCRMContractFIN.put("FSettleModeId", fSettleModeId);
        model.put("FCRMContractFIN", fCRMContractFIN);
        List<Map<String, Object>> fContractClause = new ArrayList<>();
        Map<String, Object> contractClause = new HashMap<>();
        contractClause.put("FEntryID", 0);
        Map<String, Object> fClauseId = new HashMap<>();
        fClauseId.put("FNumber", "");
        contractClause.put("FClauseId", fClauseId);
        contractClause.put("FClauseDesc", "");
        fContractClause.add(contractClause);
        model.put("FContractClause", fContractClause);
        List<Map<String, Object>> fCRMContractEntry = new ArrayList<>();
        for (OAMaterialDto wlbm : wlbms) {
            Map<String, Object> contractEntry = new HashMap<>();
            Map<String, Object> fMaterialId = new HashMap<>();
            fMaterialId.put("FNumber", wlbm.getWlbm());
            contractEntry.put("FMaterialId", fMaterialId);
            contractEntry.put("FQty", wlbm.getSl());
            contractEntry.put("FTaxPrice", wlbm.getDj());
            contractEntry.put("FXSDJ", wlbm.getDij());
            contractEntry.put("FDELIVERYDATE", wlbm.getFDELIVERYDATE());
            contractEntry.put("FEntryNote", wlbm.getFEntryNote());
            contractEntry.put("F_JJGH", wlbm.getJjghrq());
            contractEntry.put("F_DJ", wlbm.getF_DJ());
            contractEntry.put("F_RJJE", wlbm.getF_RJJE());
            contractEntry.put("F_QYJ", wlbm.getF_QYJ());
            Map<String, Object> fSettleOrgId = new HashMap<>();
            fSettleOrgId.put("FNumber", "100");
            contractEntry.put("FSettleOrgId", fSettleOrgId);
            fCRMContractEntry.add(contractEntry);
        }
        model.put("FCRMContractEntry", fCRMContractEntry);
        List<Map<String, Object>> fQIGMSaleOrderClause = new ArrayList<>();
        if (!"".equals(salesSontractBill.getZbtk())) {
            Map<String, Object> qigmClause1 = new HashMap<>();
            qigmClause1.put("FEntryID", 0);
            Map<String, Object> fQIGMClauseId1 = new HashMap<>();
            fQIGMClauseId1.put("FNumber", "1");
            qigmClause1.put("F_QIGM_ClauseId", fQIGMClauseId1);
            qigmClause1.put("F_QIGM_ClauseDesc", "");
            qigmClause1.put("F_QIGM_XPKID_C", 0);
            qigmClause1.put("F_QIGM_httk", salesSontractBill.getZbtk());
            qigmClause1.put("F_QIGM_httk_Tag", "");
            fQIGMSaleOrderClause.add(qigmClause1);
        }
        if (!"".equals(salesSontractBill.getYstk())) {
            Map<String, Object> qigmClause2 = new HashMap<>();
            qigmClause2.put("FEntryID", 0);
            Map<String, Object> fQIGMClauseId2 = new HashMap<>();
            fQIGMClauseId2.put("FNumber", "2");
            qigmClause2.put("F_QIGM_ClauseId", fQIGMClauseId2);
            qigmClause2.put("F_QIGM_ClauseDesc", "");
            qigmClause2.put("F_QIGM_XPKID_C", 0);
            qigmClause2.put("F_QIGM_httk", salesSontractBill.getYstk());
            qigmClause2.put("F_QIGM_httk_Tag", "");
            fQIGMSaleOrderClause.add(qigmClause2);
        }
        if (!"".equals(salesSontractBill.getWytk())) {
            Map<String, Object> qigmClause3 = new HashMap<>();
            qigmClause3.put("FEntryID", 0);
            Map<String, Object> fQIGMClauseId3 = new HashMap<>();
            fQIGMClauseId3.put("FNumber", "3");
            qigmClause3.put("F_QIGM_ClauseId", fQIGMClauseId3);
            qigmClause3.put("F_QIGM_ClauseDesc", "");
            qigmClause3.put("F_QIGM_XPKID_C", 0);
            qigmClause3.put("F_QIGM_httk", salesSontractBill.getWytk());
            qigmClause3.put("F_QIGM_httk_Tag", "");
            fQIGMSaleOrderClause.add(qigmClause3);
        }
        if (!"".equals(salesSontractBill.getJsyq())) {
            Map<String, Object> qigmClause4 = new HashMap<>();
            qigmClause4.put("FEntryID", 0);
            Map<String, Object> fQIGMClauseId4 = new HashMap<>();
            fQIGMClauseId4.put("FNumber", "4");
            qigmClause4.put("F_QIGM_ClauseId", fQIGMClauseId4);
            qigmClause4.put("F_QIGM_ClauseDesc", "");
            qigmClause4.put("F_QIGM_XPKID_C", 0);
            qigmClause4.put("F_QIGM_httk", salesSontractBill.getJsyq());
            qigmClause4.put("F_QIGM_httk_Tag", "");
            fQIGMSaleOrderClause.add(qigmClause4);
        }
        if (!"".equals(salesSontractBill.getFhyq())) {
            Map<String, Object> qigmClause5 = new HashMap<>();
            qigmClause5.put("FEntryID", 0);
            Map<String, Object> fQIGMClauseId5 = new HashMap<>();
            fQIGMClauseId5.put("FNumber", "5");
            qigmClause5.put("F_QIGM_ClauseId", fQIGMClauseId5);
            qigmClause5.put("F_QIGM_ClauseDesc", "");
            qigmClause5.put("F_QIGM_XPKID_C", 0);
            qigmClause5.put("F_QIGM_httk", salesSontractBill.getFhyq());
            qigmClause5.put("F_QIGM_httk_Tag", "");
            fQIGMSaleOrderClause.add(qigmClause5);
        }
        if (!"".equals(salesSontractBill.getKpfssm())) {
            Map<String, Object> qigmClause6 = new HashMap<>();
            qigmClause6.put("FEntryID", 0);
            Map<String, Object> fQIGMClauseId6 = new HashMap<>();
            fQIGMClauseId6.put("FNumber", "6");
            qigmClause6.put("F_QIGM_ClauseId", fQIGMClauseId6);
            qigmClause6.put("F_QIGM_ClauseDesc", "");
            qigmClause6.put("F_QIGM_XPKID_C", 0);
            qigmClause6.put("F_QIGM_httk", salesSontractBill.getKpfssm());
            qigmClause6.put("F_QIGM_httk_Tag", "");
            fQIGMSaleOrderClause.add(qigmClause6);
        }
        if (fQIGMSaleOrderClause.size()>0) {
            model.put("F_QIGM_SaleOrderClause", fQIGMSaleOrderClause);
        }
        data.put("Model", model);
        jsonData.put("formid", "CRM_Contract");
        jsonData.put("data", data);
        HttpManager httpManager = new HttpManager();
        Map<String, String> head = new HashMap<>();
        String sessionId = login();
        head.put("kdservice-sessionid", sessionId);
        String jsonString = new ObjectMapper().writeValueAsString(jsonData);
        writeLog("销售合同推送内容: " + jsonString);
        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, jsonString, head);
        JSONObject hfJson = JSONObject.parseObject(hf);
        writeLog("销售合同返回: " + hfJson.toJSONString());
        erpUtil.setERPLog(requestid, "销售合同新建", jsonString, hfJson.toJSONString());

        return hfJson;
    }


//    public JSONObject PostSalesSontract(String requestid, List<OAMaterialDto> wlbms, SalesSontractBill salesSontractBill) throws Exception {
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("{");
//        stringBuffer.append("    \"formid\": \"CRM_Contract\",");
//        stringBuffer.append("    \"data\": {");
//        stringBuffer.append("        \"NeedUpDateFields\": [],");
//        stringBuffer.append("        \"NeedReturnFields\": [],");
//        stringBuffer.append("        \"IsDeleteEntry\": \"true\",");
//        stringBuffer.append("        \"SubSystemId\": \"\",");
//        stringBuffer.append("        \"IsVerifyBaseDataField\": \"false\",");
//        stringBuffer.append("        \"IsEntryBatchFill\": \"true\",");
//        stringBuffer.append("        \"ValidateFlag\": \"true\",");
//        stringBuffer.append("        \"NumberSearch\": \"true\",");
//        stringBuffer.append("        \"IsAutoAdjustField\": \"true\",");
//        stringBuffer.append("        \"InterationFlags\": \"\",");
//        stringBuffer.append("        \"IgnoreInterationFlag\": \"\",");
//        stringBuffer.append("        \"IsControlPrecision\": \"false\",");
////        stringBuffer.append("        \"IsAutoSubmitAndAudit\": \"true\",");
//        stringBuffer.append("        \"ValidateRepeatJson\": \"false\",");
//        stringBuffer.append("        \"Model\": {");
//        stringBuffer.append("            \"FID\": 0,");
//        stringBuffer.append("            \"FBillTypeID\": {");
//        stringBuffer.append("                \"FNUMBER\": \"" + salesSontractBill.getDjlx() + "\"");
//        stringBuffer.append("            },");
//        stringBuffer.append("            \"FSaleGroupId\": {");
//        stringBuffer.append("                \"FNUMBER\": \"" + salesSontractBill.getFSaleGroupId() + "\"");
//        stringBuffer.append("            },");
//        stringBuffer.append("            \"FOASQ\": {");
//        stringBuffer.append("                \"FSTAFFNUMBER\": \"" + salesSontractBill.getFOASQ() + "\"");
//        stringBuffer.append("            },");
//        stringBuffer.append("            \"FSYB\": \"" + salesSontractBill.getFSYB() + "\",");
//        stringBuffer.append("            \"FSRQRZL\": \"" + salesSontractBill.getFOADX() + "\",");
//        stringBuffer.append("            \"FName\": \"" + salesSontractBill.getHtmc() + "\",");
//        stringBuffer.append("            \"FSALEORGID\": {");
////        stringBuffer.append("                \"FNumber\": \""+salesSontractBill.getZz()+"\"");
//        stringBuffer.append("                \"FNumber\": \"100\"");
//        stringBuffer.append("            },");
//        stringBuffer.append("            \"FCustId\": {");
//        stringBuffer.append("                \"FNUMBER\": \"" + salesSontractBill.getKh() + "\"");
//        stringBuffer.append("            },");
//        stringBuffer.append("            \"FBDCUSTID\": {");
//        stringBuffer.append("                \"FNUMBER\": \"" + salesSontractBill.getKh() + "\"");
//        stringBuffer.append("            },");
//        LocalDate currentDate = LocalDate.now();
//        // 定义日期格式
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        // 格式化输出当前日期
//        String formattedDate = currentDate.format(formatter);
//
//        stringBuffer.append("            \"FDate\": \"" + formattedDate + "\",");
//        stringBuffer.append("        ");
//        stringBuffer.append("            \"FSalerId\": {");
//        stringBuffer.append("                \"FNumber\": \"" + salesSontractBill.getXsy() + "\"");
//        stringBuffer.append("            },");
//        stringBuffer.append("            \"F_QIGM_Text1\": \"" + salesSontractBill.getOahtbh() + "\",");
//        stringBuffer.append("            \"F_QIGM_Text11\": \"" + salesSontractBill.getKhhtbh() + "\",");
////        stringBuffer.append("            \"F_QIGM_LargeText\": \"" + salesSontractBill.getKpfssm() + "\",");
//        stringBuffer.append("            \"F_QIGM_Amount2\": " + Util.getIntValue(salesSontractBill.getWbfyje(), 0) + ",");
//        stringBuffer.append("            \"F_qdrq\": \"" + salesSontractBill.getQdrq() + "\",");
//        stringBuffer.append("            \"F_OADH\": \"" + salesSontractBill.getRequestid() + "\",");
//        stringBuffer.append("            \"F_OABM\": \"" + salesSontractBill.getBh() + "\",");
//        stringBuffer.append("\"FCRMContractFIN\":{\"FExchangeRate\":1,\"FRecConditionId\": {\n" + "                \"FNumber\": \"" + salesSontractBill.getSktj() + "\"\n" + "            },\"FSettleModeId\": {\n" + "                \"FNumber\": \"" + salesSontractBill.getJsfs() + "\"\n" + "            }" + "},");
//        stringBuffer.append("            \"FContractClause\": [");
//        stringBuffer.append("                {");
//        stringBuffer.append("                    \"FEntryID\": 0,");
//        stringBuffer.append("                    \"FClauseId\": {");
//        stringBuffer.append("                        \"FNumber\": \"\"");
//        stringBuffer.append("                    },");
//        stringBuffer.append("                    \"FClauseDesc\": \"\"");
//        stringBuffer.append("                }");
//        stringBuffer.append("            ],");
//        stringBuffer.append("            \"FCRMContractEntry\": [");
//
//        int i = 0;
//        for (OAMaterialDto wlbm : wlbms) {
//            stringBuffer.append("                {");
//            stringBuffer.append("                    \"FMaterialId\": {");
//            stringBuffer.append("                        \"FNumber\": \"" + wlbm.getWlbm() + "\"");
//            stringBuffer.append("                    },");
//            stringBuffer.append("              ");
//            stringBuffer.append("                    \"FQty\": " + wlbm.getSl() + ",");
//            stringBuffer.append("                    \"FTaxPrice\": " + wlbm.getDj() + ",");
//            stringBuffer.append("                    \"FXSDJ\": " + wlbm.getDij() + ",");
//            stringBuffer.append("                    \"FDELIVERYDATE\": \"" + wlbm.getFDELIVERYDATE() + "\",");
//            stringBuffer.append("                    \"FEntryNote\": \"" + wlbm.getFEntryNote() + "\",");
//            stringBuffer.append("                    \"F_JJGH\": \"" + wlbm.getJjghrq() + "\",");
//
//            stringBuffer.append("                    \"F_DJ\": \"" + wlbm.getF_DJ() + "\",");
//            stringBuffer.append("                    \"F_RJJE\": \"" + wlbm.getF_RJJE() + "\",");
////            stringBuffer.append("                    \"F_QWBFY\": \"" + wlbm.getF_QWBFY() + "\",");
//            stringBuffer.append("                    \"F_QYJ\": \"" + wlbm.getF_QYJ() + "\",");
//
//            stringBuffer.append("                    \"FSettleOrgId\": {");
////            stringBuffer.append("                        \"FNumber\": \""+salesSontractBill.getZz()+"\"");
//            stringBuffer.append("                        \"FNumber\": \"100\"");
//            stringBuffer.append("                    }");
//            stringBuffer.append("                }");
//
//            if (i != wlbms.size() - 1) {
//                stringBuffer.append("        ,");
//            }
//            i++;
//        }
//        stringBuffer.append("            ],");
//        stringBuffer.append("            \"F_QIGM_SaleOrderClause\": [");
//        stringBuffer.append("                {");
//        stringBuffer.append("                    \"FEntryID\": 0,");
//        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
//        stringBuffer.append("                        \"FNumber\": \"1\"");
//        stringBuffer.append("                    },");
//        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
//        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
//        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getZbtk() + "\",");
//        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
//        stringBuffer.append("                },");
//        stringBuffer.append("                {");
//        stringBuffer.append("                    \"FEntryID\": 0,");
//        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
//        stringBuffer.append("                        \"FNumber\": \"2\"");
//        stringBuffer.append("                    },");
//        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
//        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
//        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getYstk() + "\",");
//        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
//        stringBuffer.append("                },");
//        stringBuffer.append("                {");
//        stringBuffer.append("                    \"FEntryID\": 0,");
//        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
//        stringBuffer.append("                        \"FNumber\": \"3\"");
//        stringBuffer.append("                    },");
//        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
//        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
//        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getWytk() + "\",");
//        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
//        stringBuffer.append("                },");
//        stringBuffer.append("                {");
//        stringBuffer.append("                    \"FEntryID\": 0,");
//        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
//        stringBuffer.append("                        \"FNumber\": \"4\"");
//        stringBuffer.append("                    },");
//        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
//        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
//        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getJsyq() + "\",");
//        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
//        stringBuffer.append("                },");
//        stringBuffer.append("                {");
//        stringBuffer.append("                    \"FEntryID\": 0,");
//        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
//        stringBuffer.append("                        \"FNumber\": \"5\"");
//        stringBuffer.append("                    },");
//        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
//        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
//        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getFhyq() + "\",");
//        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
//        stringBuffer.append("                },");
//        stringBuffer.append("                {");
//        stringBuffer.append("                    \"FEntryID\": 0,");
//        stringBuffer.append("                    \"F_QIGM_ClauseId\": {");
//        stringBuffer.append("                        \"FNumber\": \"6\"");
//        stringBuffer.append("                    },");
//        stringBuffer.append("                    \"F_QIGM_ClauseDesc\": \"\",");
//        stringBuffer.append("                    \"F_QIGM_XPKID_C\": 0,");
//        stringBuffer.append("                    \"F_QIGM_httk\": \"" + salesSontractBill.getKpfssm() + "\",");
//        stringBuffer.append("                    \"F_QIGM_httk_Tag\": \"\"");
//        stringBuffer.append("                }");
//        stringBuffer.append("            ]");
//        stringBuffer.append("        }");
//        stringBuffer.append("    }");
//        stringBuffer.append("}");
//        HttpManager httpManager = new HttpManager();
//        Map<String, String> head = new HashMap<>();
//        String sessionId = login();
//        head.put("kdservice-sessionid", sessionId);
//        writeLog("销售合同推送内容:" + stringBuffer);
//        String hf = httpManager.postJsonDataSSL("http://" + erpUtil.Erp_Host + ":" + erpUtil.Erp_Port + "/" + erpUtil.SaveUrl, stringBuffer.toString(), head);
//        JSONObject hfJson = JSONObject.parseObject(hf);
//        writeLog("销售合同返回:" + hfJson.toJSONString());
//        erpUtil.setERPLog(requestid, "销售合同新建", stringBuffer.toString(), hfJson.toJSONString());
//        return hfJson;
//    }
//
     */

}
