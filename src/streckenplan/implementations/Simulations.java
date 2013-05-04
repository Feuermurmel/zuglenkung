package streckenplan.implementations;

import streckenplan.interfaces.Simulation;

public class Simulations {
	private Simulations() {
	}
	
	public static Simulation createSimulation() {
		return new SimulationImpl();
	}
}
