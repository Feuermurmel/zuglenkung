package util;

import java.util.*;

public class RandomUtil {
	private RandomUtil() {}

	private static final Random random = new Random();
	
	public static <T> T randomElement(T[] array) {
		return randomElement(Arrays.asList(array));
	}

	private static <T> T randomElement(List<T> list) {
		return list.get(random.nextInt(list.size()));
	}
}
