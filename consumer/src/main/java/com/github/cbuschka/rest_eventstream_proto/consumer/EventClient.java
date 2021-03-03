package com.github.cbuschka.rest_eventstream_proto.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
public class EventClient
{
	private ObjectMapper objectMapper = new ObjectMapper();

	public List<GetEventsResponse.Event> get(String last, int limit)
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
			GetEventsResponse response = objectMapper.readerFor(GetEventsResponse.class).readValue(in);
			return response.events;
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
			return new URL("http://localhost:8080/events?last=" + last + "&limit=" + limit);
		}
		else
		{
			return new URL("http://localhost:8080/events?limit=" + limit);
		}
	}
}
