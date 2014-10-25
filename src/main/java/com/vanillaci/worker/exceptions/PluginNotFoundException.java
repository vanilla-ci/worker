package com.vanillaci.worker.exceptions;

/**
 * @author Joel Johnson
 */
public class PluginNotFoundException extends RuntimeException {
	public PluginNotFoundException(String name) {
		super(name);
	}
}
