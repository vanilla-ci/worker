package com.vanillaci.worker.testplugins;

import com.vanillaci.plugins.*;

import java.lang.reflect.*;

/**
 * Created by joeljohnson on 11/27/14.
 */
public class MethodInvokeStep implements WorkStep {
	@Override
	public void execute(WorkContext workContext) {
		String className = workContext.getParameter("className");
		String methodName = workContext.getParameter("methodName");
		String value = workContext.getParameter("value");

		try {
			Class<?> loadedClass = Class.forName(className);
			Method method = loadedClass.getMethod(methodName, String.class);
			method.invoke(null, value);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
