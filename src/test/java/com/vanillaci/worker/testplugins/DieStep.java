package com.vanillaci.worker.testplugins;

import com.vanillaci.plugins.*;

/**
 * Simply terminates
 * Created by joeljohnson on 11/27/14.
 */
public class DieStep implements WorkStep {
	@Override
	public void execute(WorkContext workContext) {
		throw new RuntimeException("this is an unhandled exception that's leaking through");
	}
}
