package streckenplan.api;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import types.Color;

public interface Track {
	Track reverse();

	Field getField();

	List<? extends Signal> getAffectingSignals();

	/**
	 * null for end tracks in direction from the end.
	 */
	@Nullable
	Edge getStartEdge();

	/**
	 * null for end tracks in direction to the end.
	 */
	@Nullable
	Edge getEndEdge();

	boolean isActive();

	void activate(Runnable success);

	Train addTrain(double engineCarLength);

	Signal addSignal(SignalType type);

	void setColor(Color color);
}
