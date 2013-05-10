package streckenplan.impl;

import java.awt.Font;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import streckenplan.api.*;
import streckenplan.sched.Steppable;
import types.*;
import util.*;
import view.painter.Paintable;
import view.painter.Painter;

import static util.MathUtil.*;

final class TrainImpl implements Train, Steppable, Paintable {
	private final LayoutImpl layout;
	private final Engine engine;
	private final List<CarImpl> cars = new ArrayList<CarImpl>();
	private TrackPosition position;
	private boolean crashed = false;

	TrainImpl(LayoutImpl layout, TrackPosition position) {
		this.layout = layout;
		this.position = position;
		engine = new Engine(1. / 25);

		updateCarOrientations();
	}

	@Override
	public void step(double delta) {
		if (crashed)
			return;

		List<SignalImpl> signals = position.track.getAffectingSignals();

		if (!signals.isEmpty()) {
			Aspect aspect = signals.get(0).getCurrentAspect();

			if (aspect.speed > 0) {
				engine.setTargetSpeed(aspect.speed);
			} else if (aspect.distance > 0) {
				engine.setTargetSpeed(.4);
			} else {
				engine.setTargetSpeed(0);
			}
		}

		engine.step(delta);

		try {
			position = position.move(engine.getCurrentSpeed() * delta);
		} catch (NoMoreTrackException ignored) {
			crashed = true;
		}

		updateCarOrientations();
	}

	@Override
	public void paint(Painter p) {
		for (CarImpl i : cars) {
			i.paint(p);
		}

		Orientation2d orientation = cars.get(0).getCurrentOrientation();

		if (MathUtil.mod(orientation.angle + tau / 4, tau) > tau / 2)
			orientation = orientation.rotate(tau / 2);

		Font font = FontUtil.font("Helvetica Neue", 1);
		String text = String.format("%s", (int) Math.ceil(engine.getCurrentSpeed() * 100));
		Shape shape = ShapeUtil.text(Vector2d.zero, font, text, .5);

		p.translated(orientation.position()).rotated(orientation.angle).translated(Vector2d.create(0, -.3)).scaled(.2).fill(Color.black, shape);
	}

	@Override
	public double getCurrentSpeed() {
		return engine.getCurrentSpeed();
	}

	@Override
	public void setTargetSpeed(double speed) {
		engine.setTargetSpeed(speed);
	}

	@Override
	public Car addCar(double length) {
		CarImpl car = new CarImpl(this, length);

		cars.add(car);
		updateCarOrientations();

		return car;
	}

	private void updateCarOrientations() {
		TrackPosition beginPosition = position;

		layout.occupyField(beginPosition.track.getField(), this);

		for (CarImpl i : cars) {
			TrackPosition middlePosition;
			TrackPosition endPosition;

			// We just place all cars until we hit a problem, the other cars will stay at their current position.
			try {
				double h = -i.getLength() / 2;
				middlePosition = beginPosition.move(h);
				endPosition = middlePosition.move(h);
			} catch (NoMoreTrackException ignored) {
				crashed = true;

				return;
			}

			layout.occupyField(middlePosition.track.getField(), this);
			layout.occupyField(endPosition.track.getField(), this);

			i.updateOrientation(endPosition, middlePosition, beginPosition);

			beginPosition = endPosition;
		}
	}

	public void setCrashedWithOtherTrain() {
		crashed = true;
	}

	@Override
	public double getLength() {
		double length = 0;

		for (CarImpl i : cars) {
			length += i.getLength();
		}

		return length;
	}

	@Override
	public List<Car> getCars() {
		return new ArrayList<Car>(cars);
	}

	public boolean hasCrashed() {
		return crashed;
	}
}
