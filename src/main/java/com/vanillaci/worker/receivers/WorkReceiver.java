package com.vanillaci.worker.receivers;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.vanillaci.worker.model.*;
import com.vanillaci.worker.service.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;

/**
 * @author Joel Johnson
 */
public class WorkReceiver {
	@Autowired
	private WorkService workService;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void receiveMessage(String rawMessage) throws IOException {
		WorkMessage message = objectMapper.readValue(rawMessage, new TypeReference<WorkMessage>() {});
		workService.doWork(message);
	}
}
