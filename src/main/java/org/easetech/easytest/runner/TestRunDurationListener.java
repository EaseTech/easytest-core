package org.easetech.easytest.runner;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * This listener measures the time of the test execution
 * @author gpcmol
 *
 */
public class TestRunDurationListener extends RunListener {

	private long startInNano = 0L;
	private long endInNano = 0L;
	
	@Override
	public void testAssumptionFailure(Failure failure) {
		// not used
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		// not used
	}

	@Override
	public void testFinished(Description description) throws Exception {
		endInNano = System.nanoTime();
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		// not used
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		// not used
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		// not used
	}

	@Override
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
