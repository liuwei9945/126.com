package test;

import weaver.general.SecurityHelper;
import weaver.general.Util;
import weaver.interfaces.encode.AES;
import weaver.interfaces.encode.DES;


public class AESnew {
 public static void main(String[] args) {
     String secret="123456";
     String param="zhucf";
     AES aes = new AES();
     aes.setPwd(secret);
   String   paraaes = aes.encode(param);
     System.out.println("paraaes = " + paraaes);

     String   paraaesnew = aes.decode(paraaes);
     System.out.println("paraaesnew = " + paraaesnew);
String srt="(USD)Dak Nong Dak N'Drung Energy One Member Co., L";
     srt=   Util.toXmlText(srt);
     System.out.println("srt = " + srt);
     String old= SecurityHelper.decrypt("ecology","/c4Q2hAVXFc=h1Q5k7zWqIwfK0weDqewtzHAb1XAxKat");
     System.out.println("old=="+old);

     String jm="A342DB5DF0971A660BD064E080E0963E7405DD92982F4AA5003DE5CB76C3E8A4E1910619A13E4604D7ACE6F3C8326668C99030DE435405A0";
     String secret1 = "ecology9";
     DES des = new DES();
     des.setPwd(secret1);
     String ticket = des.decode(jm);
     System.out.println("oldjm=="+ticket);


    }
}
