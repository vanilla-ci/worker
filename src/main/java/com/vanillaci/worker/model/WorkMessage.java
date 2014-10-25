package com.vanillaci.worker.model;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.*;

import java.util.*;

/**
 * @author Joel Johnson
 */
public class WorkMessage {
	private final String id;
	private final Map<String, String> parameters;
	private final List<WorkStepMessage> steps;
	private final List<WorkStepMessage> postSteps;

	public WorkMessage(
		@JsonProperty(value = "id", required = true) String id,
		@JsonProperty(value = "parameters", required = false) Map<String, String> parameters,
		@JsonProperty(value = "steps", required = false) List<WorkStepMessage> steps,
		@JsonProperty(value = "postSteps", required = false) List<WorkStepMessage> postSteps
	) {
		this.id = id;
		this.parameters = parameters == null ? ImmutableMap.of() : ImmutableMap.copyOf(parameters);
		this.steps = steps == null ? ImmutableList.of() : ImmutableList.copyOf(steps);
		this.postSteps = postSteps == null ? ImmutableList.of() : ImmutableList.copyOf(postSteps);
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public List<WorkStepMessage> getSteps() {
		return steps;
	}

	public List<WorkStepMessage> getPostSteps() {
		return postSteps;
	}
}
