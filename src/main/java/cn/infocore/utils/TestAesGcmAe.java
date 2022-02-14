package cn.infocore.utils;


/**
 * @ProjectName: trytry
 * @Package: AboutString
 * @ClassName: TestAesGcmAe
 * @Author: aren904
 * @Description:
 * @Date: 2021/7/8 10:45
 * @Version: 1.0
 */
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * A simple showcase for encryption and decryption with AES + GCM in Java
 */
public class TestAesGcmAe {
    private final SecureRandom secureRandom = new SecureRandom();
    private final static int GCM_IV_LENGTH = 12;

    public static void main(String args[]){
        System.out.println("\nstart\n");
        TestAesGcmAe instance = new TestAesGcmAe();
        try {
            instance.testEncryption();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\nexit\n");
    }

    /* s must be an even-length string. */
    // https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public void testEncryption() throws Exception {
        //create new random key
        byte[] key = hexStringToByteArray("99eca6ea354344b7856444ec87340370");
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        //byte[] associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8); //meta data you want to verify with the secret message

        String message = "abcdefgh12345678";

        //byte[] cipherText = encrypt(message, secretKey, associatedData);
        //String decrypted = decrypt(cipherText, secretKey, associatedData);

        System.out.println("--------------------------------------------------------------------------------");
        byte[] cipherText = encrypt(message, secretKey, null);
        System.out.println("--------------------------------------------------------------------------------");
        String decrypted = decrypt(cipherText, secretKey, null);
        System.out.println("--------------------------------------------------------------------------------");
    }

    /**
     * Encrypt a plaintext with given key.
     *
     * @param plaintext      to encrypt (utf-8 encoding will be used)
     * @param secretKey      to encrypt, must be AES type, see {@link SecretKeySpec}
     * @param associatedData optional, additional (public) data to verify on decryption with GCM auth tag
     * @return encrypted message
     * @throws Exception if anything goes wrong
     */
    private byte[] encrypt(String plaintext, SecretKey secretKey, byte[] associatedData) throws Exception {
        // byte[] iv = new byte[GCM_IV_LENGTH]; //NEVER REUSE THIS IV WITH SAME KEY
        byte[] iv = hexStringToByteArray("6dd49a0a8be34519bcc1a9a0");
        int tag_size_bytes = 16;

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(tag_size_bytes * 8, iv); //128 bit auth tag length
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }

        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        System.out.println("           key: " + bytesToHex(secretKey.getEncoded()));
        System.out.println("      key size: " + secretKey.getEncoded().length + " bytes");
        System.out.println("            iv: " + bytesToHex(iv));
        System.out.println("       iv size: " + iv.length + " bytes");
        System.out.println("      tag size: " + tag_size_bytes);
        System.out.println("         plain: " + plaintext);
        System.out.println("    plain size: " + plaintext.length() + " bytes");
        System.out.println("        cipher: " + bytesToHex(byteBuffer.array()));
        System.out.println("   cipher size: " + byteBuffer.array().length + " bytes");

        return byteBuffer.array();
    }

    /**
     * Decrypts encrypted message (see {@link #encrypt(String, SecretKey, byte[])}).
     *
     * @param cipherMessage  iv with ciphertext
     * @param secretKey      used to decrypt
     * @param associatedData optional, additional (public) data to verify on decryption with GCM auth tag
     * @return original plaintext
     * @throws Exception if anything goes wrong
     */
    public static String decrypt(byte[] cipherMessage, SecretKey secretKey, byte[] associatedData) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        //use first 12 bytes for iv
        AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, cipherMessage, 0, GCM_IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmIv);

        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }
        //use everything from 12 bytes on as ciphertext
        byte[] plainText = cipher.doFinal(cipherMessage, GCM_IV_LENGTH, cipherMessage.length - GCM_IV_LENGTH);
        String plain_text_str = new String(plainText, StandardCharsets.UTF_8);

        System.out.println("           key: " + bytesToHex(secretKey.getEncoded()));
        System.out.println("      key size: " + secretKey.getEncoded().length + " bytes");
        System.out.println("        rplain: " + plain_text_str);
        System.out.println("   rplain size: " + plain_text_str.length() + " bytes");

        return plain_text_str;
    }
}

