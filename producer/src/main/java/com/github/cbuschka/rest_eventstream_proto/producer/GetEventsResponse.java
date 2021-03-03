package com.github.cbuschka.rest_eventstream_proto.producer;

import java.util.List;

public class GetEventsResponse
{
	public List<Event> events;

	public GetEventsResponse(List<Event> events)
	{
		this.events = events;
	}
}
