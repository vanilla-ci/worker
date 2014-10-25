package com.vanillaci.worker.service;

import com.google.common.collect.*;
import com.vanillaci.dieplugin.*;
import com.vanillaci.plugins.*;
import com.vanillaci.echoplugin.*;
import com.vanillaci.worker.exceptions.*;
import org.springframework.stereotype.*;

import javax.annotation.*;
import java.util.*;

/**
 * @author Joel Johnson
 */
@Service
public class PluginService {
	private Map<String, WorkStep> workStepMap = ImmutableMap.of();

	@PostConstruct
	private void init() {
		registerPlugins(ImmutableList.of(
			new EchoWorkStep(), // TODO: load jars dynamically
			new DieWorkStep()
		));
	}

	public void registerPlugins(Iterable<WorkStep> workSteps) {
		ImmutableMap.Builder<String, WorkStep> builder = ImmutableMap.<String, WorkStep>builder()
			.putAll(workStepMap);

		for (WorkStep workStep : workSteps) {
			builder.put(workStep.getClass().getName(), workStep);
		}

		workStepMap = builder.build();
	}

	public WorkStep getWorkStep(String name) {
		WorkStep workStep = workStepMap.get(name);
		if(workStep == null) {
			throw new PluginNotFoundException(name);
		}
		return workStep;
	}

	public Iterable<WorkStepInterceptor> getWorkStepInterceptors() {
		return ImmutableList.of();
	}
}
