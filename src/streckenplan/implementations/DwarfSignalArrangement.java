package streckenplan.implementations;

import java.awt.Stroke;
import java.awt.geom.Path2D;

import streckenplan.interfaces.Aspect;
import streckenplan.interfaces.Direction;
import types.Color;
import types.Vector2d;
import util.*;
import view.painter.Painter;

final class DwarfSignalArrangement extends SignalArrangement {
	private final double offset;
	private final double angle;

	DwarfSignalArrangement(Direction limitEntryAngle, Direction limitExitAngle) {
		super(limitEntryAngle, limitExitAngle);
		
		int range = limitExitAngle.minus(limitEntryAngle.turn(-4)); // Number of tracks edges in the signals affecting range.

		angle = limitEntryAngle.reverse().angle + limitExitAngle.minus(limitEntryAngle.reverse()) * MathUtil.tau / 12;

		if (range == 3)
			offset = .11;
		else if (range == 4)
			offset = .25;
		else if (range == 5)
			offset = .36;
		else
			throw new AssertionError();
	}

	@Override
	public void paint(Painter p, Aspect currentAspect) {
		float u = 1f / 24;
		Painter p2 = p.rotated(angle).translated(Vector2d.create(-offset, -u / 2));
		Stroke stroke = StrokeUtil.basic(u);
		Path2D.Double path = new Path2D.Double();
		
		path.moveTo(-u * 2, 0);
		path.lineTo(u * 2, 0);
		path.lineTo(u * 2, u * 1.75);
		path.lineTo(-u * .25, u * 4);
		path.lineTo(-u * 2, u * 4);
		path.closePath();

		p2.fill(Color.black, path);
		p2.draw(Color.black, stroke,
			ShapeUtil.line(Vector2d.create(-u * 2, -u * 2), Vector2d.create(u * 2, -u * 2)),
			ShapeUtil.line(Vector2d.create(0, -u * 2), Vector2d.create(0, u)));
		
		float r = u * .6f;
		Color onColor = Color.yellow.brighten(.8f);
		Color offColor = Color.gray(.2f);
		
		Color topLeft = offColor;
		Color bottomLeft = offColor;
		Color bottomRight = offColor;
		
		if (currentAspect.speed > 0) {
			topLeft = onColor;
			bottomLeft = onColor;
		} else if (currentAspect.distance > 0) {
			topLeft = onColor;
			bottomRight = onColor;
		} else {
			bottomLeft = onColor;
			bottomRight = onColor;
		}
		
		p2.fill(topLeft, ShapeUtil.circle(Vector2d.create(-u, 3 * u), r));
		p2.fill(bottomLeft, ShapeUtil.circle(Vector2d.create(-u, u), r));
		p2.fill(bottomRight, ShapeUtil.circle(Vector2d.create(u, u), r));
	}
}
