package com.github.cbuschka.rest_eventstream_proto.consumer;

import java.util.function.Supplier;

public class PollingStrategy
{
	private final Supplier<Long> now = System::currentTimeMillis;

	private final long maxSleepMillis = 10 * 1000;
	private final long minSleepMillis = 250;

	private long currentSleepMillis = 0L;
	private long lastPollMillis = 0L;

	public void received()
	{
		this.currentSleepMillis = 0;
		this.lastPollMillis = now.get();
	}

	public void failure()
	{
		this.lastPollMillis = now.get();
		empty();
	}

	public void empty()
	{
		this.lastPollMillis = now.get();
		this.currentSleepMillis = Math.max(Math.min(this.currentSleepMillis * 2, maxSleepMillis), minSleepMillis);
	}

	public boolean shouldPoll()
	{
		return getMillisUntilNextPoll() == 0;
	}

	public long getMillisUntilNextPoll()
	{
		return Math.max(this.lastPollMillis + this.currentSleepMillis - now.get(), 0);
	}
}
