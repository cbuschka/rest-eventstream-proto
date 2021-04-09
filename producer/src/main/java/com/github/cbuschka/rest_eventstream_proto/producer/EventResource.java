package com.github.cbuschka.rest_eventstream_proto.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventResource
{
	@Autowired
	private EventStore eventStore;

	@GetMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetEventsResponse> getEvents(@RequestParam(name = "last", required = false) String lastEvent,
													   @RequestParam(name = "limit", required = false, defaultValue = "100") int limit)
	{
		GetEventsResponse response = this.eventStore.getEvents(lastEvent, limit);
		return ResponseEntity.ok(response);
	}
}
