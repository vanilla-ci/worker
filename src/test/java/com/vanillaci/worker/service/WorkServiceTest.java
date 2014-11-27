package com.vanillaci.worker.service;

import com.google.common.collect.*;
import com.vanillaci.worker.*;
import com.vanillaci.worker.model.*;
import com.vanillaci.worker.testplugins.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

/**
 * Created by joeljohnson on 11/27/14.
 */
public class WorkServiceTest extends BaseTest {
	@Autowired
	private WorkService workService;

	private static ThreadLocal<String> valueToSet = new ThreadLocal<>();

	@Before
	public void setUp() {
		valueToSet.remove();
	}

	@After
	public void tearDown() {
		valueToSet.remove();
	}

	@Test
	public void testTerminate() {
		Map<String, String> workParameters = new HashMap<>();

		List<WorkStepMessage> steps = ImmutableList.of(
			new WorkStepMessage(TerminateStep.class.getName(), ImmutableMap.of()),
			new WorkStepMessage(MethodInvokeStep.class.getName(), ImmutableMap.of(
				"className", getClass().getName(),
				"methodName", "methodToInvoke",
				"value", "this shouldn't be set"
			))
		);

		List<WorkStepMessage> postSteps = ImmutableList.of();

		WorkMessage workMessage = new WorkMessage(getFullTestName(), workParameters, steps, postSteps);

		workService.doWork(workMessage);

		Assert.assertNull(valueToSet.get());
	}


	@Test
	public void testTerminate_postStillRuns() {
		Map<String, String> workParameters = new HashMap<>();

		List<WorkStepMessage> steps = ImmutableList.of(
			new WorkStepMessage(TerminateStep.class.getName(), ImmutableMap.of())
		);

		List<WorkStepMessage> postSteps = ImmutableList.of(
			new WorkStepMessage(MethodInvokeStep.class.getName(), ImmutableMap.of(
				"className", getClass().getName(),
				"methodName", "methodToInvoke",
				"value", "this should be set"
			))
		);

		WorkMessage workMessage = new WorkMessage(getFullTestName(), workParameters, steps, postSteps);

		workService.doWork(workMessage);

		Assert.assertEquals("all post build steps should be called", "this should be set", valueToSet.get());
	}

	@Test
	public void testTerminate_allPostStillRuns() {
		Map<String, String> workParameters = new HashMap<>();

		List<WorkStepMessage> steps = ImmutableList.of(
			new WorkStepMessage(TerminateStep.class.getName(), ImmutableMap.of())
		);

		List<WorkStepMessage> postSteps = ImmutableList.of(
			new WorkStepMessage(DieStep.class.getName(), ImmutableMap.of()),
			new WorkStepMessage(MethodInvokeStep.class.getName(), ImmutableMap.of(
				"className", getClass().getName(),
				"methodName", "methodToInvoke",
				"value", "this should be set"
			))
		);

		WorkMessage workMessage = new WorkMessage(getFullTestName(), workParameters, steps, postSteps);

		workService.doWork(workMessage);

		Assert.assertEquals("all post build steps should be called", "this should be set", valueToSet.get());
	}

	@Test
	public void testOneWorkStep() {
		Map<String, String> workParameters = new HashMap<>();

		List<WorkStepMessage> steps = ImmutableList.of(
			new WorkStepMessage(MethodInvokeStep.class.getName(), ImmutableMap.of(
				"className", getClass().getName(),
				"methodName", "methodToInvoke",
				"value", "testValue"
			))
		);

		List<WorkStepMessage> postSteps = ImmutableList.of();

		WorkMessage workMessage = new WorkMessage(getFullTestName(), workParameters, steps, postSteps);

		workService.doWork(workMessage);

		Assert.assertEquals("testValue", valueToSet.get());
	}

	@Test
	public void testPostStep() {
		Map<String, String> workParameters = new HashMap<>();

		List<WorkStepMessage> steps = ImmutableList.of();

		List<WorkStepMessage> postSteps = ImmutableList.of(
			new WorkStepMessage(MethodInvokeStep.class.getName(), ImmutableMap.of(
				"className", getClass().getName(),
				"methodName", "methodToInvoke",
				"value", "testValue"
			))
		);

		WorkMessage workMessage = new WorkMessage(getFullTestName(), workParameters, steps, postSteps);

		workService.doWork(workMessage);

		Assert.assertEquals("testValue", valueToSet.get());
	}

	/**
	 * Called by MethodInvokeStep in test
	 */
	public static void methodToInvoke(String value) {
		valueToSet.set(value);
	}
}
