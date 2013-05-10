package streckenplan.api;

import streckenplan.sched.Scheduler;
import streckenplan.sched.Steppable;
import view.painter.Paintable;

public interface Simulation extends Steppable {
	Layout getLayout();

	Paintable getPaintable();

	Scheduler getScheduler();
}
