package com.github.cbuschka.rest_eventstream_proto.producer;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class EventStore
{
	private final List<Event> events = new LinkedList<>();

	public synchronized void addEvent(Event event)
	{
		this.events.add(event);
	}

	public synchronized List<Event> getEvents(String lastEventOrNull, int limit)
	{
		if (limit < 1) limit = 1;
		if (limit > 100) limit = 100;
		if (lastEventOrNull != null && lastEventOrNull.trim().length() == 0)
		{
			lastEventOrNull = null;
		}

		boolean lastEventFound = lastEventOrNull == null;
		List<Event> next = new ArrayList<>();
		for (Iterator<Event> iter = this.events.iterator(); iter.hasNext(); )
		{
			Event event = iter.next();
			if (next.size() == limit)
			{
				break;
			}

			if (lastEventFound)
			{
				next.add(event);
			}

			lastEventFound = lastEventFound || (lastEventOrNull != null && lastEventOrNull.equals(event.uuid));
		}

		if (lastEventOrNull != null && !lastEventFound)
		{
			throw new IllegalStateException("Last " + lastEventOrNull + " not found.");
		}

		return next;
	}
}
