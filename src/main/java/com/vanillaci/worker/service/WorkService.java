package com.vanillaci.worker.service;

import com.google.common.collect.*;
import com.vanillaci.plugins.*;
import com.vanillaci.worker.model.*;
import org.apache.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * @author Joel Johnson
 */
@Service
public class WorkService {
	private Logger logger = Logger.getLogger(WorkService.class);

	@Autowired
	private PluginService pluginService;

	public void doWork(WorkMessage workMessage) {
		logger.info("starting work: " + workMessage.getId());
		try {
			Map<String, String> parameters = workMessage.getParameters();

			try {
				List<WorkStepMessage> steps = workMessage.getSteps();
				for (WorkStepMessage step : steps) {
					executeStep(workMessage, step, parameters);
				}
			} finally {
				List<WorkStepMessage> postSteps = workMessage.getPostSteps();
				for (WorkStepMessage postStep : postSteps) {
					try {
						executeStep(workMessage, postStep, parameters);
					} catch (Exception e) {
						logger.info("Exception running post work step: " + workMessage.getId(), e);
					}
				}
			}
		} catch (Exception e) {
			logger.info("Exception running work: " + workMessage.getId(), e);
		}
	}

	private void executeStep(WorkMessage workMessage, WorkStepMessage step, Map<String, String> parameters) {
		Map<String, String> allParameters = ImmutableMap.<String, String>builder()
			.putAll(parameters)
			.putAll(step.getParameters())
			.build();

		WorkStep workStep = pluginService.getWorkStep(step.getName());
		WorkContext workContext = new WorkContextImpl(workMessage, workStep, allParameters);

		workStep.execute(workContext);
	}
}
