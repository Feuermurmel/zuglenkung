package streckenplan.api;

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

	public int minus(Direction other) {
		return MathUtil.mod(ordinal() - other.ordinal(), 6);
	}

	private static double turnAngle(int turns) {
		return turns * MathUtil.tau / 6;
	}
}
