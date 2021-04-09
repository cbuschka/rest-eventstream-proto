package com.github.cbuschka.rest_eventstream_proto.event_feed_importer;

import com.github.cbuschka.rest_eventstream_proto.consumer.GetEventsResponse;
import com.github.cbuschka.rest_eventstream_proto.election.ElectionService;
import com.github.cbuschka.rest_eventstream_proto.polling.PollingAction;
import com.github.cbuschka.rest_eventstream_proto.polling.PollingService;
import com.github.cbuschka.rest_eventstream_proto.polling.PollingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class EventFeedImporterService
{
	private final int limit = 1000;
	private final EventClient eventClient;
	private final PointerStore pointerStore;
	private final PollingService pollingService;
	private final ElectionService electionService;
	private final Consumer<PollingAction> executor;

	public EventFeedImporterService(EventClient eventClient, PointerStore pointerStore, ElectionService electionService, Consumer<PollingAction> executor, TaskScheduler taskScheduler)
	{
		this.eventClient = eventClient;
		this.pointerStore = pointerStore;
		this.electionService = electionService;
		this.executor = executor;
		this.pollingService = new PollingService(new PollingStrategy(), this::wrappedImportEventsIfLeader, taskScheduler);
	}

	@PostConstruct
	public void start()
	{
		this.pollingService.start();
	}

	@PreDestroy
	public void stop()
	{
		this.pollingService.stop();
	}

	private void wrappedImportEventsIfLeader(PollingStrategy pollingStrategy) throws Exception
	{
		if (this.executor != null)
		{
			this.executor.accept(this::importEventsIfLeader);
		}
		else
		{
			importEventsIfLeader(pollingStrategy);
		}
	}

	private void importEventsIfLeader(PollingStrategy pollingStrategy) throws Exception
	{
		if (!electionService.isLeader())
		{
			log.debug("Not leader. Skipping.");
			return;
		}

		Optional<String> optPointer = this.pointerStore.getPointer();
		GetEventsResponse response;
		if (optPointer.isEmpty())
		{
			log.debug("Pointer before the stream.");
			response = this.eventClient.get(null, limit);
		}
		else
		{
			log.debug("Pointer is at {}.", optPointer.get());
			response = this.eventClient.get(optPointer.get(), limit);
		}

		List<GetEventsResponse.Event> events = response.events;
		if (!events.isEmpty())
		{
			log.info("Got {} event(s): {}", events.size(), events);
			this.pointerStore.setPointer(events.get(events.size() - 1).uuid);
			pollingStrategy.received();
			if (response.pendingCount == 0)
			{
				pollingStrategy.empty();
			}
		}
		else
		{
			log.debug("Got no events.");
			pollingStrategy.empty();
		}

		log.info("{} event(s) pending in feed.", response.pendingCount);
	}
}
