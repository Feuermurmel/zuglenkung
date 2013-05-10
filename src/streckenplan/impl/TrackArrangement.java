package streckenplan.impl;

import org.jetbrains.annotations.Nullable;
import streckenplan.api.Direction;
import types.Color;
import types.Vector2d;
import view.painter.Painter;

interface TrackArrangement {
	void paintBorder(Painter p);

	void paintFill(Painter p, Color color);

	void paintActiveIndicator(Painter p);

	void paintFahrstrasse(Painter p);

	double getLength();

	Vector2d positionOnPath(double position);

	Direction getStartDirection();

	/**
	 * May return null.
	 */
	@Nullable
	Direction getEndDirection();
}
