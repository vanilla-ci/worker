package com.vanillaci.worker.model;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.*;

import java.util.*;

/**
 * @author Joel Johnson
 */
public class WorkStepMessage {
	private final String name;
	private final Map<String, String> parameters;

	public WorkStepMessage(
		@JsonProperty(value = "name", required = true) String name,
		@JsonProperty(value = "parameters", required = false) Map<String, String> parameters
	) {
		this.name = name;
		this.parameters = parameters == null ? ImmutableMap.of() : ImmutableMap.copyOf(parameters);
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
}
