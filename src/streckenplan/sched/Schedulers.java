package streckenplan.sched;

public class Schedulers {
	private Schedulers() {
	}

	public static Scheduler createScheduler() {
		return new SchedulerImpl();
	}
}
