package streckenplan.api;

import java.util.List;

public interface Signal {
	Field getField();

	List<? extends Track> getAffectedTracks();

	Aspect getCurrentAspect();

	void setCurrentAspect(Aspect aspect);
}
