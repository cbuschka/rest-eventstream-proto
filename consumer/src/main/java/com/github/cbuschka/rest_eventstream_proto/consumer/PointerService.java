package com.github.cbuschka.rest_eventstream_proto.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PointerService
{
	@Autowired
	private StorageClient storageClient;

	private String pointer;

	public void setPointer(String pointer)
	{
		this.storageClient.post("consumer","pointer",pointer);
	}

	public Optional<String> getPointer()
	{
		return this.storageClient.get("consumer","pointer");
	}
}
