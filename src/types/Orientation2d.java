package types;

public final class Orientation2d {
	public final double x;
	public final double y;
	public final double angle;

	private Orientation2d(double x, double y, double angle) {
		this.x = x;
		this.y = y;
		this.angle = angle;
	}

	public Vector2d position() {
		return Vector2d.create(x, y);
	}

	public Orientation2d move(Vector2d offset) {
		return create(position().add(offset), angle);
	}

	public static Orientation2d create(double x, double y, double angle) {
		return new Orientation2d(x, y, angle);
	}

	public static Orientation2d create(Vector2d position, double angle) {
		return create(position.x, position.y, angle);
	}

	public static Orientation2d atOrigin(double angle) {
		return create(Vector2d.zero, angle);
	}

	@Override
	public String toString() {
		return String.format("Orientation2d(x = %s, y = %s, angle = %s)", x, y, angle);
	}

	public Orientation2d rotate(double angle) {
		return create(x, y, this.angle + angle);
	}
}
