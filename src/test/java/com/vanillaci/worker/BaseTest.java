package com.vanillaci.worker;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.boot.test.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;

/**
 * Created by joeljohnson on 11/27/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WorkerApplication.class)
@ActiveProfiles(profiles="test")
public class BaseTest {
	@Rule
	public TestNameRule testNameRule = new TestNameRule();

	public String getFullTestName() {
		return testNameRule.getFrameworkMethod().getMethod().getDeclaringClass().getName() + "#" + testNameRule.getFrameworkMethod().getName();
	}
}
