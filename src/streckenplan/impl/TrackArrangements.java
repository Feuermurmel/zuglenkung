package streckenplan.impl;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.EnumMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import streckenplan.api.Direction;
import types.Color;
import types.Vector2d;
import util.*;
import view.painter.Painter;

final class TrackArrangements {
	private TrackArrangements() {
	}

	private static final Map<Direction, TrackArrangement> straightTracks = new EnumMap<Direction, TrackArrangement>(Direction.class);

	private static final Map<Direction, TrackArrangement> curveTracks = new EnumMap<Direction, TrackArrangement>(Direction.class); // right curves from starting point

	private static final Map<Direction, TrackArrangement> shortTerminus = new EnumMap<Direction, TrackArrangement>(Direction.class);
	private static final Map<Direction, TrackArrangement> longTerminus = new EnumMap<Direction, TrackArrangement>(Direction.class);

	private static final Stroke borderStroke = StrokeUtil.basic(1f / 6);
	private static final Stroke fillStroke = StrokeUtil.basic(1f / 6 - 1f / 16);
	private static final Stroke activeIndicatorStroke = StrokeUtil.basic(1f / 24);
	private static final Stroke fahrstrasseStroke = StrokeUtil.basic(3 / 8f);

	private static final Color borderColor = Color.black;
	//private static final Color fillColor = Color.white;
	private static final Color activeIndicatorColor = Color.blue;
	private static final Color fahrstrasseColor = Color.create(1, 1, .3f);

	static {
		for (Direction i : Direction.values()) {
			if (i.ordinal() < 3)
				straightTracks.put(i, new Straight(i));
			else
				straightTracks.put(i, straightTracks.get(i.reverse()));

			curveTracks.put(i, new RightCurve(i));
			shortTerminus.put(i, new Terminus(i, 2f / 3 - 1f / 2));
			longTerminus.put(i, new Terminus(i, 2f / 3));
		}
	}

	//public static TrackArrangement get(Direction end1, TrackType type) {
	//	if (type == TrackType.straight)
	//		return straight(end1);
	//	else if (type == TrackType.leftCurve)
	//		return leftCurve(end1);
	//	else if (type == TrackType.rightCurve)
	//		return rightCurve(end1);
	//	else if (type == TrackType.facingEndpoint)
	//		return terminus(end1, false);
	//	else
	//		throw new AssertionError();
	//}

	public static TrackArrangement straight(Direction dir) {
		return straightTracks.get(dir);
	}

	public static TrackArrangement leftCurve(Direction dir) {
		return rightCurve(dir.turn(-2));
	}

	public static TrackArrangement rightCurve(Direction dir) {
		return curveTracks.get(dir);
	}

	public static TrackArrangement terminus(Direction dir, boolean long_) {
		return (long_ ? longTerminus : shortTerminus).get(dir);
	}

	public abstract static class AbstractTrackArrangement implements TrackArrangement {
		protected final Direction startDirection;
		protected final Direction endDirection;

		protected AbstractTrackArrangement(Direction startDirection, @Nullable Direction endDirection) {
			this.startDirection = startDirection;
			this.endDirection = endDirection;
		}

		@Override
		public final Direction getStartDirection() {
			return startDirection;
		}

		@Nullable
		@Override
		public final Direction getEndDirection() {
			return endDirection;
		}
	}

	private static final class Straight extends AbstractTrackArrangement {
		private final Shape shape;
		private final Shape activeIndicatorShape;

		{
			AffineTransform transform = AffineTransform.getRotateInstance(startDirection.ordinal() / 6. * MathUtil.tau);

			shape = transform.createTransformedShape(ShapeUtil.line(Vector2d.create(-1f / 2, 0), Vector2d.create(1f / 2, 0)));
			activeIndicatorShape = transform.createTransformedShape(ShapeUtil.line(Vector2d.create(-1f / 4, 0), Vector2d.create(1f / 4, 0)));
		}

		private Straight(Direction startDirection) {
			super(startDirection, startDirection);
		}

