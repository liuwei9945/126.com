package weaver.interfaces.jd.erp.util;

import weaver.conn.RecordSet;
import weaver.formmode.setup.ModeRightInfo;
import weaver.general.BaseBean;
import weaver.general.Util;

public class ERPUtil extends BaseBean {

    public ERPUtil(){
        if (Erp_Host == null || "".equals(Erp_Host)) {
            Erp_Host = getPropValue("ERPFieldIdSet","Erp_Host");
        }
        if (Erp_Host == null || "".equals(Erp_Host)) {
            Erp_Host = "";
        }

        if (Erp_Port == null || "".equals(Erp_Port)) {
            Erp_Port = getPropValue("ERPFieldIdSet","Erp_Port");
        }
        if (Erp_Port == null || "".equals(Erp_Port)) {
            Erp_Port = "80";
        }


        if (AcctId == null || "".equals(AcctId)) {
            AcctId = getPropValue("ERPFieldIdSet","AcctId");
        }
        if (AcctId == null || "".equals(AcctId)) {
            AcctId = "";
        }

        if (UserName == null || "".equals(UserName)) {
            UserName = getPropValue("ERPFieldIdSet","UserName");
        }
        if (UserName == null || "".equals(UserName)) {
            UserName = "";
        }

        if (PassWord == null || "".equals(PassWord)) {
            PassWord = getPropValue("ERPFieldIdSet","PassWord");
        }
        if (PassWord == null || "".equals(PassWord)) {
            PassWord = "";
        }

        if (LCID == null || "".equals(LCID)) {
            LCID = getPropValue("ERPFieldIdSet","LCID");
        }
        if (LCID == null || "".equals(LCID)) {
            LCID = "2052";
        }

        if (LoginUrl == null || "".equals(LoginUrl)) {
            LoginUrl = getPropValue("ERPFieldIdSet","LoginUrl");
        }
        if (LoginUrl == null || "".equals(LoginUrl)) {
            LoginUrl = "/k3cloud/Kingdee.BOS.WebApi.ServicesStub.AuthService.ValidateUser.common.kdsvc";
        }

        if (SaveUrl == null || "".equals(SaveUrl)) {
            SaveUrl = getPropValue("ERPFieldIdSet","SaveUrl");
        }
        if (SaveUrl == null || "".equals(SaveUrl)) {
            SaveUrl = "/k3cloud/Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Save.common.kdsvc";
        }

        if (AuditUrl == null || "".equals(AuditUrl)) {
            AuditUrl = getPropValue("ERPFieldIdSet","AuditUrl");
        }
        if (AuditUrl == null || "".equals(AuditUrl)) {
            AuditUrl = "/k3cloud/Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Audit.common.kdsvc";
        }

        if (SubmitUrl == null || "".equals(SubmitUrl)) {
            SubmitUrl = getPropValue("ERPFieldIdSet","SubmitUrl");
        }
        if (SubmitUrl == null || "".equals(SubmitUrl)) {
            SubmitUrl = "/k3cloud/Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.Submit.common.kdsvc";
        }

        if (FileUrl == null || "".equals(FileUrl)) {
            FileUrl = getPropValue("ERPFieldIdSet","FileUrl");
        }
        if (FileUrl == null || "".equals(FileUrl)) {
            FileUrl = "/k3cloud/Kingdee.BOS.WebApi.ServicesStub.DynamicFormService.AttachmentUpLoad.common.kdsvc";
        }

        if (ZZBM == null || "".equals(ZZBM)) {
            ZZBM = getPropValue("ERPFieldIdSet","ZZBM");
        }
        if (ZZBM == null || "".equals(ZZBM)) {
            ZZBM = "100";
        }

    }


    /**接口地址*/public String Erp_Host = "";
    /**接口端口*/public String Erp_Port = "";
    /**账套id*/public String AcctId = "";

    /**用户名*/public String UserName = "";

    /**密码*/public String PassWord = "";

    /**不知道什么玩意 固定值*/public String LCID = "";

    /**登录接口地址*/public String LoginUrl = "";
    /**保存接口地址*/public String SaveUrl = "";

    /**审核接口地址*/public String AuditUrl = "";
    /**提交接口地址*/public String SubmitUrl = "";


    public String FileUrl = "";
    public String ZZBM = "";


    public  void setERPLog(String requestid,String name,String tsw,String fhw){
        RecordSet rs = new RecordSet();
        ModeRightInfo modeRightInfo = new ModeRightInfo();
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
        //获取当前日期 yyyy-MM-dd
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        //获取当前时间 hh:mm:ss
        String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        String sql = "INSERT INTO uf_jderplog(  xglc, jkmc, tsw, fhw, " +
                "formmodeid, modedatacreater, modedatacreatertype, modedatacreatedate, " +
                "modedatacreatetime, MODEUUID)" +
                " VALUES (?,?,?,?,1405,1,0,?,?,?)";
        rs.executeUpdate(sql,requestid,name,tsw,fhw,date,time,uuid);
        sql = "select id from uf_jderplog where MODEUUID = '" + uuid + "' ";
        rs.execute(sql);
        if (rs.next()) {
            modeRightInfo.setNewRight(true);
            int billid = rs.getInt("id");
            modeRightInfo.editModeDataShare(1, 1405, billid);
        }

    }

}
