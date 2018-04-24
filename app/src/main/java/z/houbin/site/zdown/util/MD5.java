package z.houbin.site.zdown.util;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 encrypt algorithm. <br>
 * Encryption irreversible.
 *
 * @author Tony_tian
 * @version 0.0.1 <br> 2015-10-15 12:57:12
 */
public final class MD5 {

    /**
     * Determine encrypt algorithm MD5
     */
    private static final String ALGORITHM_MD5 = "MD5";
    /**
     * UTF-8 Encoding
     */
    private static final String UTF_8 = "UTF-8";

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * MD5 16bit Encrypt Methods.
     *
     * @param readyEncryptStr ready encrypt string
     * @return String encrypt result string
     * @throws NoSuchAlgorithmException
     */
    public static final String MD5_16bit(String readyEncryptStr) throws NoSuchAlgorithmException {
        if (readyEncryptStr != null) {
            return MD5.MD5_32bit(readyEncryptStr).substring(8, 24);
        } else {
            return null;
        }
    }

    /**
     * MD5 32bit Encrypt Methods.
     *
     * @param readyEncryptStr ready encrypt string
     * @return String encrypt result string
     * @throws NoSuchAlgorithmException
     */
    public static final String MD5_32bit(String readyEncryptStr) {
        if (readyEncryptStr != null) {
//Get MD5 digest algorithm's MessageDigest's instance.
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance(ALGORITHM_MD5);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
//Use specified byte update digest.
            md.update(readyEncryptStr.getBytes());
//Get cipher text
            byte[] b = md.digest();
//The cipher text converted to hexadecimal string
            StringBuilder su = new StringBuilder();
//byte array switch hexadecimal number.
            for (int offset = 0, bLen = b.length; offset < bLen; offset++) {
                String haxHex = Integer.toHexString(b[offset] & 0xFF);
                if (haxHex.length() < 2) {
                    su.append("0");
                }
                su.append(haxHex);
            }
            return su.toString();
        } else {
            return null;
        }
    }

    /**
     * MD5 32bit Encrypt Methods.
     *
     * @param readyEncryptStr ready encrypt string
     * @return String encrypt result string
     * @throws NoSuchAlgorithmException
     */
    public static final String MD5_32bit1(String readyEncryptStr) throws NoSuchAlgorithmException {
        if (readyEncryptStr != null) {
//The cipher text converted to hexadecimal string
            StringBuilder su = new StringBuilder();
//Get MD5 digest algorithm's MessageDigest's instance.
            MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
            byte[] b = md.digest(readyEncryptStr.getBytes());
            int temp = 0;
//byte array switch hexadecimal number.
            for (int offset = 0, bLen = b.length; offset < bLen; offset++) {
                temp = b[offset];
                if (temp < 0) {
                    temp += 256;
                }
                int d1 = temp / 16;
                int d2 = temp % 16;
                su.append(Integer.toHexString(d1) + Integer.toHexString(d2));
            }
            return su.toString();
        } else {
            return null;
        }
    }

    /**
     * MD5 32bit Encrypt Methods.
     *
     * @param readyEncryptStr ready encrypt string
     * @return String encrypt result string
     * @throws NoSuchAlgorithmException
     */
    public static final String MD5_32bit2(String readyEncryptStr) throws NoSuchAlgorithmException {
        if (readyEncryptStr != null) {
//The cipher text converted to hexadecimal string
            StringBuilder su = new StringBuilder();
//Get MD5 digest algorithm's MessageDigest's instance.
            MessageDigest md = MessageDigest.getInstance(ALGORITHM_MD5);
//Use specified byte update digest.
            md.update(readyEncryptStr.getBytes());
            byte[] b = md.digest();
            int temp = 0;
//byte array switch hexadecimal number.
            for (int offset = 0, bLen = b.length; offset < bLen; offset++) {
                temp = b[offset];
                if (temp < 0) {
                    temp += 256;
                }
                if (temp < 16) {
                    su.append("0");
                }
                su.append(Integer.toHexString(temp));
            }
            return su.toString();
        } else {
            return null;
        }
    }

    /**
     * MD5 16bit Encrypt Methods.
     *
     * @param readyEncryptStr ready encrypt string
     * @return String encrypt result string
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static final String MD5_64bit(String readyEncryptStr) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(ALGORITHM_MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            return new String(Base64.encode(md.digest(readyEncryptStr.getBytes(UTF_8)), Base64.DEFAULT));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        try {
            String md516 = MD5.MD5_16bit("kaka123");
            System.out.println("16bit-md5:\n" + md516); //
            String md532 = MD5.MD5_32bit("kaka123");
            String md5321 = MD5.MD5_32bit1("kaka123");
            String md5322 = MD5.MD5_32bit2("kaka123");
            System.out.println("32bit-md5:"); //5d052f1e32af4e4ac2544a5fc2a9b992
            System.out.println("1:  " + md532);
            System.out.println("2:  " + md5321);
            System.out.println("3:  " + md5322);
            String md564 = MD5.MD5_64bit("kaka123");
            System.out.println("64bit-md5:\n" + md564); //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}