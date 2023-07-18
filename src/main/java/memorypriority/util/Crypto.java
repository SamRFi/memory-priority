package memorypriority.util;

import org.jasypt.util.text.StrongTextEncryptor;

public class Crypto {
    private static final Crypto INSTANCE = new Crypto();
    private static final String KEY = "SECRET";
    private final StrongTextEncryptor encryptor;

    public static Crypto getInstance() {
        return INSTANCE;
    }

    private Crypto() {
        encryptor = new StrongTextEncryptor();
        encryptor.setPassword(KEY);
    }

    public String encrypt(String in) {
        return encryptor.encrypt(in);
    }

    public String decrypt(String decryptedMessage) {
        return encryptor.decrypt(decryptedMessage);
    }

}