package com.github.cbuschka.rest_eventstream_proto.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ConsumerWebApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ConsumerWebApplication.class, args);
	}
}
