package streckenplan.implementations;

import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.*;

import types.Vector2d;
import streckenplan.interfaces.*;
import types.*;
import util.ShapeUtil;
import util.StrokeUtil;
import view.painter.Painter;

abstract class SignalArrangement {
	// limitEntryAngle and limitExitAngle define a range. The range starts at limitEntryAngle.reverse() and ends at limitExitAngle. A signal affects all tracks that enter and exit a field within that range and where the entry point is "before" the exit point. No tracks are allowed to enter or exit at points outside that range at all.
	public final Direction limitEntryAngle;
	public final Direction limitExitAngle;

	protected SignalArrangement(Direction limitEntryAngle, Direction limitExitAngle) {
		this.limitEntryAngle = limitEntryAngle;
		this.limitExitAngle = limitExitAngle;
	}
	
	public final boolean affectsTrack(Direction entryDirection, Direction exitDirection) {
		boolean hitEntry = false; // To check whether we hit the entry before the exit.
		
		for (Direction dir = limitEntryAngle.reverse(); dir != limitExitAngle.turn(1); dir = dir.turn(1)) {
			if (hitEntry) {
				if (dir == exitDirection)
					return true;
			} else {
				if (dir == entryDirection.reverse())
					hitEntry = true;
			}
		}
		
		return false;
	}

	private static final Map<Tuple<Direction, Direction>, SignalArrangement> dwarfSignals = new HashMap<Tuple<Direction, Direction>, SignalArrangement>();

	static {
		for (Direction i : Direction.values()) {
			for (int j = -1; j < 2; j += 1) {
				Direction limitExitAngle = i.turn(j);
				
				dwarfSignals.put(Tuples.tuple(i, limitExitAngle), new Dwarf(i, limitExitAngle));
			}
		}
	}

	public static SignalArrangement getArrangement(Direction limitEntryAngle, Direction limitExitAngle, SignalType type) {
		assert type == SignalType.dwarf;

		SignalArrangement result = dwarfSignals.get(Tuples.tuple(limitEntryAngle, limitExitAngle));

		if (result == null)
			throw new IllegalArgumentException();
		
		return result;
	}

	public abstract void paint(Painter p, Aspect currentAspect);

	private static final class Dwarf extends SignalArrangement {
		private final double offset;
		private final double angle;

		private Dwarf(Direction limitEntryAngle, Direction limitExitAngle) {
			super(limitEntryAngle, limitExitAngle);
			
			int range = limitExitAngle.diff(limitEntryAngle.turn(-4)); // Number of tracks edges in the signals affecting range.
			
			angle = Direction.turnRangeMidAngle(limitEntryAngle.reverse(), limitExitAngle);

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
		public void paint(Painter p, Aspect aspect) {
			// TODO: Use aspect here
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
			Color color = Color.yellow.brighten(.8f);
			
			boolean topLeft = false;
			boolean bottomLeft = false;
			boolean bottomRight = false;
			
			if (aspect.speed > 0) {
				topLeft = true;
				bottomLeft = true;
			} else if (aspect.distance > 0) {
				topLeft = true;
				bottomRight = true;
			} else {
				bottomLeft = true;
				bottomRight = true;
			}
			
			if (topLeft)
				p2.fill(color, ShapeUtil.circle(Vector2d.create(-u, 3 * u), r)); // Top left light
			
			if (bottomLeft)
				p2.fill(color, ShapeUtil.circle(Vector2d.create(-u, u), r)); // Bottom left light
			
			if (bottomRight)
				p2.fill(color, ShapeUtil.circle(Vector2d.create(u, u), r)); // Bottom right Light
		}
	}
}
