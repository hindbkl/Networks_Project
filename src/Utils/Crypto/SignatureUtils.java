package Utils.Crypto;

import MVC.Model.User;

import java.security.*;
import java.util.Base64;

/**
 *
 * Digital signature library. Signs data with an RSA private key.
 *
 */


public class SignatureUtils {
    private static final String ALGORITHM= "SHA1withRSA";

    public static String signServer(String data) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.initSign(RSAUtils.getPrivateKeyServer());
        signature.update(data.getBytes());
        byte[] signatureData = signature.sign();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(signatureData);
    }

    public static String signUser(String data, User user) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.initSign(RSAUtils.getPrivateKeyUser(user));
        signature.update(data.getBytes());
        byte[] signatureData = signature.sign();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(signatureData);
    }

    public static boolean verify(String data, String signature, PublicKey pbk) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature verifier = Signature.getInstance(ALGORITHM);
        verifier.initVerify(pbk);
        verifier.update(data.getBytes());
        Base64.Decoder decoder = Base64.getDecoder();
        return verifier.verify(decoder.decode(signature));
    }
}
