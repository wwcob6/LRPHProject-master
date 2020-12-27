package com.punuo.sys.app.sercet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by chenblue23 on 2016/4/20.
 *
 */
public class SHA1 {

    private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static SHA1 sha1 = null;
    private MessageDigest md = null;

    private SHA1() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("SHA1");
    }

    public static SHA1 getInstance() {
        if (sha1 == null) {
            try {
                sha1 = new SHA1();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return sha1;
    }

    public String hashData(byte[] bytes) {
        md.update(bytes, 0, bytes.length);
        return bytesToHex(md.digest());
    }

    public String hashData(String str) {
        byte[] bytes = str.getBytes();
        return hashData(bytes);
    }

    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int i = 0; i < bytes.length; i++) {
            v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_CHARS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }

}
