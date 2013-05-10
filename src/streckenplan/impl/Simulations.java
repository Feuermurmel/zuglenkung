package streckenplan.impl;

import streckenplan.api.Simulation;

public class Simulations {
	private Simulations() {
	}

	public static Simulation createSimulation() {
		return new SimulationImpl();
	}
}
