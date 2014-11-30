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

	@Value("${home.directory}")
	private String homeDirectory;

	@Value("${number.concurrent.work}")
	private int numberConcurrentWork;

	public String getQueueName() {
		return workQueueName;
	}

	public String getHomeDirectory() {
		return homeDirectory;
	}

	public int getNumberConcurrentWork() {
		return numberConcurrentWork;
	}
}
