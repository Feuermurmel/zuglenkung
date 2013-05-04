package streckenplan.implementations;

import java.util.*;

import streckenplan.interfaces.*;
import streckenplan.sched.Scheduler;
import streckenplan.sched.Schedulers;
import view.Steppable;
import view.painter.Paintable;

final class SimulationImpl implements Simulation {
	private final Scheduler scheduler = Schedulers.createScheduler();
	private final LayoutImpl layout = new LayoutImpl(scheduler);
	private final Set<Steppable> steppables = new LinkedHashSet<Steppable>(Arrays.asList(scheduler, layout)); // determinacy FTW!

	@Override
	public Layout getLayout() {
		return layout;
	}

	@Override
	public Paintable getPaintable() {
		return layout;
	}

	@Override
	public void step(double delta) {
		for (Steppable i : steppables)
			i.step(delta);
	}

	public void addSteppable(Steppable steppable) {
		assert steppables.add(steppable);
	}

	public void removeSteppable(Steppable steppable) {
		assert steppables.remove(steppable);
	}
}
