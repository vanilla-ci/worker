package com.vanillaci.worker.service;

import com.vanillaci.plugins.*;
import com.vanillaci.worker.*;
import com.vanillaci.worker.testplugins.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;

/**
 * Created by joeljohnson on 11/27/14.
 */
public class PluginServiceTest extends BaseTest {
	@Autowired
	private PluginService pluginService;

	@Test
	public void testLoadPlugins() {
		WorkStep workStep = pluginService.getWorkStep(MethodInvokeStep.class.getName());
		Assert.assertNotNull(workStep);
	}
}
