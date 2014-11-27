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
			Map<String, String> workParameters = workMessage.getParameters();
			Map<String, String> addedParameters = new HashMap<>();

			Status status = new Status();
			status.workStatus = WorkStatus.SUCCESS;
			status.terminate = false;

			try {
				List<WorkStepMessage> steps = workMessage.getSteps();
				for (WorkStepMessage step : steps) {
					try {
						executeStep(workMessage, step, workParameters, addedParameters, status);
					} catch (Exception e) {
						logger.info("Exception running work step: " + workMessage.getId() + " " + step.getName(), e);
						status.setWorkStatus(WorkStatus.ERROR);
						status.setTerminate(true);
					}

					if(status.terminate) {
						logger.info("Terminating");
						break;
					}
				}
			} finally {
				logger.info("Running post steps: " + workMessage.getId());

				List<WorkStepMessage> postSteps = workMessage.getPostSteps();
				for (WorkStepMessage postStep : postSteps) {
					try {
						executeStep(workMessage, postStep, workParameters, addedParameters, status);
					} catch (Exception e) {
						logger.info("Exception running post work step: " + workMessage.getId() + " " + postStep.getName(), e);
						status.setWorkStatus(WorkStatus.UNEXPECTED_ERROR);
					}
				}
			}
		} catch (Exception e) {
			logger.info("Exception running work: " + workMessage.getId(), e);
		}
	}

	private void executeStep(WorkMessage workMessage, WorkStepMessage step, Map<String, String> workParameters, Map<String, String> addedParameters, Status status) {
		Map<String, String> stepParameters = step.getParameters();

		Map<String, String> allParameters = new HashMap<>();
		allParameters.putAll(workParameters);
		allParameters.putAll(stepParameters);
		allParameters.putAll(addedParameters);

		WorkStep workStep = pluginService.getWorkStep(step.getName());
		WorkContext workContext = new WorkContextImpl(workMessage, workStep, allParameters, addedParameters, status);

		Iterable<WorkStepInterceptor> workStepInterceptors = pluginService.getWorkStepInterceptors();

		runBefores(workContext, workStepInterceptors);

		try {
			// Make sure the before didn't terminate the work
			if(!workContext.getTerminate()) {
				// Since the befores are allowed to change the step that is actually executed, make sure we execute that one.
				WorkStep overwrittenStep = workContext.getWorkStep();
				if(overwrittenStep != workStep) {
					logger.info("Work step overwritten: " + workMessage.getId());
				}

				if(overwrittenStep != null) {
					overwrittenStep.execute(workContext);
				}
			}
		} catch (Exception e) {
			workContext.setWorkStatus(WorkStatus.ERROR);
			workContext.setTerminate(true);
			throw e;
		} finally {
			runAfters(workContext, workStepInterceptors);
		}
	}

	private void runBefores(WorkContext workContext, Iterable<WorkStepInterceptor> workStepInterceptors) {
		for (WorkStepInterceptor workStepInterceptor : workStepInterceptors) {
			try {
				workStepInterceptor.before(workContext);
			} catch (Exception e) {
				logger.info("Exception running before " + workStepInterceptor.getClass().getName() + " for step " + workContext.getWorkStep().getClass().getName() + ": " + workContext.getWorkId(), e);
				workContext.setWorkStatus(WorkStatus.ERROR);
				workContext.setTerminate(true);
			}
		}
	}

	private void runAfters(WorkContext workContext, Iterable<WorkStepInterceptor> workStepInterceptors) {
		for (WorkStepInterceptor workStepInterceptor : workStepInterceptors) {
			try {
				workStepInterceptor.after(workContext);
			} catch (Exception e) {
				logger.info("Exception running after " + workStepInterceptor.getClass().getName() + " for step " + workContext.getWorkStep().getClass().getName() + ": " + workContext.getWorkId(), e);
				workContext.setWorkStatus(WorkStatus.ERROR);
				workContext.setTerminate(true);
			}
		}
	}

	/**
	 * Mutable object representing the work's current status.
	 */
	public static class Status {
		private WorkStatus workStatus;
		private boolean terminate;

		public WorkStatus getWorkStatus() {
			return workStatus;
		}

		public void setWorkStatus(WorkStatus workStatus) {
			this.workStatus = workStatus;
		}

		public boolean getTerminate() {
			return terminate;
		}

		public void setTerminate(boolean terminate) {
			this.terminate = terminate;
		}
	}
}
