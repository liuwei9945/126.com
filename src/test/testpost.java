package test;

import com.alibaba.fastjson.JSONObject;
import com.cloudstore.dev.api.util.HttpManager;
import weaver.general.TimeUtil;
import weaver.general.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class testpost {
    public static void main(String[] args) {
        // 示例：data：转换规则定义的请求参数
        String acctID ="";
        String username = "";
        String lcid = "";
        String appid = "";
        String appsecret ="";
        String urlip = "";
        String fromCurrency ="USD";
        String toCurrency ="CNY";
        String fdate = "";
        // return data + "～转换后的值";

        HttpManager http=new HttpManager();
        if("".equals(urlip)){ urlip="http://192.168.152.59";}
        if("".equals(acctID)){ acctID="6309b2985cde72";}
        if("".equals(username)){ username="oasystem";}
        if("".equals(lcid)){ lcid="2052";}
        if("".equals(appid)){ appid="233254_XccDS7GHRlgf5X1E76XA1ZWv4gQU7oML";}
        if("".equals(appsecret)){ appsecret="5de707376d6b40a0b84c0de1795c3983";}
        if("".equals(fdate)){ fdate= TimeUtil.getCurrentDateString();}


        Map<String,String > bodymap =new HashMap<>();
        bodymap.put("acctID",acctID);
        bodymap.put("username",username);
        bodymap.put("lcid",lcid);
        bodymap.put("appid",appid);
        bodymap.put("appsecret",appsecret);

        String loginurl=urlip+"/k3cloud/Kingdee.BOS.WebApi.ServicesStub.AuthService.LoginByAppSecret.common.kdsvc";
        String loginstr=  http.postData(loginurl,bodymap);
        JSONObject loginobj=JSONObject.parseObject(loginstr);
        System.out.println("loginobj = " + loginobj);
        String LoginResultType=loginobj.getString("LoginResultType");
        String hl="";
        if("1".equals(LoginResultType)) {
            try {
                String KDSVCSessionId = loginobj.getString("KDSVCSessionId");
                String SessionId = loginobj.getJSONObject("Context").getString("SessionId");

                String geturl = urlip + "/k3cloud/SUNGROW.CustomFormService.GetExchangeRate,SUNGROW.common.kdsvc";


                Map<String, String> headermap = new HashMap<>();
                headermap.put("ASP.NET_SessionId", SessionId);
                headermap.put("kdservice-sessionid", KDSVCSessionId);
                headermap.put("Content-Type", "application/json");
                headermap.put("Cookie", "ASP.NET_SessionId=" + SessionId + "; kdservice-sessionid=" + KDSVCSessionId);
                // headermap.put("Content-Length","116");


                bodymap = new HashMap<>();
                bodymap.put("fromCurrency", fromCurrency);
                bodymap.put("toCurrency", toCurrency);
                bodymap.put("rateType", "HLTX01_SYS");
                bodymap.put("fdate", fdate);

                String retstr = http.postJsonData(geturl, JSONObject.toJSONString(bodymap), headermap);

                JSONObject retobj = JSONObject.parseObject(retstr);
                System.out.println("loginobj = " + retobj);
                String IsSuccess = retobj.getJSONObject("Result").getJSONObject("ResponseStatus").getString("IsSuccess");

                if ("true".equals(IsSuccess)) {
                    hl = retobj.getJSONObject("Result").getJSONObject("ResponseStatus").getString("value");

                }
                //String retstr="1";

            }catch(Exception s){
                s.printStackTrace();
            }
        }
        System.out.println("retstr = " + hl);
    }
}
