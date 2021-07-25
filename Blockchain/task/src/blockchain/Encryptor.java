package blockchain;

import javax.crypto.Cipher;
import java.security.*;

public class Encryptor {

    public Encryptor(){
    }

    public static KeyPair generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom();
        keyGen.initialize(2048, secureRandom);
        return keyGen.generateKeyPair();
    }

    public static byte[] encryptMessage(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static String decryptMessage(byte [] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return new String(cipher.doFinal(data));

    }

    public static boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }


    public static byte[] singMessage(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }
}
