package streckenplan.api;

public interface Field {
	/**
	 * Return the field that is distance fields in the given direction.
	 */
	Field getNeighbour(Direction direction, int distance);

	/**
	 * Get the edge that enters the field in the given direction.
	 */
	Edge getEdge(Direction direction);

	boolean isOccupied();

	Track addTrack(Direction startDirection, Direction endDirection);

	Track addEndTrack(Direction startDirection, EndTrackLength length);

	// TODO: rename to awaitState
	void waitUntilState(boolean occupied, Runnable runnable);
}
