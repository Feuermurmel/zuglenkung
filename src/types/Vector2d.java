package types;

public final class Vector2d {
	public final double x;
	public final double y;

	private Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public double angle() {
		return Math.atan2(y, x);
	}

	public Vector2d scale(double factor) {
		return create(x * factor, y * factor);
	}

	public Vector2d rotate(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		return create(x * cos - y * sin, y * cos + x * sin);
	}

	public double dot(Vector2d vec) {
		return x * vec.x + y * vec.y;
	}

	public Vector2d add(Vector2d vec) {
		return create(x + vec.x, y + vec.y);
	}

	public Vector2d sub(Vector2d vec) {
		return create(x - vec.x, y - vec.y);
	}
	
	public static final Vector2d zero = create(0, 0);
	public static final Vector2d unitX = create(1, 0);
	public static final Vector2d unitY = create(0, 1);

	public static Vector2d create(double x, double y) {
		return new Vector2d(x, y);
	}

	public static Vector2d fromAngle(double angle) {
		return create(Math.cos(angle), Math.sin(angle));
	}

	public static Vector2d fromAngle(double angle, double length) {
		return create(Math.cos(angle) * length, Math.sin(angle) * length);
	}

	@Override
	public String toString() {
		return String.format("Vector2d(x = %s, y = %s)", x, y);
	}
}
