package org.easetech.easytest.runner;

import org.junit.runner.Description;

/**
 * This listener measures the time of the test execution
 * @author gpcmol
 *
 */
public class TestRunDurationListener extends EasyTestRunListener {

	private long startInNano = 0L;
	private long endInNano = 0L;


	
	public void testFinished(Description description) throws Exception {
		endInNano = System.nanoTime();
	}
	
	public void testStarted(Description description) throws Exception {
		startInNano = System.nanoTime();
	}

	public long getStartInNano() {
		return startInNano;
	}

	public long getEndInNano() {
		return endInNano;
	}

}
