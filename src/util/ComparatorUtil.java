package util;

import java.util.Comparator;

public class ComparatorUtil {
	private ComparatorUtil() {
	}

	public static int compare(long x, long y) {
		if (x < y)
			return -1;
		else if (x > y)
			return 1;
		else
			return 0;
	}

	public static int compare(int x, int y) {
		if (x < y)
			return -1;
		else if (x > y)
			return 1;
		else
			return 0;
	}

	// Comparator that extracts a key from values and compares these keys instead
	public abstract static class KeyComparator<T, K extends Comparable<K>> implements Comparator<T> {
		private final Comparator<K> keyComparator;

		protected KeyComparator(Comparator<K> keyComparator) {
			this.keyComparator = keyComparator;
		}

		protected KeyComparator() {
			this(new Comparator<K>() {
				@Override
				public int compare(K o1, K o2) {
					return o1.compareTo(o2);
				}
			});
		}

		public abstract K getKey(T value);

		@Override
		public final int compare(T o1, T o2) {
			return keyComparator.compare(getKey(o1), getKey(o2));
		}
	}
}
