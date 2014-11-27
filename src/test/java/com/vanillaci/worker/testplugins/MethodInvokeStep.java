package com.vanillaci.worker.testplugins;

import com.vanillaci.plugins.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Created by joeljohnson on 11/27/14.
 */
public class MethodInvokeStep implements WorkStep {
	@Override
	public void execute(WorkContext workContext) {
		Map<String, String> parameters = workContext.getParameters();
		String className = parameters.get("className");
		String methodName = parameters.get("methodName");
		String value = parameters.get("value");

		try {
			Class<?> loadedClass = Class.forName(className);
			Method method = loadedClass.getMethod(methodName, String.class);
			method.invoke(null, value);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
