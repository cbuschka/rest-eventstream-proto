package com.github.cbuschka.rest_eventstream_proto.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class EventGenerator
{
	private static final Random random = new Random();

	@Autowired
	private EventStore eventStore;

	private int seq = 0;

	@Scheduled(fixedDelay = 1000L)
	public void generateEvent()
	{
		int n = random.nextInt(10000);
		for (int i = 0; i < n; ++i)
		{
			this.eventStore.addEvent(new Event(String.valueOf(seq++)));
		}
		log.info("Generated {} event(s).", n);
	}
}
