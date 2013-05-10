package streckenplan.impl;

import streckenplan.api.Layout;
import streckenplan.api.Simulation;
import streckenplan.sched.Scheduler;
import streckenplan.sched.Schedulers;
import view.painter.Paintable;

final class SimulationImpl implements Simulation {
	private final Scheduler scheduler = Schedulers.createScheduler();
	private final LayoutImpl layout = new LayoutImpl(scheduler);

	@Override
	public Layout getLayout() {
		return layout;
	}

	@Override
	public Paintable getPaintable() {
		return layout;
	}

	@Override
	public Scheduler getScheduler() {
		return scheduler;
	}

	@Override
	public void step(double delta) {
		scheduler.step(delta);
		layout.step(delta);
	}
}
