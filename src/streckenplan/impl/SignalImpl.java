package streckenplan.impl;

import java.util.List;

import streckenplan.api.*;
import view.painter.Paintable;
import view.painter.Painter;

final class SignalImpl implements Signal, Paintable {
	private final FieldImpl field;
	private final SignalArrangement arrangement;
	private Aspect currentAspect = Aspect.create(0, 0);

	SignalImpl(FieldImpl field, SignalArrangement arrangement) {
		this.field = field;
		this.arrangement = arrangement;
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public List<Track> getAffectedTracks() {
		return field.getAffectedTracks(this);
	}

	@Override
	public Aspect getCurrentAspect() {
		return currentAspect;
	}

	@Override
	public void setCurrentAspect(Aspect currentAspect) {
		this.currentAspect = currentAspect;
	}

	@Override
	public void paint(Painter p) {
		arrangement.paint(p, currentAspect);
	}

	public SignalArrangement getArrangement() {
		return arrangement;
	}
}
