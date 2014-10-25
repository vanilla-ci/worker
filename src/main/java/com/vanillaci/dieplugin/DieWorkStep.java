package com.vanillaci.dieplugin;

import com.vanillaci.plugins.*;

/**
 * @author Joel Johnson
 */
public class DieWorkStep implements WorkStep {
	@Override
	public void execute(WorkContext workContext) {
		System.out.println("I'm about to die...");
		throw new RuntimeException("I'm going to die now");
	}
}
