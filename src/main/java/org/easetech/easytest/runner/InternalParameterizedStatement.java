package org.easetech.easytest.runner;

import org.easetech.easytest.runner.DataDrivenTestRunner.EasyTestRunner;

import org.easetech.easytest.loader.DataConverter;
import org.easetech.easytest.util.DataContext;

import java.util.HashMap;

import java.util.Date;
import java.util.Map;
import org.easetech.easytest.exceptions.ParamAssertionError;
import org.easetech.easytest.loader.Loader;
import org.easetech.easytest.reports.data.DurationBean;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;

import java.lang.reflect.InvocationTargetException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

import org.junit.experimental.theories.PotentialAssignment;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import org.easetech.easytest.internal.EasyAssignments;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.internal.AssumptionViolatedException;

import org.easetech.easytest.reports.data.ReportDataContainer;

import org.easetech.easytest.reports.data.TestResultBean;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An internal class that holds the logic of running a given Test method.
 * This class contains the common code for both {@link EasyTestRunner} and SpringTestRunner
 * that is present in the easytest-spring module.
 * 
 * @author Anuj Kumar
 *
 */
public class InternalParameterizedStatement extends Statement{

    /**
     * An instance of logger associated with the test framework.
     */
    protected final Logger LOG = LoggerFactory.getLogger(InternalParameterizedStatement.class);

    private int successes = 0;

    /**
     * an instance of {@link FrameworkMethod} identifying the method to be tested.
     */
    private FrameworkMethod fTestMethod;
    
    /**
     * Instance of {@link TestResultBean} containg result for a single execution of test method
     */
    TestResultBean testResult;
    
    /**
     * The report container which holds all the reporting data
     */
    private ReportDataContainer testReportContainer = null;

    /**
     * The name of the method currently being executed. Used for populating the {@link #writableData} map.
     * Note this is a static field and therefore the state is maintained across the test executions
     */
    private static String mapMethodName = "";
    
    /**
     * The default rowNum within the {@link #writableData}'s particular method data.
     * Note this is a static field and therefore the state is maintained across the test executions
     */
    private static int rowNum = 0;
    
    /**
     * An instance of {@link Map} that contains the data to be written to the File
     */
    private Map<String, List<Map<String, Object>>> writableData = new HashMap<String, List<Map<String, Object>>>();
    
    /**
     * A List of {@link Assignments}. Each member in the list corresponds to a single set of test data to be
     * passed to the test method. For eg. If the user has specified the test data in the CSV file as:<br>
     * <br>
     * <B>testGetItems,LibraryId,itemType,searchText</B> <br>
     * ,4,journal,batman <br>
     * ,1,ebook,potter <br>
     * where: <li>testGetItems is the name of the method</li> <li>
     * LibraryId,itemType,searchText are the names of the parameters that the test method expects</li> and <li>
     * ,4,journal,batman</li> <li>,1,ebook,potter</li> are the actual test data <br>
     * then this list will consists of TWO {@link Assignments} instances with values: <li>[[{LibraryId=4,
     * itemType=journal, searchText=batman}]]</li> AND <li>[[{LibraryId=1, itemType=ebook, searchText=potter}]]
     * 
     */
    private List<EasyAssignments> listOfAssignments;

    /**
     * List of Invalid parameters
     */
    private List<AssumptionViolatedException> fInvalidParameters = new ArrayList<AssumptionViolatedException>();

    /**
     * An instance of {@link TestClass} identifying the class under test
     */
    private TestClass fTestClass;
    
    /**
     * The actual instance of the test class. This is extremely handy in cases where we want to reflectively set
     * instance fields on a test class.
     */
    Object testInstance;
    
    public InternalParameterizedStatement(FrameworkMethod fTestMethod , TestResultBean testResult,
        ReportDataContainer testReportContainer,Map<String, List<Map<String, Object>>> writableData, TestClass testClass, Object testInstance){
        this.fTestMethod = fTestMethod;
        this.testResult = testResult == null ? new TestResultBean() : testResult;
        this.testReportContainer = testReportContainer;
        this.writableData = writableData;
        this.listOfAssignments = new ArrayList<EasyAssignments>();
        this.fTestClass = testClass;
        this.testInstance = testInstance;
        DataContext.setMethodName(DataConverter.getFullyQualifiedTestName(fTestMethod.getName(),
            testClass.getJavaClass()));
        
    }
    
    private TestClass getTestClass() {
        return fTestClass;
    }

    public void evaluate() throws Throwable {
        runWithAssignment(EasyAssignments.allUnassigned(fTestMethod.getMethod(), getTestClass()));
        LOG.debug("ParamAnchor evaluate");
        if (successes == 0)
            Assert.fail("Never found parameters that satisfied method assumptions.  Violated assumptions: "
                + fInvalidParameters);
    }

