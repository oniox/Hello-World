import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Utility class for generating unique keys in hexadecimal format.
 */
public class KeyGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final HexFormat hexFormat = HexFormat.of();

    /**
     * Generates a unique key in hexadecimal format (8 characters long, like "60fe4ec6").
     * @return The generated key as a String
     */
    public static String generateKey() {
        byte[] randomBytes = new byte[4]; // 4 bytes = 32 bits = 8 hex characters
        secureRandom.nextBytes(randomBytes);
        return hexFormat.formatHex(randomBytes);
    }

    /**
     * Generates a unique key in hexadecimal format with custom length.
     * @param byteLength The number of bytes to use for the key (each byte = 2 hex chars)
     * @return The generated key as a String
     * @throws IllegalArgumentException if byteLength is not positive
     */
    public static String generateKey(int byteLength) {
        if (byteLength <= 0) {
            throw new IllegalArgumentException("Byte length must be positive");
        }
        
        byte[] randomBytes = new byte[byteLength];
        secureRandom.nextBytes(randomBytes);
        return hexFormat.formatHex(randomBytes);
    }
}