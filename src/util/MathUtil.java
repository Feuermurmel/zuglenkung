package util;

public class MathUtil {
	private MathUtil() {
	}

	public static final double tau = Math.PI * 2;

	public static int mod(int x, int divisor) {
		int rem = x % divisor;

		if (rem < 0 == divisor < 0)
			return rem;
		else
			return rem + divisor;
	}

	public static long mod(long x, long divisor) {
		long rem = x % divisor;

		if (rem < 0 == divisor < 0)
			return rem;
		else
			return rem + divisor;
	}

	public static float mod(float x, float divisor) {
		float rem = x % divisor;

		if (rem < 0 == divisor < 0)
			return rem;
		else
			return rem + divisor;
	}

	public static double mod(double x, double divisor) {
		double rem = x % divisor;

		if (rem < 0 == divisor < 0)
			return rem;
		else
			return rem + divisor;
	}

	public static int mod(int x, int divisor, int offset) {
		return mod(x - offset, divisor) + offset;
	}

	public static long mod(long x, long divisor, long offset) {
		return mod(x - offset, divisor) + offset;
	}

	public static float mod(float x, float divisor, float offset) {
		return mod(x - offset, divisor) + offset;
	}

	public static double mod(double x, double divisor, double offset) {
		return mod(x - offset, divisor) + offset;
	}

	public static int clamp(int min, int max, int x) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		else
			return x;
	}

	public static double clamp(double min, double max, double x) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		else
			return x;
	}

	public static float blend(float a, float b, float x) {
		return (1 - x) * a + x * b;
	}

	public static double blend(double a, double b, double x) {
		return (1 - x) * a + x * b;
	}
}
