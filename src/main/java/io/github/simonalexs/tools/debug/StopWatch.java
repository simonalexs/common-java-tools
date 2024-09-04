package io.github.simonalexs.tools.debug;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Simple stop watch, allowing for timing of a number of tasks, exposing total
 * running time and running time for each named task.
 *
 * <p>Conceals use of {@link System#nanoTime()}, improving the readability of
 * application code and reducing the likelihood of calculation errors.
 *
 * <p>Note that this object is not designed to be thread-safe and does not use
 * synchronization.
 *
 * <p>This class is normally used to verify performance during proof-of-concept
 * work and in development, rather than as part of production applications.
 *
 * <p>As of Spring Framework 5.2, running time is tracked and reported in
 * nanoseconds.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since May 2, 2001
 */
public class StopWatch {

	/**
	 * Identifier of this {@code StopWatch}.
	 * <p>Handy when we have output from multiple stop watches and need to
	 * distinguish between them in log or console output.
	 */
	private final String id;

	private boolean keepTaskList = true;

	private final List<TaskInfo> taskList = new LinkedList<>();

	/** Start time of the current task. */
	private long startTimeNanos;

	/** Name of the current task. */
	private String currentTaskName;

	private TaskInfo lastTaskInfo;

	private int taskCount;

	/** Total running time. */
	private long totalTimeNanos;


	/**
	 * Construct a new {@code StopWatch}.
	 * <p>Does not start any task.
	 */
	public StopWatch() {
		this("");
	}

	/**
	 * Construct a new {@code StopWatch} with the given ID.
	 * <p>The ID is handy when we have output from multiple stop watches and need
	 * to distinguish between them.
	 * <p>Does not start any task.
	 * @param id identifier for this stop watch
	 */
	public StopWatch(String id) {
		this.id = id;
	}


	/**
	 * Get the ID of this {@code StopWatch}, as specified on construction.
	 * @return the ID (empty String by default)
	 * @since 4.2.2
	 * @see #StopWatch(String)
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Configure whether the {@link TaskInfo} array is built over time.
	 * <p>Set this to {@code false} when using a {@code StopWatch} for millions
	 * of intervals; otherwise, the {@code TaskInfo} structure will consume
	 * excessive memory.
	 * <p>Default is {@code true}.
	 * @param keepTaskList 是否保存taskList
	 */
	public void setKeepTaskList(boolean keepTaskList) {
		this.keepTaskList = keepTaskList;
	}


	/**
	 * Start an unnamed task.
	 * <p>The results are undefined if {@link #stop()} or timing methods are
	 * called without invoking this method first.
	 * @see #start(String)
	 * @see #stop()
	 */
	public void start() throws IllegalStateException {
		start("");
	}

	/**
	 * Start a named task.
	 * <p>The results are undefined if {@link #stop()} or timing methods are
	 * called without invoking this method first.
	 * @param taskName the name of the task to start
	 * @see #start()
	 * @see #stop()
	 */
	public void start(String taskName) throws IllegalStateException {
		if (this.currentTaskName != null) {
			throw new IllegalStateException("Can't start StopWatch: it's already running");
		}
		this.currentTaskName = taskName;
		this.startTimeNanos = System.nanoTime();
	}

	/**
	 * Stop the current task.
	 * <p>The results are undefined if timing methods are called without invoking
	 * at least one pair of {@code start()} / {@code stop()} methods.
	 * @see #start()
	 * @see #start(String)
	 */
	public void stop() throws IllegalStateException {
		if (this.currentTaskName == null) {
			throw new IllegalStateException("Can't stop StopWatch: it's not running");
		}
		long lastTime = System.nanoTime() - this.startTimeNanos;
		this.totalTimeNanos += lastTime;
		this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
		if (this.keepTaskList) {
			this.taskList.add(this.lastTaskInfo);
		}
		++this.taskCount;
		this.currentTaskName = null;
	}

	/**
	 * Determine whether this {@code StopWatch} is currently running.
	 * @return 是否正在运行
	 * @see #currentTaskName()
	 */
	public boolean isRunning() {
		return (this.currentTaskName != null);
	}

	public String currentTaskName() {
		return this.currentTaskName;
	}

	public long getLastTaskTimeNanos() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task interval");
		}
		return this.lastTaskInfo.getTimeNanos();
	}

	public long getLastTaskTimeMillis() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task interval");
		}
		return this.lastTaskInfo.getTimeMillis();
	}

	public String getLastTaskName() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task name");
		}
		return this.lastTaskInfo.getTaskName();
	}

	public TaskInfo getLastTaskInfo() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task info");
		}
		return this.lastTaskInfo;
	}


	public long getTotalTimeNanos() {
		return this.totalTimeNanos;
	}

	public long getTotalTimeMillis() {
		return nanosToMillis(this.totalTimeNanos);
	}

	public double getTotalTimeSeconds() {
		return nanosToSeconds(this.totalTimeNanos);
	}

	public int getTaskCount() {
		return this.taskCount;
	}

	public TaskInfo[] getTaskInfo() {
		if (!this.keepTaskList) {
			throw new UnsupportedOperationException("Task info is not being kept!");
		}
		return this.taskList.toArray(new TaskInfo[0]);
	}

	public String shortSummary() {
		return "StopWatch '" + getId() + "': running time = " + getTotalTimeNanos() + " ns";
	}

	public String prettyPrint() {
		StringBuilder sb = new StringBuilder(shortSummary());
		sb.append('\n');
		if (!this.keepTaskList) {
			sb.append("No task info kept");
		}
		else {
			sb.append("---------------------------------------------\n");
			sb.append("ns         %     Task name\n");
			sb.append("---------------------------------------------\n");
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMinimumIntegerDigits(9);
			nf.setGroupingUsed(false);
			NumberFormat pf = NumberFormat.getPercentInstance();
			pf.setMinimumIntegerDigits(3);
			pf.setGroupingUsed(false);
			for (TaskInfo task : getTaskInfo()) {
				sb.append(nf.format(task.getTimeNanos())).append("  ");
				sb.append(pf.format((double) task.getTimeNanos() / getTotalTimeNanos())).append("  ");
				sb.append(task.getTaskName()).append("\n");
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(shortSummary());
		if (this.keepTaskList) {
			for (TaskInfo task : getTaskInfo()) {
				sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeNanos()).append(" ns");
				long percent = Math.round(100.0 * task.getTimeNanos() / getTotalTimeNanos());
				sb.append(" = ").append(percent).append("%");
			}
		}
		else {
			sb.append("; no task info kept");
		}
		return sb.toString();
	}


	private static long nanosToMillis(long duration) {
		return TimeUnit.NANOSECONDS.toMillis(duration);
	}

	private static double nanosToSeconds(long duration) {
		return duration / 1_000_000_000.0;
	}


	/**
	 * Nested class to hold data about one task executed within the {@code StopWatch}.
	 */
	public static final class TaskInfo {

		private final String taskName;

		private final long timeNanos;

		TaskInfo(String taskName, long timeNanos) {
			this.taskName = taskName;
			this.timeNanos = timeNanos;
		}

		public String getTaskName() {
			return this.taskName;
		}

		public long getTimeNanos() {
			return this.timeNanos;
		}

		public long getTimeMillis() {
			return nanosToMillis(this.timeNanos);
		}

		public double getTimeSeconds() {
			return nanosToSeconds(this.timeNanos);
		}
	}
}
