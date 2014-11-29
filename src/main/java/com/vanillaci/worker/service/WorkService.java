package com.vanillaci.worker.service;

import com.vanillaci.plugins.*;
import com.vanillaci.worker.*;
import com.vanillaci.worker.model.*;
import org.apache.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

/**
 * @author Joel Johnson
 */
@Service
public class WorkService {
	private Logger logger = Logger.getLogger(WorkService.class);

	@Autowired
	private PluginService pluginService;

	@Autowired
	private AppConfiguration appConfiguration;

	public WorkContext doWork(WorkMessage workMessage) {
		final File workingDirectory = createWorkingDirectory(workMessage);
		WorkContextImpl workContext = new WorkContextImpl(workMessage, workingDirectory);

		// Execute Work Lifecycle
		started(workContext);
		runSteps(workContext);
		runPostSteps(workContext);
		finished(workContext);

		return workContext;
	}

	private void started(WorkContextImpl workContext) {
		logger.info("Started work " + workContext.getWorkId());
	}

	private void runSteps(WorkContextImpl workContext) {
		workContext.setWorkPhase(WorkPhase.STEPS);

		List<WorkStepMessage> stepMessages = workContext.getWorkMessage().getSteps();
		for (WorkStepMessage workStepMessage : stepMessages) {
			workContext.incrementCurrentStep();
			executeStep(workContext, workStepMessage);

			if(workContext.getTerminate()) {
				return;
			}
		}
	}

	private void runPostSteps(WorkContextImpl workContext) {
		workContext.setWorkPhase(WorkPhase.POST_STEPS);

		List<WorkStepMessage> postStepMessages = workContext.getWorkMessage().getPostSteps();
		for (WorkStepMessage postWorkStepMessage : postStepMessages) {
			workContext.incrementCurrentPostStep();
			executeStep(workContext, postWorkStepMessage);
		}
	}

	private void executeStep(WorkContextImpl workContext, WorkStepMessage workStepMessage) {
		WorkStep workStep = pluginService.getWorkStep(workStepMessage.getName());
		workContext.setWorkStepMessage(workStepMessage);
		workContext.setWorkStep(workStep);

		logger.info("Executing step " +
			"(" + workContext.getCurrentStep() + "/" + workContext.getTotalSteps() + ") " +
			"(" + workContext.getCurrentPostStep() + "/" + workContext.getTotalPostSteps() + ") " +
			workStepMessage.getName() + " for work: " + workContext.getWorkId()
		);

		try {
			executeBefores(workContext);
			if(workContext.getWorkPhase() == WorkPhase.POST_STEPS || !workContext.getTerminate()) {
				workStep.execute(workContext);
			}
		} catch (Exception e) {
			logger.info("Unexpected error while executing work workContext.getWorkId(), step: " + workStepMessage.getName(), e);
			workContext.setWorkStatus(WorkStatus.UNEXPECTED_ERROR);
			workContext.setTerminate(true);
		} finally {
			executeAfters(workContext);
		}
	}

	private void executeBefores(WorkContextImpl workContext) {
		logger.info("Running befores for " + workContext.getWorkId() + " step " + workContext.getWorkStepMessage().getName());

		Iterable<WorkStepInterceptor> workStepInterceptors = pluginService.getWorkStepInterceptors();
		for (WorkStepInterceptor workStepInterceptor : workStepInterceptors) {
			try {
				workStepInterceptor.before(workContext);
			} catch (Exception e) {
				logger.info("Unexpected error while executing work before step workContext.getWorkId(), interceptor: " + workStepInterceptor.getClass().getName(), e);
				workContext.setWorkStatus(WorkStatus.UNEXPECTED_ERROR);
				workContext.setTerminate(true);
			}
		}
	}

	private void executeAfters(WorkContextImpl workContext) {
		logger.info("Running afters for " + workContext.getWorkId() + " step " + workContext.getWorkStepMessage().getName());

		Iterable<WorkStepInterceptor> workStepInterceptors = pluginService.getWorkStepInterceptors();
		for (WorkStepInterceptor workStepInterceptor : workStepInterceptors) {
			try {
				workStepInterceptor.after(workContext);
			} catch (Exception e) {
				logger.info("Unexpected error while executing work after step workContext.getWorkId(), interceptor: " + workStepInterceptor.getClass().getName(), e);
				workContext.setWorkStatus(WorkStatus.UNEXPECTED_ERROR);
				workContext.setTerminate(true);
			}
		}
	}

	private void finished(WorkContextImpl workContext) {
		logger.info("Completed work " + workContext.getWorkId());
	}

	private File createWorkingDirectory(WorkMessage workMessage) {
		String directoryName = workMessage.getId();
		directoryName = directoryName.replaceAll("[^A-Za-z0-9_-]", "_");

		String homeDirectoryString = appConfiguration.getHomeDirectory();
		File homeDirectory = new File(homeDirectoryString);

		if(!homeDirectory.exists() && !homeDirectory.mkdirs()) {
			throw new IllegalStateException("Unable to create home directory: " + homeDirectory.getAbsolutePath());
		}

		File workingDirectory = new File(homeDirectory, directoryName);
		if(!workingDirectory.exists() && !workingDirectory.mkdirs()) {
			throw new IllegalStateException("Unable to create working directory: " + homeDirectory.getAbsolutePath());
		}

		logger.info("created working directory for " + workMessage.getId() + " at: " + workingDirectory.getAbsolutePath());
		return workingDirectory;
	}
}
