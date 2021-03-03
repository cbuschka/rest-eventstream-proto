package com.github.cbuschka.rest_eventstream_proto.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Controller
public class ObjectResource
{
	@Autowired
	private StoreService storeService;

	@PostMapping(value = "/buckets/{bucketName}/objects/**", consumes = MediaType.ALL_VALUE)
	public ResponseEntity<?> post(@PathVariable("bucketName") String bucketName, @RequestBody byte[] data, HttpServletRequest request) throws URISyntaxException
	{
		String contentType = request.getContentType();
		if (contentType == null)
		{
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}

		String objectPath = getObjectPath(request);
		this.storeService.put(bucketName, objectPath, contentType, data);
		return ResponseEntity.created(new URI(request.getRequestURI())).build();
	}

	private String getObjectPath(HttpServletRequest request)
	{
		String requestURI = request.getRequestURI();
		return requestURI.substring(requestURI.indexOf("/objects/") + "/objects/".length());
	}

	@PutMapping(value = "/buckets/{bucketName}/objects/**", consumes = MediaType.ALL_VALUE)
	public ResponseEntity<?> put(@PathVariable("bucketName") String bucketName, @RequestBody byte[] data, HttpServletRequest request) throws URISyntaxException
	{
		String contentType = request.getContentType();
		if (contentType == null)
		{
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}

		String objectPath = getObjectPath(request);
		this.storeService.put(bucketName, objectPath, contentType, data);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/buckets/{bucketName}/objects/**")
	public ResponseEntity<?> get(@PathVariable("bucketName") String bucketName, HttpServletRequest request) throws URISyntaxException
	{
		String objectPath = getObjectPath(request);
		Optional<StoreService.Entry> optEvent = this.storeService.get(bucketName, objectPath);
		if (optEvent.isEmpty())
		{
			return ResponseEntity.notFound().build();
		}
		StoreService.Entry entry = optEvent.get();
		return ResponseEntity.ok().headers((h) -> h.setContentType(MediaType.valueOf(entry.getContentType()))).body(entry.getData());
	}

	@DeleteMapping(value = "/buckets/{bucketName}/objects/**")
	public ResponseEntity<?> delete(@PathVariable("bucketName") String bucketName, HttpServletRequest request) throws URISyntaxException
	{
		String objectPath = getObjectPath(request);
		this.storeService.delete(bucketName, objectPath);
		return ResponseEntity.noContent().build();
	}

}
