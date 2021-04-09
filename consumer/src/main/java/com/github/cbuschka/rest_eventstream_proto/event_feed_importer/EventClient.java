package com.github.cbuschka.rest_eventstream_proto.event_feed_importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cbuschka.rest_eventstream_proto.consumer.GetEventsResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class EventClient
{
	private String url;

	private ObjectMapper objectMapper = new ObjectMapper();

	public EventClient(String url)
	{
		this.url = url;
	}

	public GetEventsResponse get(String last, int limit)
	{
		HttpURLConnection httpURLConnection = null;
		try
		{
			httpURLConnection = (HttpURLConnection) buildUrl(last, limit).openConnection();
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode < 200 || responseCode > 299)
			{
				throw new IOException("Failed: " + responseCode);
			}
			InputStream in = httpURLConnection.getInputStream();
			return objectMapper.readerFor(GetEventsResponse.class).readValue(in);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
		finally
		{
			if (httpURLConnection != null)
			{
				httpURLConnection.disconnect();
			}
		}
	}

	private URL buildUrl(String last, int limit) throws MalformedURLException
	{
		if (last != null)
		{
			return new URL(this.url + "?last=" + last + "&limit=" + limit);
		}
		else
		{
			return new URL(this.url + "?limit=" + limit);
		}
	}
}
