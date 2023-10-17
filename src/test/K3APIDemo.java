package test;

import com.alibaba.fastjson.JSONObject;
import kingdee.bos.webapi.client.K3CloudApiClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;


/**
 * 说明
 * 修改时
 * 类名要与文件名保持一致
 * class文件存放位置与路径保持一致。
 * 请把编译后的class文件，放在对应的目录中才能生效
 * 注意 同一路径下java名不能相同。
 * @author Administrator
 *
 */
public class K3APIDemo {


    public static void main(String[] args) throws Exception {
        K3APIDemo restfulDemo = new K3APIDemo();
        restfulDemo.doAction();
    }

    /**
     *restful接口调用案例
     *以getModeDataPageList为例
     */
    public void doAction() throws Exception {

        // 使用webapi引用组件Kingdee.BOS.WebApi.Client.dll
        K3CloudApiClient client = new K3CloudApiClient("http://192.168.152.59/k3cloud/");
        Boolean loginResult = client.login("20200509162155462","王杰","Sr@20220422",2052);
        System.out.println("loginResult=="+loginResult);
//登录结果类型等于1，代表登录成功
String returnstr="";
        String data ="{\"IsAutoSubmitAndAudit\":\"false\",\"Model\":{\"FID\":\"0\",\"FBillNo\":\"\",\"FDate\":\"2022-09-01\",\"FBillTypeID\":{\"FNumber\":\"YFD02_SYS\"},\"FISINIT \":\"\",\"FENDDATE_H\":\"2022-09-01\",\"FSUPPLIERID\":{\"FNumber\":\"INTP10003\"},\"FCURRENCYID\":{\"FNumber\":\"PRE001\"},\"FPayConditon\":{\"FNumber\":\"FKTJ03_SYS\"},\"FISPRICEEXCLUDETAX\":\"true\",\"FBUSINESSTYPE\":\"FY\",\"FISTAX\":\"true\",\"FSETTLEORGID\":{\"FNumber\":\"100.01.001\"},\"FPAYORGID\":{\"FNumber\":\"100.01.001\"},\"FSetAccountType\":\"1\",\"FISTAXINCOST\":\"false\",\"FISHookMatch\":\"false\",\"FCancelStatus\":\"A\",\"FISBYIV\":\"false\",\"FISGENHSADJ\":\"false\",\"FISINVOICEARLIER\":\"false\",\"FWBOPENQTY\":\"false\",\"FIsGeneratePlanByCostItem\":\"false\",\"F_ora_Base\":{\"FNumber\":\"F1-20020\"},\"F_ora_Base1\":{\"FNumber\":\"BM000003\"},\"F_ora_Assistant\":{\"FNumber\":\"JTW01\"},\"F_ora_Text\":\"123123123\",\"F_ora_Base2\":{\"FNumber\":\"10007829\"},\"F_ora_Assistant1\":{\"FNumber\":\"DDLX002\"},\"F_ora_Base3\":{\"FNumber\":\"MBF1.01.03.08\"},\"F_PAEZ_SFXMXG\":\"false\",\"F_PAEZ_HTH\":\"2323\",\"F_PAEZ_FDZT\":\"1212\",\"F_PAEZ_FPDPQK\":\"l\",\"F_PAEZ_FKYT\":{\"FNumber\":\"SFKYT08_SYS\"},\"F_PAEZ_BTZGS\":{\"FNumber\":\"100.01.024\"},\"FsubHeadSuppiler\":{\"FORDERID\":{\"FNumber\":\"INTP10003\"},\"FTRANSFERID\":{\"FNumber\":\"INTP10003\"},\"FChargeId\":{\"FNumber\":\"INTP10003\"}},\"FsubHeadFinc\":{\"FACCNTTIMEJUDGETIME\":\"2022-09-01\",\"FSettleTypeID\":{\"FNumber\":\"JSFS01_SYS\"},\"FMAINBOOKSTDCURRID\":{\"FNumber\":\"PRE001\"},\"FEXCHANGETYPE\":{\"FNumber\":\"HLTX01_SYS\"},\"FExchangeRate\":\"1\",\"FTaxAmountFor\":\"1\",\"FNoTaxAmountFor\":\"100\"},\"FEntityDetail\":[{\"FCOSTID\":{\"FNumber\":\"FYXM11_SYS\"},\"FPrice\":\"10.619469\",\"FPriceQty\":\"1\",\"FTaxPrice\":\"1\",\"FPriceWithTax\":\"1\",\"FEntryTaxRate\":\"100\",\"FNoTaxAmountFor_D\":\"100\",\"FTAXAMOUNTFOR_D\":\"100\",\"FALLAMOUNTFOR_D\":\"100\",\"F_PAEZ_CXJE\":\"0\",\"FINCLUDECOST\":\"FALSE\",\"FISOUTSTOCK\":\"FALSE\",\"FIsFree\":\"FALSE\",\"FPriceBaseDen\":\"1\",\"FStockBaseNum\":\"1\"}],\"FEntityPlan\":[{\"FENDDATE\":\"2022-09-01\",\"FPAYAMOUNTFOR\":\"100\",\"FPAYRATE\":\"100%\",\"FQTY_P\":\"1\"}]}}"
                ;
        if (loginResult)
        {
            returnstr=  client.save("AP_Payable",data);
        }
        System.out.println("returnstr=="+returnstr);

    }




}
