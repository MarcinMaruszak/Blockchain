package blockchain;

import java.security.PublicKey;

public class Transaction {
    private long id;
    private byte[] data;
    private byte [] signature;
    private PublicKey publicKey;


    public Transaction(byte[] data, byte[] signature, PublicKey publicKey, long id) {
        this.id = id;
        this.data = data;
        this.signature = signature;
        this.publicKey = publicKey;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }


}
