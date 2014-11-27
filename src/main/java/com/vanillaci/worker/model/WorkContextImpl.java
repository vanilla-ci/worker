package com.vanillaci.worker.model;

import com.google.common.collect.*;
import com.vanillaci.plugins.*;
import com.vanillaci.worker.service.*;
import jdk.nashorn.internal.ir.annotations.*;

import java.util.*;

/**
 * @author Joel Johnson
 */
public class WorkContextImpl implements WorkContext {
	private final WorkMessage workMessage;
	private final Map<String, String> parameters;
	private final Map<String, String> addedParameters;
	private WorkService.Status workStatus;
	private WorkStep workStep;

	public WorkContextImpl(WorkMessage workMessage, WorkStep workStep, Map<String, String> parameters, Map<String, String> addedParameters, WorkService.Status currentStatus) {
		this.workMessage = workMessage;
		this.workStep = workStep;
		this.parameters = parameters;
		this.addedParameters = addedParameters;
		this.workStatus = currentStatus;
	}

	@Override
	public String getWorkId() {
		return workMessage.getId();
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	public void addParameter(String parameterName, String parameterValue) {
		parameters.put(parameterName, parameterValue);
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

	@Override
	public WorkStatus getWorkStatus() {
		return workStatus.getWorkStatus();
	}

	@Override
	public void setWorkStatus(WorkStatus workStatus) {
		if(getWorkStatus().isLessSevereThan(workStatus)) {
			this.workStatus.setWorkStatus(workStatus);
		}
	}

	@Override
	public void overrideWorkStatus(WorkStatus workStatus) {
		this.workStatus.setWorkStatus(workStatus);
	}

	@Override
	public boolean getTerminate() {
		return workStatus.getTerminate();
	}

	@Override
	public void setTerminate(boolean terminate) {
		workStatus.setTerminate(terminate);
	}
}
