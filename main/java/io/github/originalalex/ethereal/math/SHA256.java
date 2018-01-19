package io.github.originalalex.ethereal.math;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String hash(String value, String secret) {
        try{
            md.update((value + secret).getBytes());
            String res = bytesToHex(md.digest());
            md.reset();
            return res;
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static String hash(String value) {
        return hash(value, "");
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}
