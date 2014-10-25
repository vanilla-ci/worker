package com.vanillaci.worker.model;

import com.google.common.collect.*;
import com.vanillaci.plugins.*;
import jdk.nashorn.internal.ir.annotations.*;

import java.util.*;

/**
 * @author Joel Johnson
 */
public class WorkContextImpl implements WorkContext {
	private final WorkMessage workMessage;
	private final Map<String, String> parameters;
	private WorkStep workStep;

	public WorkContextImpl(WorkMessage workMessage, WorkStep workStep, Map<String, String> parameters) {
		this.workMessage = workMessage;
		this.workStep = workStep;
		this.parameters = ImmutableMap.copyOf(parameters);
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
		throw new UnsupportedOperationException("I should implement this now");
	}

	@Override
	public WorkStep getWorkStep() {
		return workStep;
	}

	@Override
	public void setWorkStep(WorkStep workStep) {
		this.workStep = workStep;
	}
}
