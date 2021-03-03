package com.github.cbuschka.rest_eventstream_proto.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProducerWebApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ProducerWebApplication.class, args);
	}
}
