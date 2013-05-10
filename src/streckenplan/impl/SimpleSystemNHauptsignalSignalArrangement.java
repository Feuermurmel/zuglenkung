package streckenplan.impl;

import java.awt.Stroke;

import streckenplan.api.Aspect;
import streckenplan.api.Direction;
import types.Color;
import util.MathUtil;
import util.StrokeUtil;
import view.painter.Painter;

import static types.Color.*;
import static types.Vector2d.create;
import static util.ShapeUtil.*;

final class SimpleSystemNHauptsignalSignalArrangement extends SignalArrangement {
	private final double offset;
	private final double angle;

	SimpleSystemNHauptsignalSignalArrangement(Direction limitEntryAngle, Direction limitExitAngle) {
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
		Painter p2 = p.rotated(angle).translated(create(-offset, 0));
		Stroke stroke = StrokeUtil.basic(u);

		p2.fill(black,
				polygon(
						create(-.75 * u, (1.25 + 4) * u),
						create(-1.25 * u, (.75 + 4) * u),
						create(-1.25 * u, -.75 * u),
						create(-.75 * u, -1.25 * u),
						create(.75 * u, -1.25 * u),
						create(1.25 * u, -.75 * u),
						create(1.25 * u, (.75 + 4) * u),
						create(.75 * u, (1.25 + 4) * u)));

		p2.draw(black, stroke,
			line(create(-u * 1.5, -u * 4), create(u * 1.5, -u * 4)),
			line(create(0, -u * 4), create(0, -1.25 * u)));

		float r = u * .6f;
		Color offColor = gray(.2f);

		// From top to bottom.
		Color[] colors = {
				green.brighten(.2f),
				red.brighten(.2f),
				red.blend(yellow, .5f).brighten(.2f) };

		boolean[] lamps = new boolean[colors.length];

		if (currentAspect.speed < .4) {
			lamps[1] = true;
		} else if (currentAspect.speed < 1.0) {
			lamps[0] = true;
			lamps[2] = true;
		} else {
			lamps[0] = true;
		}

		for (int i = 0; i < lamps.length; i += 1) {
			p2.fill(lamps[i] ? colors[i] : offColor, circle(create(0, (lamps.length - i - 1) * 2 * u), r));
		}
	}
}
