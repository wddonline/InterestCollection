package org.wdd.app.android.interestcollection.cache;

import java.security.MessageDigest;

/**
 * Created by richard on 5/13/16.
 */
class CacheUtils {

    /**
     * URL转换成缓存文件名
     * @param url
     * @return
     */
    public static String url2Filename(String url) {
        String result = CacheUtils.getMd5String(url) + ".cache";
        return result;
    }

    public static String getMd5String(String msg) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = msg.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
