package com.github.cbuschka.rest_eventstream_proto.consumer;

import com.github.cbuschka.rest_eventstream_proto.event_feed_importer.PointerStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

public class PointerService implements PointerStore
{
	private final StorageClient storageClient;

	public PointerService(StorageClient storageClient)
	{
		this.storageClient = storageClient;
	}

	public void setPointer(String pointer)
	{
		this.storageClient.post("consumer","pointer",pointer);
	}

	public Optional<String> getPointer()
	{
		return this.storageClient.get("consumer","pointer");
	}
}
