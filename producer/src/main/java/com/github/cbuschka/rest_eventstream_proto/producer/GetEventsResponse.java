package com.github.cbuschka.rest_eventstream_proto.producer;

import java.util.List;

public class GetEventsResponse
{
	public int pendingCount;

	public List<Event> events;

	public GetEventsResponse(List<Event> events, int pendingCount)
	{
		this.events = events;
		this.pendingCount = pendingCount;
	}
}
