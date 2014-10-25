package com.vanillaci.plugins;

import java.util.*;

/**
 * @author Joel Johnson
 */
public interface WorkContext {
	String getWorkId();

	/**
	 * All the parameters provided globally, specifically to this work step, or provided by previous workSteps
	 * To add parameters for later workSteps, use the {@link #addParameter(String, String)} method.
	 * @return read-only map of the parameters.
	 * @since 0.0.1
	 */
	Map<String, String> getParameters();

	/**
	 * Adds the given parameter to the parameter map for future work steps.
	 * @since 0.0.1
	 */
	void addParameter(String parameterName, String parameterValue);


	/**
	 * @return The work step that's going to be run.
	 * 			From the context of a WorkStep's execute method, WorkContext.getWorkStep() == this.
	 * @since 0.0.1
	 */
	WorkStep getWorkStep();

	/**
	 * Override the workStep that's about to run. Obviously, calling this will only have effect if it's called before the
	 * work step is run. For example, calling it from {@link com.vanillaci.plugins.WorkStepInterceptor#after} will have no effect.
	 *
	 * @param workStep The workStep that should execute.
	 * @since 0.0.1
	 */
	void setWorkStep(WorkStep workStep);
}
