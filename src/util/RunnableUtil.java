package util;

public class RunnableUtil {
	private RunnableUtil() {
	}

	public static final Runnable emptyRunnable = new Runnable() {
		@Override
		public void run() {
		}
	};
}
