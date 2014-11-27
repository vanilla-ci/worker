package com.vanillaci.worker;

import org.junit.rules.*;
import org.junit.runners.model.*;

/**
 * Created by joeljohnson on 11/27/14.
 */
public class TestNameRule implements MethodRule {
	private FrameworkMethod frameworkMethod;

	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		this.frameworkMethod = method;
		return base;
	}

	public FrameworkMethod getFrameworkMethod() {
		return frameworkMethod;
	}
}