    /**
     * This method encapsulates the actual change in behavior from the traditional JUnit Theories way of
     * populating and supplying the test data to the test method. This method creates a list of
     * {@link Assignments} identified by {@link #listOfAssignments} and then calls
     * {@link #runWithCompleteAssignment(EasyAssignments)} for each {@link Assignments} element in the
     * {@link #listOfAssignments}
     * 
     * @param parameterAssignment an instance of {@link Assignments} identifying the parameters that needs to be
     *            supplied test data
     * @throws Throwable if any exception occurs.
     */
    protected void runWithAssignment(EasyAssignments parameterAssignment) throws Throwable {
        while (!parameterAssignment.isComplete()) {
            List<PotentialAssignment> potentialAssignments = parameterAssignment.potentialsForNextUnassigned();
            boolean isFirstSetOfArguments = listOfAssignments.isEmpty();
            for (int i = 0; i < potentialAssignments.size(); i++) {
                if (isFirstSetOfArguments) {
                    EasyAssignments assignments = EasyAssignments.allUnassigned(fTestMethod.getMethod(),
                        getTestClass());
                    listOfAssignments.add(assignments.assignNext(potentialAssignments.get(i)));
                } else {
                    EasyAssignments assignments = listOfAssignments.get(i);
                    try {
                        listOfAssignments.set(i, assignments.assignNext(potentialAssignments.get(i)));
                    } catch (IndexOutOfBoundsException e) {
                        listOfAssignments.add(assignments.assignNext(potentialAssignments.get(i)));
                    }
                }

            }
            parameterAssignment = parameterAssignment.assignNext(null);
        }
        if (listOfAssignments.isEmpty()) {
            LOG.debug("The list of Assignments is null. It normally happens when the user has not supplied any parameters to the test.");
            LOG.debug(" Creating an instance of Assignments object with all its value unassigned.");
            listOfAssignments.add(EasyAssignments.allUnassigned(fTestMethod.getMethod(), getTestClass()));
        }
        for (EasyAssignments assignments : listOfAssignments) {
            runWithCompleteAssignment(assignments);
        }
    }
    
    /**
     * Run the test data with complete Assignments
     * 
     * @param complete the {@link Assignments}
     * @throws InstantiationException if an error occurs while instantiating the method
     * @throws IllegalAccessException if an error occurs due to illegal access to the test method
     * @throws InvocationTargetException if an error occurs because the method is not invokable
     * @throws NoSuchMethodException if an error occurs because no such method with the given name exists.
     * @throws Throwable any other error
     */
    protected void runWithCompleteAssignment(final EasyAssignments complete) throws InstantiationException,
        IllegalAccessException, InvocationTargetException, NoSuchMethodException, Throwable {
        new BlockJUnit4ClassRunner(getTestClass().getJavaClass()) {

            protected void collectInitializationErrors(List<Throwable> errors) {
                // do nothing
            }

            public Statement methodBlock(FrameworkMethod method) {
                final Statement statement = super.methodBlock(method);
                // Sample Run Notifier to catch any runnable events for a test and do something.
                final RunNotifier notifier = new RunNotifier();
                notifier.addListener(new EasyTestRunListener());
                final EachTestNotifier eachNotifier = new EachTestNotifier(notifier, null);
                eachNotifier.fireTestStarted();

                return new Statement() {

                    public void evaluate() throws Throwable {
                        try {
                            statement.evaluate();
                            handleDataPointSuccess();
                        } catch (AssumptionViolatedException e) {                         
                            eachNotifier.addFailedAssumption(e);
                            handleAssumptionViolation(e);
                        } catch (Throwable e) {
                            
                            if (e instanceof AssertionError) { // Assertion error
                                testResult.setPassed(Boolean.FALSE);
                                testResult.setResult(e.getMessage());
                            } else { // Exception
                                testResult.setException(Boolean.TRUE);
                                testResult.setExceptionResult(e.toString());
                            }
                            eachNotifier.addFailure(e);
                            reportParameterizedError(e, complete.getArgumentStrings(true));
                        } finally {
                            eachNotifier.fireTestFinished();
                        }
                    }

                };
            }

            protected Statement methodInvoker(FrameworkMethod method, Object test) {
                return methodCompletesWithParameters(method, complete, test);
            }

            public Object createTest() throws Exception {
                return testInstance;
            }
        }.methodBlock(fTestMethod).evaluate();
    }
    
