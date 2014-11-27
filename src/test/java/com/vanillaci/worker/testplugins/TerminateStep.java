package com.vanillaci.worker.testplugins;

import com.vanillaci.plugins.*;
import com.vanillaci.worker.model.*;

/**
 * Simply terminates
 * Created by joeljohnson on 11/27/14.
 */
public class TerminateStep implements WorkStep {
	@Override
	public void execute(WorkContext workContext) {
		workContext.setTerminate(true);
	}
}
