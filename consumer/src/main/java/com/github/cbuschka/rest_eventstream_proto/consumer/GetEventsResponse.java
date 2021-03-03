package com.github.cbuschka.rest_eventstream_proto.consumer;

import lombok.ToString;

import java.util.List;

public class GetEventsResponse
{
	public List<Event> events;

	@ToString
	public static class Event
	{
		public String uuid;
	}
}
