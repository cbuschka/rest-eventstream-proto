package com.github.cbuschka.rest_eventstream_proto.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PollingService
{
	private final PollingStrategy pollingStrategy = new PollingStrategy();

	private static final int LIMIT = 20;

	@Autowired
	private PointerService pointerService;

	@Autowired
	private EventClient eventClient;

	@Scheduled(fixedDelay = 1000L)
	public void poll()
	{
		while (this.pollingStrategy.shouldPoll())
		{
			try
			{
				doPoll();
			}
			catch (RuntimeException ex)
			{
				this.pollingStrategy.failure();

				log.error("Polling failed.", ex);
			}
		}

		log.debug("Next poll in {} milli(s)...", this.pollingStrategy.getMillisUntilNextPoll());
	}

	private void doPoll()
	{
		Optional<String> optPointer = this.pointerService.getPointer();
		List<GetEventsResponse.Event> events;
		if (optPointer.isEmpty())
		{
			log.debug("Pointer before the stream.");
			events = this.eventClient.get(null, LIMIT);
		}
		else
		{
			log.debug("Pointer is at {}.", optPointer.get());
			events = this.eventClient.get(optPointer.get(), LIMIT);
		}

		if (!events.isEmpty())
		{
			log.info("Got {} event(s): {}", events.size(), events);
			this.pointerService.setPointer(events.get(events.size() - 1).uuid);
			this.pollingStrategy.received();
		}
		else
		{
			log.debug("Got no events.");
			this.pollingStrategy.empty();
		}
	}
}
