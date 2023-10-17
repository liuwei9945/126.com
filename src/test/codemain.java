package test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.weaver.general.GCONSTUClient;

import weaver.conn.RecordSet;
import weaver.general.SecurityHelper;
import wscheck.MD5Coder;







public class codemain {

	public static void main(String[] args) throws Exception {
		String url="http://127.0.0.1:20191/integration/hlszyd/settingSql.jsp?operation=edit&ZBQYJBXX_URL=select+distinct+requestid%2Cnodeid%2Cworkflowid%2Cworkflowtype+from+workflow_currentoperator+where+workflowid%3C%3E1+and+isremark%3D%270%27+and+%28isreminded+is+null+or+isreminded+%3D0%29++and+%09%09%09%09%28EXISTS+%28select+1+from+workflow_nodelink+t1+where+t1.wfrequestid+is+null+and+EXISTS+%28select+1+from+workflow_base+t2+where+t1.workflowid%3Dt2.id+and+%28t2.isvalid+%3D+%271%27+or+t2.isvalid+%3D+%273%27%29+and+%28t2.istemplate+is+null+or+t2.istemplate%3C%3E%271%27%29%29+and+%28t1.nodepasshour%3E0+or+t1.nodepassminute%3E0+or+%28t1.dateField+is+not+null+and+t1.dateField+%21%3D+%27+%27%29%29+and+workflow_currentoperator.nodeid%3Dt1.nodeid%29+or+EXISTS+%28select+1+from+workflow_nodelink+t1+where+EXISTS+%28select+1+from+workflow_base+t2+where+t1.workflowid%3Dt2.id++and+%28t2.isvalid+%3D+%271%27+or+t2.isvalid+%3D+%273%27%29+and+%28t2.istemplate+is+null+or+t2.istemplate%3C%3E%271%27%29%29+and+%28t1.nodepasshour%3E0+or+t1.nodepassminute%3E0+or+%28t1.dateField+is+not+null+and+t1.dateField+%21%3D+%27+%27%29%29+and+workflow_currentoperator.nodeid%3Dt1.nodeid+and+workflow_currentoperator.requestid%3Dt1.wfrequestid%29%29+%09and+%28%28not%28isreminded+is+not+null+and+lastRemindDa";
		System.out.println("url"+url);
		try{
		String keyWord = new URLDecoder().decode(url, "GBK");  
		}catch(Exception s){
			s.printStackTrace();
		}
	
		String nosrcZipPath="ecology";
		//38cti9kWa/4=kb+ML5MiitWTvz0k3QwTYpgfwFSwxUt8KyISoDpCA4CegXWcno/PrQ==
		// TODO Auto-generated method stub
		String code=SecurityHelper.encrypt("wEAVER@check#2019",nosrcZipPath);
		System.out.println(code);
		String old=SecurityHelper.decrypt("ecology","/c4Q2hAVXFc=h1Q5k7zWqIwfK0weDqewtzHAb1XAxKat");
		System.out.println("old=="+old);

		checkPackageCode("");
	}
	
	public static String checkPackageCode(String var1) {
       // this.updateCheckAuth("0");
        String var2 = "";
       File var3 = null;
      
        File var5 = new File("E:\\项目\\项目明细\\2022年项目\\国轩高科\\GXGK-20220524-001连接池问题处理.zip");

        String var7;
		try {
            String var6;
            if (var5.exists()) {
                var3 = var5;
                var6 = MD5Coder.getMD5BigFile(var3);
                System.out.println("wEAVER@check#2019" + var3.length()+"========="+ var6);
                var7 = SecurityHelper.encrypt("wEAVER@check#2019" + var3.length(), var6);
              System.out.println("var7=="+var7);
                if (var7 != null && var7.equals(var1)) {
                    //this.updateCheckAuth("1");
                    var2 = "success";
                }

                String var8 = var2;
                return var8;
            }

            var6 = "filerror";
            return var6;
        } catch (Exception var12) {
            var12.printStackTrace();
            var7 = "failure";
        } finally {
           

        }

        return var7;
    }

	private void updateCheckAuth(String string) {
		// TODO Auto-generated method stub
		
	}


}
