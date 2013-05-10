package util;

import java.util.*;

public class StringUtil {
	private StringUtil() {
	}

	public static final String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
	public static final String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String letters = uppercaseLetters + lowercaseLetters;
	public static final String digits = "0123456789";

	public static String joinToString(String separator, Collection<?> list) {
		StringBuilder builder = new StringBuilder();
		String sep = "";

		for (Object i : list) {
			builder.append(sep + i);

			sep = separator;
		}

		return builder.toString();
	}

	public static String join(String separator, Collection<String> list) {
		return joinToString(separator, list);
	}

	public static String listToString(Collection<?> list) {
		return String.format("[%s]", joinToString(", ", list));
	}

	public static String byteArrayToHexString(byte[] data) {
		StringBuilder builder = new StringBuilder("0x");

		for (byte i : data) {
			builder.append(String.format("%02x", i));
		}

		return builder.toString();
	}

	public static List<Character> characters(String string) {
		List<Character> res = new ArrayList<Character>(string.length());

		for (int i = 0; i < string.length(); i += 1) {
			res.add(string.charAt(i));
		}

		return res;
	}

	public static String repeat(String string, int count) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < count; i += 1) {
			builder.append(string);
		}

		return builder.toString();
	}

	//public static String format(String format, Object... objects) {
	//	String[] values = new String[objects.length];
	//	
	//	for (int i = 0; i < objects.length; i += 1) {
	//		Object object = objects[i];
	//		String value;
	//		
	//		if (object instanceof List<?>)
	//			value = listToString()
	//		
	//		values[i] = value;
	//	}
	//}
}
