package streckenplan.interfaces;

import util.MathUtil;

public enum Direction {
	zero(1, 0), one(0, 1), two(-1, 1), three(-1, 0), four(0, -1), fife(1, -1);

	public final int offsetx;
	public final int offsety;
	public final double angle;

	Direction(int offsetx, int offsety) {
		this.offsetx = offsetx;
		this.offsety = offsety;
		angle = turnAngle(ordinal());
	}

	public Direction turn(int steps) {
		return values()[MathUtil.mod(ordinal() + steps, 6)];
	}
	
	public Direction reverse() {
		return turn(3);
	}
	
	public int diff(Direction other) {
		return MathUtil.mod(ordinal() - other.ordinal(), 6);
	}
	
	/** Returns the angle that is in the middle between start and end, inside the range from start to end. */
	public static double turnRangeMidAngle(Direction start, Direction end) {
		return turnAngle(MathUtil.mod(start.ordinal() + MathUtil.mod(end.ordinal(), 6, start.ordinal()), 12)) / 2;
	}

	private static double turnAngle(int turns) {
		return turns * Math.PI / 3;
	}
}
