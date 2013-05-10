package streckenplan.impl;

import streckenplan.sched.Steppable;

final class Engine implements Steppable {
	private final double maxPowerPerMass;
	private final double maxBreakDeceleration;
	private final double freeWheelingDeceleration;
	//private final double freeWheelingPowerPerMass;

	private double currentSpeed = 0;
	private double targetSpeed = 0;

	Engine(double maxPowerPerMass) {
		this.maxPowerPerMass = maxPowerPerMass;
		maxBreakDeceleration = 1. / 10;
		freeWheelingDeceleration = 1. / 100;
		//freeWheelingPowerPerMass = 1. / 10;
	}

	public void setTargetSpeed(double value) {
		targetSpeed = value;
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	@Override
	public void step(double delta) {
		double nextSpeed = currentSpeed;

		nextSpeed = addAcceleration(nextSpeed, delta, -freeWheelingDeceleration);

		if (currentSpeed > targetSpeed) {
			nextSpeed = addAcceleration(nextSpeed, delta, -maxBreakDeceleration);

			if (nextSpeed < 0)
				nextSpeed = 0;
		} else {
			nextSpeed = addPowerPerMass(nextSpeed, delta, maxPowerPerMass);

			if (nextSpeed > targetSpeed)
				nextSpeed = targetSpeed;
		}

		currentSpeed = nextSpeed;
	}

	private static double addPowerPerMass(double speed, double delta, double powerPerMass) {
		double v2 = speed * speed + powerPerMass * delta * 2;

		if (v2 > 0)
			return Math.sqrt(v2);
		else
			return 0;
	}

	private static double addAcceleration(double speed, double delta, double acceleration) {
		return speed + acceleration * delta;
	}
}
