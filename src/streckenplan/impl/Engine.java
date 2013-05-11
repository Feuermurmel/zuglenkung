package streckenplan.impl;

import streckenplan.sched.Steppable;
import util.MathUtil;

final class Engine implements Steppable {
	private final double maxAcceleration;
	private final double maxPowerPerMass;
	private final double maxBreakDeceleration;
	private final double maxBreakPowerPerMass;
	private final double freeWheelingDeceleration;
	private final double airResistance;

	private double currentSpeed = 0;
	private double targetSpeed = 0;

	Engine() {
		maxAcceleration = .1;
		maxPowerPerMass = .2;
		maxBreakDeceleration = .1;
		maxBreakPowerPerMass = .2;
		freeWheelingDeceleration = .01;
		airResistance = .01;
	}

	public void setTargetSpeed(double value) {
		targetSpeed = value;
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	@Override
	public void step(double delta) {
		double maxAcceleration = MathUtil.clamp(0, this.maxAcceleration, maxPowerPerMass / (2 * currentSpeed) - freeWheelingDeceleration - airResistance * currentSpeed);
		double maxDeceleration = MathUtil.clamp(0, maxBreakDeceleration, maxBreakPowerPerMass / (2 * currentSpeed) + freeWheelingDeceleration + airResistance * currentSpeed);

		double maxNextSpeed = currentSpeed + maxAcceleration * delta;
		double minNextSpeed = currentSpeed - maxDeceleration * delta;
		
		currentSpeed = MathUtil.clamp(minNextSpeed, maxNextSpeed, targetSpeed);
	}

	public double getTargetSpeed() {
		return targetSpeed;
	}
}
