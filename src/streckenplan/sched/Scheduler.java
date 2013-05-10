package streckenplan.sched;

public interface Scheduler extends Steppable {
	Task post(double delay, Runnable runnable);

	double getTime();
}
