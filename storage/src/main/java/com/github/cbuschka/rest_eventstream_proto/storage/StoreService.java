package com.github.cbuschka.rest_eventstream_proto.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class StoreService
{
	private final Map<String, Map<String, Entry>> bucketMap = new HashMap<>();

	@Getter
	@AllArgsConstructor
	public static class Entry
	{
		private final String contentType;
		private final byte[] data;

		@Override
		public String toString()
		{
			if ("text/plain; charset=UTF-8".equals(this.contentType))
			{
				return "Entry{" +
						"contentType='" + contentType + '\'' +
						", data=" + new String(this.data, StandardCharsets.UTF_8) +
						'}';
			}
			else
			{
				return "Entry{" +
						"contentType='" + contentType + '\'' +
						", data=" + Arrays.toString(data) +
						'}';
			}
		}
	}

	@PostConstruct
	public void init()
	{
		bucketMap.put("default", new HashMap<>());
	}

	public synchronized Optional<Entry> get(String bucketName, String objectName)
	{
		Map<String, Entry> objectMap = this.bucketMap.get(bucketName);
		if (objectMap == null)
		{
			log.info("Get for {}:{} => none.", bucketName, objectName);

			return Optional.empty();
		}

		Entry entry = objectMap.get(objectName);
		if (entry == null)
		{
			log.info("Get for {}:{} => none.", bucketName, objectName);

			return Optional.empty();
		}

		log.info("Get for {}:{} => {}.", bucketName, objectName, entry);

		return Optional.of(new Entry(entry.contentType, entry.data));
	}

	public synchronized void put(String bucketName, String objectName, String contentType, byte[] data)
	{
		Map<String, Entry> objectMap = getOrCreateBucket(bucketName);
		Entry entry = new Entry(contentType, data);
		objectMap.put(objectName, entry);

		log.info("Put of {}:{} => {}.", bucketName, objectName, entry);
	}

	private Map<String, Entry> getOrCreateBucket(String bucketName)
	{
		Map<String, Entry> objectMap = this.bucketMap.computeIfAbsent(bucketName, (x) -> new HashMap<>());
		this.bucketMap.put(bucketName, objectMap);
		return objectMap;
	}

	public synchronized void delete(String bucketName, String objectPath)
	{
		Map<String, Entry> objectMap = this.bucketMap.get(bucketName);
		if (objectMap != null)
		{
			objectMap.remove(objectPath);
		}

		log.info("Delete of {}:{}.", bucketName, objectPath);
	}
}
