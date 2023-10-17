package test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * DES加密 解密算法
 *
 * @author HUZHIPAN
 * @date 2018-1-20 10:34:25
 */
public class IpiDes {

    private final static String DES = "DES";
    private final static String ENCODE = "UTF-8";
    private final static String defaultKey = "ipimobp@";

    public static void main(String[] args) throws Exception {
        String data = "{\"userName\":\"MobileOAAdmin_93241\",\"userPwd\":\"cgkj!93241\",\"destAddr\":\"18755166955\",\"content\":\"测试短信\",\"srcAddr\":\"93241\",\"exteCode\":\"999\",\"clientType\":\"11\"}";
        System.err.println(encrypt(data, defaultKey));
        System.err.println(decrypt(encrypt(data, defaultKey), defaultKey));
        System.out.println("加密：" +encrypt(data));
        String jm="ELlhlHbFY0WCftGhOPgKkCxOuevkiH16xvhoG4gORRAM0EIh6VPplEFOdyqahw7RX3waGYjBmwTpakSWacGSg4VqPWV8+clr";
        System.out.println("解密："+decrypt(encrypt(data)));
       // System.out.println("解密jm："+decrypt(jm));
        doGet(encrypt(data));


    }

    /** *//**
     * 对于主动请求其它接口的参数流写入(POST方式)
     */
    public static void doGet(String parms)
            throws ServletException, IOException
    {
        System.out.println("begin send:"+parms);
       // parms= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><page><username>爱心天使</usernaem><age>26</age></page>";

        URL url = null;
        HttpURLConnection httpConn = null;
        OutputStream output = null;
        OutputStreamWriter outr = null;

        url = new URL("http://112.30.201.87:8085/PlatSmsSDKServer/submitSM");
        httpConn = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setFollowRedirects(true);
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "application/json;charset:utf-8;");
        httpConn.connect();
        output = httpConn.getOutputStream();
        outr = new OutputStreamWriter(output);
        // 写入请求参数
        outr.write(parms.toString().toCharArray(), 0, parms
                .toString().length());
        outr.flush();
        outr.close();
        System.out.println("send ok");
        int code = httpConn.getResponseCode();
        System.out.println("code " + code);
        System.out.println(httpConn.getResponseMessage());

        //读取响应内容
        String sCurrentLine = "";
        String sTotalString = "";
        if (code == 200)
        {
            java.io.InputStream is = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            while ((sCurrentLine = reader.readLine()) != null)
                if (sCurrentLine.length() > 0)
                    sTotalString = sTotalString + sCurrentLine.trim();
        } else
        {
            sTotalString = "远程服务器连接失败,错误代码:" + code;

        }
        System.out.println("response:" + sTotalString);

    }

    public void doPost(String jm)
            throws ServletException, IOException
    {
        this.doGet(jm);
    }

    private void sendGet( String jm) throws Exception {

        String url = "http://112.30.201.87:8085/PlatSmsSDKServer/submitSM";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");

       // con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
    }

    /**
     * 使用 默认key 加密
     */
    public static String encrypt(String data) throws Exception {
        byte[] bt = encrypt(data.getBytes(ENCODE), defaultKey.getBytes(ENCODE));
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * 使用 默认key 解密
     */
    public static String decrypt(String data) throws IOException, Exception {
        if (data == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = decrypt(buf, defaultKey.getBytes(ENCODE));
        return new String(bt, ENCODE);
    }

    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key
     *            加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {
        byte[] bt = encrypt(data.getBytes(ENCODE), key.getBytes(ENCODE));
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key
     *            加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws IOException,
            Exception {
        if (data == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = decrypt(buf, key.getBytes(ENCODE));
        return new String(bt, ENCODE);
    }

    /**
     * Description 根据键值进行加密
     *
     * @param data
     * @param key
     *            加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    /**
     * Description 根据键值进行解密
     *
     * @param data
     * @param key
     *            加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }
}