package streckenplan.interfaces;

public final class Aspect {
	public final double distance;
	public final double speed;

	private Aspect(double distance, double speed) {
		this.distance = distance;
		this.speed = speed;
	}

	public static Aspect create(double distance, double speed) {
		return new Aspect(distance, speed);
	}
}