    /**
     * This method is responsible for actually executing the test method as well as capturing the test data
     * returned by the test method. The algorithm to capture the output data is as follows:
     * <ol>
     * After the method has been invoked explosively, the returned value is checked. If there is a return value:
     * <li>We get the name of the method that is currently executing,
     * <li>We find the exact place in the test input data for which this method was executed,
     * <li>We put the returned result in the map of input test data. The entry in the map has the key :
     * {@link Loader#ACTUAL_RESULT} and the value is the returned value by the test method.
     * <li>If expected result{@link Loader#EXPECTED_RESULT} exist in user input data then we compare it with
     * actual result and put the test status either passed/failed. The entry in the map has the key :
     * {@link Loader#TEST_STATUS} and the value is the either PASSED or FAILED.
     * 
     * We finally write the test data to the file.
     * 
     * @param method an instance of {@link FrameworkMethod} that needs to be executed
     * @param complete an instance of {@link Assignments} that contains the input test data values
     * @param freshInstance a fresh instance of the class for which the method needs to be invoked.
     * @return an instance of {@link Statement}
     */
    private Statement methodCompletesWithParameters(final FrameworkMethod method,
        final EasyAssignments complete, final Object freshInstance) {

        final RunNotifier testRunNotifier = new RunNotifier();
        final TestRunDurationListener testRunDurationListener = new TestRunDurationListener();
        testRunNotifier.addListener(testRunDurationListener);
        final EachTestNotifier eachRunNotifier = new EachTestNotifier(testRunNotifier, null);

        return new Statement() {

            public void evaluate() throws Throwable {
                String currentMethodName = method.getMethod().getName();
                testResult = new TestResultBean();
                testResult.setMethod(currentMethodName);
                testResult.setDate(new Date());
                Object returnObj = null;
                try {
                    final Object[] values = complete.getMethodArguments(true);
                    // Log Statistics about the test method as well as the actual testSubject, if required.
                    boolean testContainsInputParams = (values.length != 0);
                    Map<String, Object> inputData = null;

                    // invoke test method
                    eachRunNotifier.fireTestStarted();
                    LOG.debug("Calling method {} with values {}", method.getName(), values);
                    returnObj = method.invokeExplosively(freshInstance, values);
                    eachRunNotifier.fireTestFinished();

                    DurationBean testItemDurationBean = new DurationBean(currentMethodName,
                        testRunDurationListener.getStartInNano(), testRunDurationListener.getEndInNano());
                    testResult.addTestItemDurationBean(testItemDurationBean);

                    testResult.setOutput((returnObj == null) ? "void" : returnObj);
                    testResult.setPassed(Boolean.TRUE);
                    if (!mapMethodName.equals(method.getMethod().getName())) {
                        // if mapMethodName is not same as the current executing method name
                        // then assign that to mapMethodName to write to writableData
                        mapMethodName = method.getMethod().getName();
                        // initialize the row number.
                        rowNum = 0;
                    }
                    if (writableData.get(mapMethodName) != null) {
                        inputData = writableData.get(mapMethodName).get(rowNum);
                        testResult.setInput(inputData);
                    } else {
                        testResult.setInput(null);
                    }

                    if (returnObj != null) {
                        LOG.debug("Data returned by method {} is {} :", method.getName(), returnObj);
                        // checking and assigning the map method name.

                        LOG.debug("mapMethodName:" + mapMethodName + " ,rowNum:" + rowNum);
                        if (writableData.get(mapMethodName) != null) {
                            LOG.debug("writableData.get({}) is {} ", mapMethodName,
                                writableData.get(mapMethodName));

                            Map<String, Object> writableRow = writableData.get(mapMethodName).get(rowNum);
                            writableRow.put(Loader.ACTUAL_RESULT, returnObj);
                            if (testContainsInputParams) {
                                LOG.debug("writableData.get({}) is {} ", mapMethodName,
                                    writableData.get(mapMethodName));
                                inputData.put(Loader.ACTUAL_RESULT, returnObj);
                            }

                            Object expectedResult = writableRow.get(Loader.EXPECTED_RESULT);
                            // if expected result exist in user input test data,
                            // then compare that with actual output result
                            // and write the status back to writable map data.
                            if (expectedResult != null) {
                                LOG.debug("Expected result exists");
                                if (expectedResult.toString().equals(returnObj.toString())) {
                                    writableRow.put(Loader.TEST_STATUS, Loader.TEST_PASSED);
                                } else {
                                    writableRow.put(Loader.TEST_STATUS, Loader.TEST_FAILED);
                                }
                            }
                            rowNum++;
                        }

                    }
                } catch (CouldNotGenerateValueException e) {
                    // ignore
                }
                testReportContainer.addTestResult(testResult);
            }
        };
    }

    protected void handleAssumptionViolation(AssumptionViolatedException e) {
        fInvalidParameters.add(e);
    }

    protected void reportParameterizedError(Throwable e, Object... params) throws Throwable {
        if (params.length == 0)
            throw e;
        throw new ParamAssertionError(e, fTestMethod.getName(), params);
    }

    protected void handleDataPointSuccess() {
        successes++;
    }
    

}
