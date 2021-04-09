package com.github.cbuschka.rest_eventstream_proto.polling;

@FunctionalInterface
public interface PollingAction
{
	void poll(PollingStrategy pollingStrategy) throws Exception;
}
