package com.vanillaci.worker;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

/**
 * @author Joel Johnson
 */
@Configuration
public class AppConfiguration {
	@Value("${work.queue.name}")
	private String workQueueName;

	public String getQueueName() {
		return workQueueName;
	}
}
