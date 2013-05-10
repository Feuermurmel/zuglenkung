package streckenplan.impl;

import streckenplan.api.Aspect;
import streckenplan.api.Direction;
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

	public abstract void paint(Painter p, Aspect currentAspect);
}
