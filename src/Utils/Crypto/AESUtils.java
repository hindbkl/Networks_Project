package Utils.Crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Symmetric key generator, encryption and decryption library
 *
 * based on https://www.baeldung.com/java-aes-encryption-decryption
 */

public class AESUtils {
    private static final String ENC_ALG = "AES";
    private static final int KEY_SIZE = 256;
    private static final String ALG_PARA = "AES/CBC/PKCS5PADDING";

    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(ENC_ALG);
        kg.init(KEY_SIZE);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(kg.generateKey().getEncoded());
    }

    public static String generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(iv);
    }

    public static String decryption(String cipherText, String sk, String iV) throws Exception {
        SecretKey secretKey = decodeKey(sk);
        Cipher cipher = Cipher.getInstance(ALG_PARA);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iV));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] result = cipher.doFinal(decoder.decode(cipherText));

        return new String(result);
    }

    private static SecretKey decodeKey(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ENC_ALG);
    }

    public static String encryption(String plainText, String sk, String iV) throws Exception {
        SecretKey secretKey = decodeKey(sk);
        Cipher cipher = Cipher.getInstance(ALG_PARA);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iV));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(cipher.doFinal(plainText.getBytes()));
    }

}
