package com.github.cbuschka.rest_eventstream_proto.election;

import org.springframework.stereotype.Service;

@Service
public class ElectionService
{
	public boolean isLeader()
	{
		return true;
	}
}
