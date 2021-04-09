package com.github.cbuschka.rest_eventstream_proto.polling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

import java.util.concurrent.ScheduledFuture;

@Slf4j
public class PollingService
{
	private final PollingStrategy pollingStrategy;

	private final PollingAction action;
	private TaskScheduler taskScheduler;
	private ScheduledFuture<?> task;

	public PollingService(PollingStrategy pollingStrategy, PollingAction action, TaskScheduler taskScheduler)
	{
		this.pollingStrategy = pollingStrategy;
		this.action = action;
		this.taskScheduler = taskScheduler;
	}

	public void start()
	{
		this.task = this.taskScheduler.scheduleWithFixedDelay(this::poll, 1000L);
	}

	public void stop()
	{
		if (this.task != null)
		{
			try
			{
				this.task.cancel(true);
			}
			finally
			{
				this.task = null;
			}
		}
	}

	private void poll()
	{
		while (this.pollingStrategy.shouldPoll())
		{
			try
			{
				this.action.poll(this.pollingStrategy);
			}
			catch (Exception ex)
			{
				this.pollingStrategy.failure();

				log.error("Polling failed.", ex);
			}
		}

		log.debug("Next poll in {} milli(s)...", this.pollingStrategy.getMillisUntilNextPoll());
	}
}