		@Override
		public void paintBorder(Painter p) {
			p.draw(borderColor, borderStroke, shape);
		}

		@Override
		public void paintFill(Painter p, Color color) {
			p.draw(color, fillStroke, shape);
		}

		@Override
		public void paintActiveIndicator(Painter p) {
			p.draw(activeIndicatorColor, activeIndicatorStroke, activeIndicatorShape);
		}

		@Override
		public void paintFahrstrasse(Painter p) {
			p.draw(fahrstrasseColor, fahrstrasseStroke, shape);
		}

		@Override
		public double getLength() {
			return 1;
		}

		@Override
		public Vector2d positionOnPath(double position) {
			return Vector2d.fromAngle(startDirection.angle, position - 1. / 2);
		}

		@Override
		public String toString() {
			return String.format("Straight(direction = %s)", startDirection);
		}
	}

	/**
	 * Right curve
	 */
	private static final class RightCurve extends AbstractTrackArrangement {
		private final Shape shape;
		private final Shape activeIndicatorShape;
		private final Vector2d arcCenter = Vector2d.fromAngle(startDirection.angle, -1. / 2).add(Vector2d.fromAngle(startDirection.angle - MathUtil.tau / 4, radius));

		{
			AffineTransform transform = AffineTransform.getRotateInstance(startDirection.ordinal() / 6. * MathUtil.tau);

			shape = transform.createTransformedShape(ShapeUtil.arc(Vector2d.create(-.5, -radius), radius, MathUtil.tau * 1 / 12, MathUtil.tau * 3 / 12));
			activeIndicatorShape = transform.createTransformedShape(ShapeUtil.arc(Vector2d.create(-.5, -radius), radius, MathUtil.tau * 3 / 24, MathUtil.tau * 5 / 24));
		}

		private RightCurve(Direction startDirection) {
			super(startDirection, startDirection.turn(-1));
		}

		@Override
		public void paintBorder(Painter p) {
			p.draw(borderColor, borderStroke, shape);
		}

		@Override
		public void paintFill(Painter p, Color color) {
			p.draw(color, fillStroke, shape);
		}

		@Override
		public void paintActiveIndicator(Painter p) {
			p.draw(activeIndicatorColor, activeIndicatorStroke, activeIndicatorShape);
		}

		@Override
		public void paintFahrstrasse(Painter p) {
			p.draw(fahrstrasseColor, fahrstrasseStroke, shape);
		}

		@Override
		public double getLength() {
			return radius * MathUtil.tau / 6;
		}

		@Override
		public Vector2d positionOnPath(double position) {
			return arcCenter.add(Vector2d.fromAngle(startDirection.angle + MathUtil.tau / 4 - position / radius, radius));
		}

		@Override
		public String toString() {
			return String.format("RightCurve(startDirection = %s, endDirection = %s)", startDirection, endDirection);
		}

		private static final double radius = Math.sqrt(3) / 2;
	}

	private static final class Terminus extends AbstractTrackArrangement {
		private final double length;

		private Terminus(Direction end1, double length) {
			super(end1, null);

			this.length = length;
		}

		@Override
		public void paintBorder(Painter p) {
		}

		@Override
		public void paintFill(Painter p, Color color) {
			Painter p2 = p.rotated(startDirection.angle);

			p2.draw(borderColor, borderStroke, ShapeUtil.line(Vector2d.create(.5, 0), Vector2d.create(.5 - length - 1f / 16 / 5 * 6, 0)));
			p2.draw(color, fillStroke, ShapeUtil.line(Vector2d.create(.5, 0), Vector2d.create(.5 - length, 0)));
		}

		@Override
		public void paintActiveIndicator(Painter p) {
		}

		@Override
		public void paintFahrstrasse(Painter p) {
		}

		@Override
		public double getLength() {
			return length;
		}

		@Override
		public Vector2d positionOnPath(double position) {
			return Vector2d.fromAngle(startDirection.angle, position - 1. / 2);
		}

		@Override
		public String toString() {
			return String.format("Terminus(direction = %s, length = %s)", startDirection, length);
		}
	}
}
