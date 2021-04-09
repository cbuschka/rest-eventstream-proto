package com.github.cbuschka.rest_eventstream_proto.consumer;

import com.github.cbuschka.rest_eventstream_proto.election.ElectionService;
import com.github.cbuschka.rest_eventstream_proto.event_feed_importer.EventClient;
import com.github.cbuschka.rest_eventstream_proto.event_feed_importer.EventFeedImporterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
public class ConsumerServiceConfig
{
	@Bean
	public StorageClient storageClient()
	{
		return new StorageClient();
	}

	@Bean
	public PointerService pointerService()
	{
		return new PointerService(storageClient());
	}

	@Bean
	public EventFeedImporterService eventFeedImporterService(PointerService pointerService, TaskScheduler taskScheduler)
	{
		return new EventFeedImporterService(new EventClient("http://localhost:8080/events"), pointerService, getElectionService(),
				null, taskScheduler);
	}

	@Bean
	public ElectionService getElectionService()
	{
		return new ElectionService();
	}
}
