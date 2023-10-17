package test;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import weaver.general.TimeUtil;
import weaver.general.Util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import static jxl.biff.FormatRecord.logger;

public class SMSSEND {
     static String CHAR_SET = "UTF-8";
    private final static String DES = "DES";
    private final static String ENCODE = "UTF-8";
    private final static String defaultKey = "cjchnws9";//陕西现网用的这个20220507

    private String host = null; // host

    private int port = 0; // port

    private static HttpClient httpClient = new HttpClient(); // HttpClient对象

    private static Log logger = LogFactory.getLog("SMSSEND");

    private boolean isActive = true;// 链接状态

    public final static String POST_REQUEST = "POST";
    public final static String GET_REQUEST = "GET";
    private static String cookie = null;
    private MultiThreadedHttpConnectionManager connectionManager = null;



    public void init() {
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.setMaxTotalConnections(5);
        connectionManager.setMaxConnectionsPerHost(5);
        // 把连接管理放置到httpClient
        httpClient.setConnectionTimeout(30 * 1000);
        httpClient.setTimeout(30 * 1000);
        httpClient.setHttpConnectionManager(connectionManager);
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(0, false));// 失败自动重试三次
    }

    public static void main(String[] args) {
       // PhoneContent p = new PhoneContent();
        List<String> phones = new ArrayList<String>();
        phones.add("18755166955");
        String content = "自定义这是二次接口批量发送的短信,测试短信功能";
        String userName = "MobileOAAdmin_93241";
        String password = "cgkj!932413";
        String srcaddr = "93241";
        String extcode = "111";
        String serverUrl = "http://112.30.201.87:8085/PlatSmsSDKServer/submitSM";
        Map<String,Object>   ret = sendSms(serverUrl, phones, content, userName, password, srcaddr, extcode,"ipimbop@");
        System.out
                .println("号码批发送完成,发送结果：" + ret.get("status") + "，结果描述：" + ret.get("desc") + "，请求ID：" + ret.get("userreqid"));
    }

    /**
     * 发送短信 号码批发方式
     *
     * @param serverUrl
     *            服务器地址
     * @param phones
     *            号码对象
     * @param content
     *            内容对象
     * @param userName
     *            用户名
     * @param password
     *            密码
     * @param srcaddr
     *            企业接入码
     * @param extcode
     *            扩展码（长度为3位，可不填）
     * @return
     */
    public static Map<String,Object> sendSms(String serverUrl, List<String> phones, String content, String userName,
                                  String password, String srcaddr, String extcode,String deskey) {

        StringBuffer sb = new StringBuffer();
        for (String phone : phones) {
            sb.append(phone);
            sb.append(",");
        }
        Map<String,Object>  ret = new HashMap<String,Object> ();
        JSONObject js = new JSONObject();
        js.put("userName", userName);
        js.put("userPwd", password);
        js.put("destAddr", sb.toString().substring(0, sb.length() - 1));
        js.put("content", content);
        js.put("srcAddr", srcaddr);
        js.put("exteCode", extcode);
        js.put("clientType", "11");
        js.put("sdkType","11");
        try {
            byte[] resdata = post(serverUrl,
                    encrypt(js.toJSONString(),deskey).getBytes(CHAR_SET));
            ret= praseData(decrypt(new String(resdata, CHAR_SET),deskey));
            return ret;
        } catch (Exception e) {
            logger.error("短信号码批发送异常", e);
        }
        return ret;
    }
    public static Map<String,Object>  praseData(String retStr) throws UnsupportedEncodingException {
        JSONObject jo = JSONObject.parseObject(retStr);
        Map<String,Object>  ret = new HashMap<String,Object> ();
        ret.put("status" ,jo.getIntValue("status"));
        ret.put("userreqid" , jo.getLongValue("userreqid"));
       ret.put("desc" , jo.getString("desc"));

       return  ret ;
    }

    /**
     * 阻塞式发送请求,并返回响应,注:如果异常,则返回null <br>
     *
     * @param requestURL
     *            请求
     * @return byte[] 响应
     * @throws Exception
     */
    public static byte[] post(String requestURL, byte[] data) throws Exception {
        logger.debug("请求URL：" + requestURL + ",DATA:" + new String(data, "UTF-8"));
        byte[] retByte = null;
        PostMethod postMethod = null;
        String tmpcookies = cookie;
        try {
            if (requestURL.startsWith("https")) {
                httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
                Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
                Protocol.registerProtocol("https", myhttps);
            }
            postMethod = new PostMethod(requestURL);
            RequestEntity stream = new ByteArrayRequestEntity(data);
            postMethod.setRequestEntity(stream);
            if (postMethod.getRequestEntity() != null)
                postMethod.setRequestHeader("Content-Length", "" + postMethod.getRequestEntity().getContentLength());
            postMethod.setRequestHeader("Connection", "Keep-Alive");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = null;
            int statusCode = httpClient.executeMethod(postMethod);
            if (statusCode != 200) {
                logger.error("发送消息" + requestURL + "时响应失败,值:" + statusCode);
                return null;
            }

            Cookie[] cookies = httpClient.getState().getCookies();
            tmpcookies = "";
            for (Cookie c : cookies) {
                tmpcookies += c.toString() + ";";
            }
            cookie = tmpcookies;
            in = postMethod.getResponseBodyAsStream();
            int b;
            while ((b = in.read()) != -1) {
                out.write((byte) b);
            }
            retByte = out.toByteArray();
            if (retByte != null)
                logger.debug("到服务端主动请求，返回：" + new String(retByte, "UTF-8"));
            in.close();
        } catch (Exception e) {
            throw e;
        } finally {
            // 释放链接
            if (postMethod != null) {
                postMethod.releaseConnection();
            }

        }
        return retByte;

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
class MySSLProtocolSocketFactory implements ProtocolSocketFactory {

    private SSLContext sslcontext = null;

    private SSLContext createSSLContext() {
        SSLContext sslcontext=null;
        try {
            sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, new TrustManager[]{new MySSLProtocolSocketFactory.TrustAnyTrustManager()}, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslcontext;
    }

    private SSLContext getSSLContext() {
        if (this.sslcontext == null) {
            this.sslcontext = createSSLContext();
        }
        return this.sslcontext;
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(
                socket,
                host,
                port,
                autoClose
        );
    }

    public Socket createSocket(String host, int port) throws IOException,
            UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(
                host,
                port
        );
    }


    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
            throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }

    public Socket createSocket(String host, int port, InetAddress localAddress,
                               int localPort, HttpConnectionParams params) throws IOException,
            UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory = getSSLContext().getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, localAddress, localPort);
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }

    //自定义私有类
    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }


}
