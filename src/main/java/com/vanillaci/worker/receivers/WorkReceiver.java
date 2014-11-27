package com.vanillaci.worker.receivers;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.vanillaci.worker.model.*;
import com.vanillaci.worker.service.*;
import org.apache.log4j.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;

/**
 * @author Joel Johnson
 */
public class WorkReceiver {
	private Logger logger = Logger.getLogger(WorkReceiver.class);

	@Autowired
	private WorkService workService;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void receiveMessage(String rawMessage) {
		WorkMessage message;
		try {
			message = objectMapper.readValue(rawMessage, new TypeReference<WorkMessage>() {});
		} catch (Exception e) {
			logger.fatal("Error while parsing work message", e);
			return;
		}

		try {
			workService.doWork(message);
		} catch (Exception e) {
			logger.fatal("Unhandled exception while executing work " + message.getId() + ". This is a bug and should be reported.", e);
		}
	}
}
