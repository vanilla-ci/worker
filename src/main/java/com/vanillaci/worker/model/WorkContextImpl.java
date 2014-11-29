package com.vanillaci.worker.model;

import com.vanillaci.plugins.*;

import java.io.*;
import java.util.*;

/**
 * @author Joel Johnson
 */
public class WorkContextImpl implements WorkContext {
	private final WorkMessage workMessage;
	private final File workingDirectory;

	private final Map<String, String> addedParameters;

	private WorkStep workStep;
	private WorkStepMessage workStepMessage;

	private int currentWorkStep;
	private final int numberWorkSteps;

	private int currentPostWorkStep;
	private final int numberPostWorkSteps;
	private WorkPhase workPhase;

	private boolean terminated;
	private WorkStatus workStatus;

	public WorkContextImpl(WorkMessage workMessage, File workingDirectory) {
		this.workMessage = workMessage;
		this.workingDirectory = workingDirectory;

		this.addedParameters = new HashMap<>();

		this.currentWorkStep = 0;
		this.numberWorkSteps = workMessage.getSteps() != null ? workMessage.getSteps().size() : 0;

		this.currentPostWorkStep = 0;
		this.numberPostWorkSteps = workMessage.getSteps() != null ? workMessage.getPostSteps().size() : 0;

		workPhase = WorkPhase.STEPS;

		terminated = false;
		workStatus = WorkStatus.SUCCESS;
	}

	@Override
	public String getWorkId() {
		return workMessage.getId();
	}

	public WorkMessage getWorkMessage() {
		return workMessage;
	}

	@Override
	public String getParameter(String parameterName) {
		String result;

		result = addedParameters.get(parameterName);
		if(result == null && getWorkStep() != null && getWorkStepMessage().getParameters() != null) {
			result = getWorkStepMessage().getParameters().get(parameterName);
		}

		if(result == null && workMessage.getParameters() != null) {
			result = workMessage.getParameters().get(parameterName);
		}

		return result;
	}

	@Override
	public void addParameter(String parameterName, String parameterValue) {
		addedParameters.put(parameterName, parameterValue);
	}

	@Override
	public WorkStep getWorkStep() {
		return workStep;
	}

	@Override
	public void setWorkStep(WorkStep workStep) {
		this.workStep = workStep;
	}

	public WorkStepMessage getWorkStepMessage() {
		return workStepMessage;
	}

	public void setWorkStepMessage(WorkStepMessage workStepMessage) {
		this.workStepMessage = workStepMessage;
	}

	@Override
	public WorkStatus getWorkStatus() {
		return workStatus;
	}

	@Override
	public void setWorkStatus(WorkStatus workStatus) {
		if(workStatus != null && this.workStatus.isLessSevereThan(workStatus)) {
			overrideWorkStatus(workStatus);
		}
	}

	@Override
	public void overrideWorkStatus(WorkStatus workStatus) {
		if(workStatus != null) {
			this.workStatus = workStatus;
		}
	}

	@Override
	public boolean getTerminated() {
		return terminated;
	}

	@Override
	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}

	@Override
	public int getCurrentStep() {
		return currentWorkStep;
	}

	public void incrementCurrentStep() {
		currentWorkStep++;
	}

	@Override
	public int getTotalSteps() {
		return numberWorkSteps;
	}

	@Override
	public int getCurrentPostStep() {
		return currentPostWorkStep;
	}

	public void incrementCurrentPostStep() {
		currentPostWorkStep++;
	}

	@Override
	public int getTotalPostSteps() {
		return numberPostWorkSteps;
	}

	@Override
	public WorkPhase getWorkPhase() {
		return workPhase;
	}

	public void setWorkPhase(WorkPhase workPhase) {
		this.workPhase = workPhase;
	}

	@Override
	public File getWorkingDirectory() {
		return workingDirectory;
	}
}
