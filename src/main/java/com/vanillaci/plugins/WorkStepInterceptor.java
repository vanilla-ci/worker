package com.vanillaci.plugins;

/**
 * @author Joel Johnson
 */
public interface WorkStepInterceptor {
	void before(WorkContext workContext);
	void after(WorkContext workContext);
}
