package util;

import java.util.List;

public class StringUtil {
	private StringUtil() {
	}

	public static String join(List<?> list, String separator) {
		StringBuilder builder = new StringBuilder();
		String sep = "";

		for (Object i : list) {
			builder.append(sep + i);

			sep = separator;
		}

		return builder.toString();
	}
}
