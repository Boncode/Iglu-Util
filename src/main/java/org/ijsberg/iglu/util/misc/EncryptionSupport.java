package org.ijsberg.iglu.util.misc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jeroe on 01/02/2018.
 */
public class EncryptionSupport {

    public static String encryptWithMD5(String pass){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] passBytes = pass.getBytes();
            messageDigest.reset();
            byte[] digested = messageDigest.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < digested.length; i++){
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("unable to encrypt string", e);
        }
    }

    /**
     * XOR algorithm encryption / decryption
     *
     * @param data Data (ciphertext / clear text)
     * @param key secret key
     * @return Return decrypted / encrypted data
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        if (data == null || data.length == 0 || key == null || key.length == 0) {
            return data;
        }

        byte[] result = new byte[data.length];

        // Use key byte array to cycle encryption or decryption
        for (int i = 0; i < data.length; i++) {
            // Data is XOR with key, and then XOR with low 8 bits of cyclic variable (increasing complexity)
            result[i] = (byte) (data[i] ^ key[i % key.length] ^ (i & 0xFF));
        }

        return result;
    }
}
