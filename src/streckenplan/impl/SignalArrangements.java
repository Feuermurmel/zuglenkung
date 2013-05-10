package streckenplan.impl;

import java.util.HashMap;

import streckenplan.api.Direction;
import streckenplan.api.SignalType;
import types.Triple;
import types.Tuples;

class SignalArrangements {
	private SignalArrangements() {
	}

	static final HashMap<Triple<SignalType, Direction, Direction>, SignalArrangement> signals = new HashMap<Triple<SignalType, Direction, Direction>, SignalArrangement>();

	static {
		for (Direction i : Direction.values()) {
			for (int j = -1; j < 2; j += 1) {
				Direction limitExitAngle = i.turn(j);

				signals.put(Tuples.triple(SignalType.dwarf, i, limitExitAngle), new DwarfSignalArrangement(i, limitExitAngle));
				signals.put(Tuples.triple(SignalType.systemLHauptsignal, i, limitExitAngle), new SimpleSystemNHauptsignalSignalArrangement(i, limitExitAngle));
			}
		}
	}

	static SignalArrangement getArrangement(Direction limitEntryAngle, Direction limitExitAngle, SignalType type) {
		SignalArrangement result = signals.get(Tuples.triple(type, limitEntryAngle, limitExitAngle));

		if (result == null)
			throw new IllegalArgumentException();

		return result;
	}
}
