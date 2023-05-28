package com.fiseq.truckcompany.utilities;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class KeyGenerationUtil {
    private static final List<Integer> VALID_ID_CHARS = new ArrayList<>();
    private static final List<Integer> VALID_ID_NUMS = new ArrayList<>();

    private static final SecureRandom secureRandom = new SecureRandom();

    private KeyGenerationUtil() {
        throw new IllegalStateException("Utility class");
    }

    static {
        IntStream.rangeClosed('0', '9').forEach(VALID_ID_CHARS::add); // 0-9
        IntStream.rangeClosed('A', 'Z').forEach(VALID_ID_CHARS::add); // A-Z
        IntStream.rangeClosed('a', 'z').forEach(VALID_ID_CHARS::add); // a-z
    }
    static {
        IntStream.rangeClosed('0', '9').forEach(VALID_ID_NUMS::add); // 0-9
    }

    public static String generateUniqueIdentifier() {
        return generateUniqueIdentifier(9);
    }
    public static String generateUniqueIdentifier(int length) {
        StringBuilder sb = new StringBuilder();
        secureRandom.ints(length,0, VALID_ID_CHARS.size()).map(VALID_ID_CHARS::get)
                .forEach(s -> sb.append((char) s));
        return sb.toString();

    }
}
