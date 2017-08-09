package 双向认证;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by linSir
 * date at 2017/8/8.
 * describe:base32的处理类
 */

public class Base32String {
	private static final Base32String INSTANCE = new Base32String("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"); // RFC
																										// 4648/3548

	static Base32String getInstance() {
		return INSTANCE;
	}

	private String ALPHABET;
	private char[] DIGITS;
	private int MASK;
	private int SHIFT;
	private HashMap<Character, Integer> CHAR_MAP;

	static final String SEPARATOR = "-";

	protected Base32String(String alphabet) {
		this.ALPHABET = alphabet;
		DIGITS = ALPHABET.toCharArray();
		MASK = DIGITS.length - 1;
		SHIFT = Integer.numberOfTrailingZeros(DIGITS.length);
		CHAR_MAP = new HashMap<Character, Integer>();
		for (int i = 0; i < DIGITS.length; i++) {
			CHAR_MAP.put(DIGITS[i], i);
		}
	}

	public static byte[] decode(String encoded) throws DecodingException {
		return getInstance().decodeInternal(encoded);
	}

	protected byte[] decodeInternal(String encoded) throws DecodingException {
		encoded = encoded.trim().replaceAll(SEPARATOR, "").replaceAll(" ", "");
		encoded = encoded.replaceFirst("[=]*$", "");
		encoded = encoded.toUpperCase(Locale.US);
		if (encoded.length() == 0) {
			return new byte[0];
		}
		int encodedLength = encoded.length();
		int outLength = encodedLength * SHIFT / 8;
		byte[] result = new byte[outLength];
		int buffer = 0;
		int next = 0;
		int bitsLeft = 0;
		for (char c : encoded.toCharArray()) {
			if (!CHAR_MAP.containsKey(c)) {
				throw new DecodingException("Illegal character: " + c);
			}
			buffer <<= SHIFT;
			buffer |= CHAR_MAP.get(c) & MASK;
			bitsLeft += SHIFT;
			if (bitsLeft >= 8) {
				result[next++] = (byte) (buffer >> (bitsLeft - 8));
				bitsLeft -= 8;
			}
		}
		return result;
	}

	public static class DecodingException extends Exception {
		public DecodingException(String message) {
			super(message);
		}
	}

}
