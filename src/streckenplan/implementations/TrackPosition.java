package streckenplan.implementations;

import types.Vector2d;

final class TrackPosition {
	public final TrackImpl track;
	public final double position;

	TrackPosition(TrackImpl track, double position) {
		this.track = track;
		this.position = position;
	}

	public TrackPosition reverse() {
		return new TrackPosition(track.reverse(), track.length() - position);
	}

	public TrackPosition move(double distance) throws NoMoreTrackException {
		TrackImpl track = this.track;
		double position = this.position + distance;

		while (position > track.length()) {
			position -= track.length();
			EdgeImpl edge = track.getEndEdge();

			if (edge == null)
				throw new NoMoreTrackException();

			track = edge.getActiveTrack();

			if (track == null)
				throw new NoMoreTrackException();
		}

		while (position < 0) {
			EdgeImpl edge = track.getStartEdge();

			if (edge == null)
				throw new NoMoreTrackException();

			TrackImpl activeTrack = edge.reverse().getActiveTrack();

			if (activeTrack == null)
				throw new NoMoreTrackException();

			track = activeTrack.reverse();

			if (track == null)
				throw new NoMoreTrackException();

			position += track.length();
		}

		return new TrackPosition(track, position);
	}

	public Vector2d positionOnPath() {
		return track.positionOnPath(position);
	}

	@Override
	public String toString() {
		return String.format("TrackPosition(track = %s, position = %s)", track, position);
	}
}
