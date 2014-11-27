package com.vanillaci.worker.service;

import com.google.common.collect.*;
import com.vanillaci.plugins.*;
import com.vanillaci.worker.exceptions.*;
import org.apache.log4j.*;
import org.springframework.stereotype.*;

import javax.annotation.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Joel Johnson
 */
@Service
public class PluginService {
	private Logger logger = Logger.getLogger(PluginService.class);

	private Map<String, WorkStep> workStepMap = ImmutableMap.of();
	private Map<String, WorkStepInterceptor> workStepInterceptorMap = ImmutableMap.of();

	@PostConstruct
	private void init() throws IOException {
		ImmutableList.Builder<WorkStep> workStepBuilder = ImmutableList.builder();
		ImmutableList.Builder<WorkStepInterceptor> workStepInterceptorBuilder = ImmutableList.builder();

		Enumeration<URL> plugins = getClass().getClassLoader().getResources("vanillaci.plugins");
		while(plugins.hasMoreElements()) {
			URL url = plugins.nextElement();
			try(InputStream inputStream = url.openStream()) {
				Scanner scanner = new Scanner(inputStream);
				int lineNumber = 0;
				while(scanner.hasNextLine()) {
					lineNumber++;
					String line = scanner.nextLine();
					try {
						Class<?> loadedClass = Class.forName(line);

						if(!WorkStep.class.isAssignableFrom(loadedClass) && !WorkStepInterceptor.class.isAssignableFrom(loadedClass)) {
							continue;
						}

						Object instance = loadedClass.newInstance();
						if(instance instanceof WorkStep) {
							workStepBuilder.add((WorkStep) instance);
						}

						if(instance instanceof WorkStepInterceptor) {
							workStepInterceptorBuilder.add((WorkStepInterceptor) instance);
						}
					} catch (ClassNotFoundException e) {
						logger.error("Unabled to find declared plugin class from " + url.toString() + " line " + lineNumber + ": " + line, e);
					} catch (InstantiationException e) {
						logger.error("Unabled instantiate plugin from " + url.toString() + " line " + lineNumber + ": " + line, e);
					} catch (IllegalAccessException e) {
						logger.error("No public constructor for plugin from " + url.toString() + " line " + lineNumber + ": " + line, e);
					}
				}
			}
		}

		registerWorkStepPlugins(workStepBuilder.build());
		registerWorkStepInterceptorPlugins(workStepInterceptorBuilder.build());
	}

	public void registerWorkStepPlugins(Iterable<WorkStep> workSteps) {
		ImmutableMap.Builder<String, WorkStep> builder = ImmutableMap.<String, WorkStep>builder()
			.putAll(workStepMap);

		for (WorkStep workStep : workSteps) {
			builder.put(workStep.getClass().getName(), workStep);
		}

		workStepMap = builder.build();
	}

	public void registerWorkStepInterceptorPlugins(ImmutableList<WorkStepInterceptor> workStepInterceptors) {
		ImmutableMap.Builder<String, WorkStepInterceptor> builder = ImmutableMap.<String, WorkStepInterceptor>builder()
			.putAll(workStepInterceptorMap);

		for (WorkStepInterceptor workStepInterceptor : workStepInterceptors) {
			builder.put(workStepInterceptor.getClass().getName(), workStepInterceptor);
		}

		workStepInterceptorMap = builder.build();
	}

	public WorkStep getWorkStep(String name) {
		WorkStep workStep = workStepMap.get(name);
		if(workStep == null) {
			throw new PluginNotFoundException(name);
		}
		return workStep;
	}

	public Iterable<WorkStepInterceptor> getWorkStepInterceptors() {
		return workStepInterceptorMap.values();
	}
}
