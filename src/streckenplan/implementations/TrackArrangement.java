package streckenplan.implementations;

import types.Vector2d;
import org.jetbrains.annotations.Nullable;
import streckenplan.interfaces.Direction;
import view.painter.Painter;

interface TrackArrangement {
	void paintBorder(Painter p);
	void paintFill(Painter p);
	void paintActiveIndicator(Painter p);
	void paintFahrstrasse(Painter p);
	double getLength();

	Vector2d positionOnPath(double position);
	Direction getStartDirection();
	/** May return null. */
	@Nullable
	Direction getEndDirection();
}


//public abstract static class State {
//	private final Color border;
//	private final Color fill;
//
//	private State(Color border, Color fill) {
//		this.border = border;
//		this.fill = fill;
//	}
//
//	public abstract void paintLayer1(Painter p, Track track);
//	public abstract void paintLayer2(Painter p, Track track);
//
//	private static final State unsetFree;
//	private static final State unsetLocked;
//	private static final State unsetOccupied;
//	private static final State setFree;
//	private static final State setLocked;
//	private static final State setOccupied;
//
//	static {
//		Color lockedFill = new Color(.5f, 1, 0);
//		Color occupiedFill = new Color(1, 0, 0);
//		Color setBorder = new Color(0, .5f, 1);
//		
//		unsetFree = new State(Color.black, Color.white);
//		unsetLocked = new State(Color.black, lockedFill);
//		unsetOccupied = new State(Color.black, occupiedFill);
//		setFree = new State(setBorder, Color.white);
//		setLocked = new State(setBorder, lockedFill);
//		setOccupied = new State(setBorder, occupiedFill);
//	}
//	
//	public static State get(boolean set, boolean locked, boolean occupied) {
//		if (set)
//			if (occupied)
//				return setOccupied;
//			else if (locked)
//				return setLocked;
//			else
//				return setFree;
//		else
//			if (occupied)
//				return unsetOccupied;
//			else if (locked)
//				return unsetLocked;
//			else
//				return unsetFree;
//	}
//
//	private static final class Set extends State {
//		private Set(Color border, Color fill) {
//			super(border, fill);
//		}
//
//		@Override
//		public void paintLayer1(Painter p, Track track) {
//			p.rotated(end1.ordinal() / 3f * Math.PI).draw(Color.black, StrokeUtil.basic(4f / 40), line);
//		}
//
//		@Override
//		public void paintLayer2(Painter p, Track track) {
//			p.rotated(end1.ordinal() / 3f * Math.PI).draw(new Color(1, 1, 0), StrokeUtil.basic(2.5f / 40), line);
//		}
//	}
//
//	private static final class Unset extends State {
//		private Unset(Color border, Color fill) {
//			super(border, fill);
//		}
//
//		@Override
//		public void paintLayer1(Painter p, Track track) {
//			...
//		}
//
//		@Override
//		public void paintLayer2(Painter p, Track track) {
//			...
//		}
//	}
//}
