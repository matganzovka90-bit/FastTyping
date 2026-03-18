package speed.fasttyping.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordHasher {
    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";
    private static final String DELIMITER = ":";
    private static final int HEX_FF = 0xff;
    private static final int HEX_100 = 0x100;
    private static final int RADIX = 16;
    private static final int EXPECTED_PARTS = 2;
    private static final int SALT_INDEX = 0;
    private static final int HASH_INDEX = 1;

    public String hash(String password) {
        byte[] salt = generateSalt();
        String saltHex = bytesToHex(salt);
        String hashHex = computeHash(password, salt);
        return saltHex + DELIMITER + hashHex;
    }

    public boolean verify(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split(DELIMITER);

        if (parts.length != EXPECTED_PARTS) {
            return false;
        }

        String saltHex = parts[SALT_INDEX];
        String expectedHash = parts[HASH_INDEX];

        byte[] salt = hexToBytes(saltHex);
        String actualHash = computeHash(password, salt);

        return slowEquals(actualHash, expectedHash);
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private String computeHash(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);

            digest.update(salt);

            byte[] passwordBytes = password.getBytes();
            digest.update(passwordBytes);

            byte[] hashBytes = digest.digest();

            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Алгоритм хешування недоступний: " + ALGORITHM, e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            int value = (b & HEX_FF) + HEX_100;
            String hex = Integer.toString(value, RADIX);
            result.append(hex.substring(1));
        }

        return result.toString();
    }

    private byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] result = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            int high = Character.digit(hex.charAt(i), RADIX) << 4;
            int low = Character.digit(hex.charAt(i + 1), RADIX);
            result[i / 2] = (byte) (high + low);
        }

        return result;
    }

    private boolean slowEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }

        return diff == 0;
    }
}
