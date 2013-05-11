package streckenplan.sched;

import java.util.*;

import org.jetbrains.annotations.NotNull;

final class SchedulerImpl implements Scheduler {
	private double currentTime = 0;
	private int taskSequence = 0;
	
	private final SortedSet<TaskImpl> tasks = new TreeSet<TaskImpl>();

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
		TaskImpl task = new TaskImpl(currentTime + delay, taskSequence, runnable);

		taskSequence += 1;
		tasks.add(task);

		return task;
	}

	@Override
	public double getTime() {
		return currentTime;
	}

	private final class TaskImpl implements Task, Comparable<TaskImpl> {
		private final double scheduledTime;
		private final int sequence;
		private final Runnable runnable;

		private TaskImpl(double scheduledTime, int sequence, Runnable runnable) {
			this.scheduledTime = scheduledTime;
			this.sequence = sequence;
			this.runnable = runnable;
		}

		@Override
		public void cancel() {
			assert tasks.remove(this);
		}

		@Override
		public int compareTo(@NotNull TaskImpl o) {
			if (scheduledTime < o.scheduledTime)
				return -1;
			else if (scheduledTime > o.scheduledTime)
				return 1;
			else if (sequence < o.sequence)
				return -1;
			else if (sequence > o.sequence)
				return 1;
			else
				return 0;
		}

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		@Override
		public boolean equals(Object obj) {
			return compareTo((TaskImpl) obj) == 0;
		}
	}
}
