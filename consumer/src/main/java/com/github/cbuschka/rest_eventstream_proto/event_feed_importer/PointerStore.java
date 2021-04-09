package com.github.cbuschka.rest_eventstream_proto.event_feed_importer;

import java.util.Optional;

public interface PointerStore
{
	void setPointer(String pointer);

	Optional<String> getPointer();
}
