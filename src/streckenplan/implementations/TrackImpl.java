package streckenplan.implementations;

import java.util.Collections;
import java.util.List;

import types.Vector2d;
import org.jetbrains.annotations.Nullable;
import streckenplan.interfaces.*;

final class TrackImpl implements Track {
	private final FieldImpl field;
	private final TrackArrangement arrangement;
	private final boolean reversed;

	TrackImpl(FieldImpl field, TrackArrangement arrangement, boolean reversed) {
		this.field = field;
		this.arrangement = arrangement;
		this.reversed = reversed;
	}

	@Override
	public TrackImpl reverse() {
		return field.getTrackForArrangement(arrangement, !reversed);
	}

	@Override
	public FieldImpl getField() {
		return field;
	}

	@Override
	public List<SignalImpl> getAffectingSignals() {
		Direction startDirection = arrangement.getStartDirection();
		Direction endDirection = arrangement.getEndDirection();

		if (endDirection == null)
			return Collections.emptyList();
		else
			return field.getAffectingSignals(this);
	}

	@Nullable
	@Override
	public EdgeImpl getStartEdge() {
		return getEdge(false);
	}

	@Nullable
	@Override
	public EdgeImpl getEndEdge() {
		return getEdge(true);
	}

	@Nullable
	private EdgeImpl getEdge(boolean end) {
		Direction direction;
		FieldImpl field = this.field;

		if (end == reversed)
			direction = arrangement.getStartDirection();
		else
			direction = arrangement.getEndDirection();
	
		if (direction == null)
			return null;

		if (reversed)
			direction = direction.reverse();
		
		if (end)
			field = field.getNeighbour(direction, 1);
		
		return new EdgeImpl(field, direction);
	}

	@Override
	public boolean isActive() {
		return field.getActiveTrack() == arrangement;
	}

	@Override
	public void activate(Runnable success) {
		field.activateTrack(arrangement, success);
	}

	@Override
	public Train addTrain(double engineCarLength) {
		return field.getLayout().addTrain(new TrackPosition(this, 0), engineCarLength);
	}

	@Override
	public Signal addSignal(SignalType type) {
		EdgeImpl startEdge = getStartEdge();
		EdgeImpl endEdge = getEndEdge();
		
		if (startEdge == null || endEdge == null)
			throw new UnsupportedOperationException("Cannot add a signal to a track end.");
		
		Direction startDirection = startEdge.getDirection();
		Direction endDirection = endEdge.getDirection();
		Direction limitEntryDirection = null;
		Direction limitExitDirection = null;
		
		// hope this works ...
		for (Direction dir = startDirection.turn(2); dir != endDirection; dir = dir.turn(-1)) {
			if (field.getEdge(dir).getTracks().isEmpty()) {
				if (limitEntryDirection == null)
					limitEntryDirection = dir.turn(-2);
				
				limitExitDirection = dir.turn(-1);
			}
		}
		
		if (limitEntryDirection == null)
			throw new IllegalStateException();
		
		return field.addSignal(limitEntryDirection, limitExitDirection, type);
	}

	public double length() {
		return arrangement.getLength();
	}

	public Vector2d positionOnPath(double position) {
		return arrangement.positionOnPath(reversed ? length() - position : position).add(field.getPosition());
	}

	@Override
	public String toString() {
		return String.format("TrackImpl(field = %s, arrangement = %s, reversed = %s)", field, arrangement, reversed);
	}

	public TrackArrangement getArrangement() {
		return arrangement;
	}
}
