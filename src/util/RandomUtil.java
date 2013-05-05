package util;

import java.util.*;

public class RandomUtil {
	private RandomUtil() {
	}

	public static <T> List<T> sample(List<T> population, int size) {
		List<T> res = new ArrayList<T>(size);

		for (int i = 0; i < size; i += 1)
			res.add(choice(population));

		return res;
	}
	
	public static <T> T choice(List<T> population) {
		return population.get(random.nextInt(population.size()));
	}

	public static String sample(String population, int size) {
		return StringUtil.joinToString("", sample(StringUtil.characters(population), size));
	}

	private static final Random random = new Random();
}
