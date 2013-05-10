package streckenplan.impl;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import streckenplan.api.*;

final class EdgeImpl implements Edge {
	private final FieldImpl field;
	private final Direction direction;

	EdgeImpl(FieldImpl field, Direction direction) {
		this.field = field;
		this.direction = direction;
	}

	@Override
	public EdgeImpl reverse() {
		Direction reverseDirection = direction.reverse();

		return new EdgeImpl(field.getNeighbour(reverseDirection, 1), reverseDirection);
	}

	@Override
	public FieldImpl getField() {
		return field;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}

	@Override
	public List<Track> getTracks() {
		return field.getTracksFromEdge(direction);
	}

	@Nullable
	@Override
	public TrackImpl getActiveTrack() {
		return field.getActiveTrackFromEdge(direction);
	}
}
