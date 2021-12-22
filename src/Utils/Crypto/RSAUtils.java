package Utils.Crypto;

import MVC.Model.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Asymmetric key generator, encryption and decryption library
 *
 * Based on https://www.devglan.com/java8/rsa-encryption-decryption-java
 */

public class RSAUtils {

    private static final int KEY_SIZE = 2048;
    private static final String KEY_ALG = "RSA";
    private static final String OUTPUT_FILE = "Keys/_usr";
    private static final String SERVER_FILE = "Keys/srv";
    private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * Generates an asymmetric key pair which are stored in base64 in the Key folder
     *
     * @param user
     *
     */

    public static void generateKeys(User user) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALG);
        kpg.initialize(KEY_SIZE);
        KeyPair kp = kpg.generateKeyPair();
        Key pub = kp.getPublic();
        Key pvt = kp.getPrivate();
        Base64.Encoder encoder = Base64.getEncoder();

        String fileName = user.getUsername() + OUTPUT_FILE + ".key";
        File outputFile = new File(fileName);
        outputFile.createNewFile();
        Writer out = new FileWriter(outputFile);
        out.write("-----BEGIN RSA PRIVATE KEY-----\n");
        out.write(encoder.encodeToString(pvt.getEncoded()));
        out.write("\n-----END RSA PRIVATE KEY-----\n");
        out.close();

        fileName = user.getUsername() + OUTPUT_FILE + ".pub";
        outputFile = new File(fileName);
        outputFile.createNewFile();
        out = new FileWriter(outputFile);
        out.write("-----BEGIN RSA PUBLIC KEY-----\n");
        out.write(encoder.encodeToString(pub.getEncoded()));
        out.write("\n-----END RSA PUBLIC KEY-----\n");
        out.close();

    }

    public static PublicKey getPublicKeyServer(){
        PublicKey publicKey = null;
        String fileName = "";
        fileName = SERVER_FILE + ".pub";
        byte[] key = null;
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            br.readLine();
            key = decoder.decode(br.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALG);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PublicKey getPublicKeyUser(User user){
        PublicKey publicKey = null;
        String fileName = "";
        fileName = user.getUsername() + OUTPUT_FILE + ".pub";
        byte[] key = null;
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            br.readLine();
            key = decoder.decode(br.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALG);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKeyServer(){
        PrivateKey privateKey = null;
        String fileName = "";
        fileName = SERVER_FILE + ".key";

        byte[] key = null;
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            br.readLine();
            key = decoder.decode(br.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALG);
            privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static PrivateKey getPrivateKeyUser(User user){
        PrivateKey privateKey = null;
        String fileName = "";
        fileName = user.getUsername() + OUTPUT_FILE + ".key";

        byte[] key = null;
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            br.readLine();
            key = decoder.decode(br.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALG);
            privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }


    public static String encrypt(String data, PublicKey publicKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }


    public static String decrypt(String data, PrivateKey privateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), privateKey);
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public static PublicKey loadPbK(String pbk) {
        PublicKey publicKey = null;
        byte[] key;
        Base64.Decoder decoder = Base64.getDecoder();
        key = decoder.decode(pbk);
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALG);
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

}
