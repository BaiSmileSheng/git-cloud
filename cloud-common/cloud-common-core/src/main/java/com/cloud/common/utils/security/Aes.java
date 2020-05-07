package com.cloud.common.utils.security;

import cn.hutool.crypto.SecureUtil;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.Inet4Address;
import java.net.InetAddress;


/**
 * @author: Jeffrey Deng
 * @description:
 * @date:created in 2017/10/9 12:12
 * @modificed by:
 */
public class Aes {

    /**
     * 使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同
     */
    private static String KEY = "uuC201807-17Cent";

    private static String IV = "UuC20180717Cente";

    /**
     * 加密方法
     *
     * @param data 要加密的数据
     * @param key  加密key
     * @param iv   加密iv
     * @return 加密的结果
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        try {
            //"算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return new Base64().encodeToString(encrypted);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 使用默认的key和iv加密
     */
    public static String encrypt(String data) throws Exception {
        return encrypt(data, KEY, IV);
    }
}
