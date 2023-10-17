package test;

import com.weaver.general.MD5;
import com.weaver.upgrade.FunctionUpgradeUtil;
import ln.LN;
import ln.LNBean;
import ln.LNParse;
import weaver.conn.RecordSet;
import weaver.general.GCONST;
import weaver.general.Util;
import weaver.system.License;

import java.io.File;
import java.util.Arrays;

public class newtestcalss {
    public static void main(String[] args) {

        String filename = "D:\\FWSYSTEM\\E9\\ecology\\"+ "license" + File.separatorChar + "3F0C2F1AFB90E3E8720645D400FB07C6.license";
        System.out.println("filename = " +filename);
        LNParse lnp = new LNParse();
String ncid="";
        try {
            LNBean lnb = lnp.getLNBean(filename, "ecology9");
            ncid=   Util.null2String(lnb.getCid());
        } catch (Exception var4) {

        }
        System.out.println("ncid" + ncid);
       // String var2 = (new License()).getCId();
        String var3 = (new MD5()).getMD5ofStr(ncid + FunctionUpgradeUtil.getMenuStatus(140, 1, Integer.parseInt(ncid)));
       System.out.println("var2 = " +ncid);
        System.out.println("var3 = " +var3);

    }
}
