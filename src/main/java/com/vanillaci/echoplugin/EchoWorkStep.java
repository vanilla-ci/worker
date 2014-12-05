package com.vanillaci.echoplugin;

import com.vanillaci.plugins.*;

/**
 * @author Joel Johnson
 */
public class EchoWorkStep implements WorkStep {
	@Override
	public void execute(WorkContext workContext) {
		System.out.println("executing for " + workContext.getWorkId());
		System.out.println("I told to say: " + workContext.getParameter("value"));
	}
}
