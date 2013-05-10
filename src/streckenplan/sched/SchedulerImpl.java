package streckenplan.sched;

import java.util.*;

import org.jetbrains.annotations.NotNull;

final class SchedulerImpl implements Scheduler {
	private double currentTime = 0;
	private final SortedSet<TaskImpl> tasks = new TreeSet<TaskImpl>(new Comparator<TaskImpl>() {
		@Override
		public int compare(@NotNull TaskImpl o1, @NotNull TaskImpl o2) {
			return Double.compare(o1.scheduledTime, o2.scheduledTime);
		}
	});

	@Override
	public void step(double delta) {
		double endTime = currentTime + delta;

		while (currentTime < endTime) {
			TaskImpl task;

			try {
				task = tasks.first();
			} catch (NoSuchElementException ignored) {
				break;
			}

			if (task.scheduledTime > endTime)
				break;

			tasks.remove(task);

			currentTime = task.scheduledTime;
			task.runnable.run();
		}

		currentTime = endTime;
	}

	@Override
	public Task post(double delay, Runnable runnable) {
		TaskImpl task = new TaskImpl(currentTime + delay, runnable);

		tasks.add(task);

		return task;
	}

	@Override
	public double getTime() {
		return currentTime;
	}

	private final class TaskImpl implements Task {
		private final double scheduledTime;
		private final Runnable runnable;

		private TaskImpl(double scheduledTime, Runnable runnable) {
			this.scheduledTime = scheduledTime;
			this.runnable = runnable;
		}

		@Override
		public void cancel() {
			assert tasks.remove(this);
		}
	}
}
