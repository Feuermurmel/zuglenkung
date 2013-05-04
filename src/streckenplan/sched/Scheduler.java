package streckenplan.sched;

import view.Steppable;

public interface Scheduler extends Steppable {
	Task post(double delay, Runnable runnable);
	double getTime();
}
