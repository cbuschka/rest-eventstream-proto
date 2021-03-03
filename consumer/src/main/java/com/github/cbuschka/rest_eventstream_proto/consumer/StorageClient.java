package com.github.cbuschka.rest_eventstream_proto.consumer;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class StorageClient
{
	public Optional<String> get(String bucketName, String objectName)
	{
		HttpURLConnection httpURLConnection = null;
		try
		{
			httpURLConnection = (HttpURLConnection) buildUrl(bucketName, objectName).openConnection();
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == 404)
			{
				return Optional.empty();
			}
			InputStream in = httpURLConnection.getInputStream();
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			byte[] bbuf = new byte[1024];
			int count;
			while ((count = in.read(bbuf, 0, bbuf.length)) > -1)
			{
				bytesOut.write(bbuf, 0, count);
			}
			if (responseCode < 200 || responseCode > 299)
			{
				throw new IOException("Failed: " + responseCode);
			}
			return Optional.of(bytesOut.toString(StandardCharsets.UTF_8));
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

	public void post(String bucketName, String objectName, String value)
	{
		HttpURLConnection httpURLConnection = null;
		try
		{
			httpURLConnection = (HttpURLConnection) buildUrl(bucketName, objectName).openConnection();
			httpURLConnection.addRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			httpURLConnection.setRequestMethod("PUT");
			httpURLConnection.setDoOutput(true);
			OutputStream out = httpURLConnection.getOutputStream();
			out.write(value.getBytes(StandardCharsets.UTF_8));
			out.flush();
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode < 200 || responseCode > 299)
			{
				throw new IOException("Failed: " + responseCode);
			}
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

	private URL buildUrl(String bucketName, String objectName) throws MalformedURLException
	{
		return new URL("http://localhost:8082/buckets/" + bucketName + "/objects/" + objectName);
	}
}
