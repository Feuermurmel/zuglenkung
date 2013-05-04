package util;

import types.Vector2d;

public class VectorUtil {
	private VectorUtil() {
	}

	public static Vector2d intersection(Vector2d position1, double angle1, Vector2d position2, double angle2) {
		Vector2d direction1 = Vector2d.fromAngle(angle1);
		Vector2d direction2 = Vector2d.fromAngle(angle2);

		double det = direction1.y * direction2.x - direction1.x * direction2.y;
		double x = -((-(direction1.y * direction2.x * position1.x) + direction1.x * direction2.x * position1.y + direction1.x * direction2.y * position2.x - direction1.x * direction2.x * position2.y)/ det);
		double y = (direction1.y * direction2.y * position1.x - direction1.x * direction2.y * position1.y - direction1.y * direction2.y * position2.x +	direction1.y * direction2.x * position2.y) / det;

		return Vector2d.create(x, y);
	}
}
