package streckenplan.interfaces;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public interface Edge {
	Edge reverse();
	Field getField();
	Direction getDirection();
	List<Track> getTracks();
	/** May return null */
	@Nullable
	Track getActiveTrack();
}
