
package org.easetech.easytest.runner;

import org.jfree.util.Log;

import org.easetech.easytest.config.ConfigLoader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.easetech.easytest.annotation.Converters;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Intercept;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.Provided;
import org.easetech.easytest.annotation.TestBean;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.converter.Converter;
import org.easetech.easytest.converter.ConverterManager;
import org.easetech.easytest.exceptions.ParamAssertionError;
import org.easetech.easytest.internal.EasyAssignments;
import org.easetech.easytest.io.EmptyResource;
import org.easetech.easytest.io.Resource;
import org.easetech.easytest.io.ResourceLoader;
import org.easetech.easytest.io.ResourceLoaderStrategy;
import org.easetech.easytest.loader.DataConverter;
import org.easetech.easytest.loader.Loader;
import org.easetech.easytest.loader.LoaderFactory;
import org.easetech.easytest.loader.LoaderType;
import org.easetech.easytest.reports.data.DurationBean;
import org.easetech.easytest.reports.data.ReportDataContainer;
import org.easetech.easytest.reports.data.TestResultBean;
import org.easetech.easytest.util.ConfigContext;
import org.easetech.easytest.util.DataContext;
import org.easetech.easytest.util.RunAftersWithOutputData;
import org.easetech.easytest.util.TestInfo;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link Suite} that encapsulates the {@link EasyTestRunner} in order to provide users with clear
 * indication of which test method is run and what is the input test data that the method is run with. For example, when
 * a user runs the test method with name : <B><I>getTestData</I></B> with the following test data:
 * <ul>
 * <li><B>"libraryId=1 and itemId=2"</B></li>
 * <li><B>"libraryId=2456 and itemId=789"</B></li><br>
 * <br>
 * 
 * then, {@link DataDrivenTestRunner}, will provide the details of the executing test method in the JUnit supported IDEs
 * like this:
 * 
 * <ul>
 * <li><B><I>getTestData{libraryId=1 ,itemId=2}</I></B></li>
 * <li><B><I>getTestData{libraryId=2456 ,itemId=789}</I></B></li></br></br>
 * 
 * This gives user the clear picture of which test was run with which input test data.
 * 
 * For details on the actual Runner implementation, see {@link EasyTestRunner}
 * 
 * @author Anuj Kumar
 * 
 */
public class DataDrivenTestRunner extends Suite {

    /**
     * An instance of {@link Map} that contains the data to be written to the File
     */
    private static Map<String, List<Map<String, Object>>> writableData = new HashMap<String, List<Map<String, Object>>>();

    /**
     * The default rowNum within the {@link #writableData}'s particular method data.
     */
    private static int rowNum = 0;

    /**
     * The name of the method currently being executed. Used for populating the {@link #writableData} map.
     */
    private String mapMethodName = "";

    /**
     * The report container which holds all the reporting data
     */
    private ReportDataContainer testReportContainer = null;

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger PARAM_LOG = LoggerFactory.getLogger(DataDrivenTestRunner.class);

    /**
     * A {@link BlockJUnit4ClassRunner} Runner implementation that adds support of input parameters as part of the
     * {@link Test} annotation. This {@link BlockJUnit4ClassRunner} extension is modified for providing convenient Data
     * Driven Testing support to its users. This Runner is capable of generating new instances of
     * {@link FrameworkMethod} based on the test data for a given method. For eg. If there is a method
     * "testMethod(String testData)" that has three sets of test data : [{"testData1"},{"testData2"},{"testData3"}],
     * then this runner will generate three {@link FrameworkMethod} instances with the method names :<br>
     * testMethod{testData1}<br>
     * testMethod{testData2}<br>
     * and<br>
     * testMethod{testData3}<br>
     * <br>
     * 
     * <br>
     * <B> A user can specify the test data at the class level(and/or method level), using the {@link DataLoader}
     * annotation and(if wants) override it at the method level. The Runner will take care of executing the test method
     * with the right test data.</B><br>
     * This is extremely beneficial in cases, where the user just wants to load the data once and then reuse it for all
     * the test methods. If the user wants, then he can always override the test data at the method level by specifying
     * the {@link DataLoader} annotation at the method level. <br>
     * <br>
     * In addition, this runner also introduces a new way for the user to specify the test data using {@link DataLoader}
     * annotation.
     * 
     * <br>
     * <br>
     * There is also a {@link Param} annotation to handle boiler plate tasks on behalf of the user as well as supports
     * additional functionality that eases the life of the user. For eg. it supports Java PropertyEditors to
     * automatically convert a String to the specified Object. It also supports passing a Map to the test method that
     * contains all the available test data key / value pairs for easy consumption by the user. It also supports user
     * defined custom Objects as parameters.Look at {@link Converter} for details.<br>
     * <br>
     * 
     * 
     * @author Anuj Kumar
     * 
     * 
     *         Finally, EasyTest also supports {@link Intercept} annotation. This annotation can be used to intercept
     *         calls to the test subject that is currently being tested. For eg. if you want to capture how much time a
     *         particular method of the actual service class is taking, then you can mark the field representing the
     *         testSubject with {@link Intercept} annotation. The framework also provides convenient way to write your
     *         own custom method interceptors.
     */
    private class EasyTestRunner extends BlockJUnit4ClassRunner {

        /**
         * Convenient class member to get the list of {@link FrameworkMethod} that this runner will execute.
         */
        List<FrameworkMethod> frameworkMethods;

        /**
         * The actual instance of the test class. This is extremely handy in cases where we want to reflectively set
         * instance fields on a test class.
         */
        Object testInstance;

        TestResultBean testResult;

        /**
         * 
         * Construct a new DataDrivenTestRunner
         * 
         * @param klass the test class whose test methods needs to be executed
         * @throws InitializationError if any error occurs
         */
        public EasyTestRunner(Class<?> klass) throws InitializationError {
            super(klass);
            try {
                testInstance = getTestClass().getOnlyConstructor().newInstance();
                ConfigLoader.loadTestConfigurations(getTestClass().getJavaClass(), testInstance);
                // handleTestConfigProvider(getTestClass().getJavaClass());
                instrumentClass(getTestClass().getJavaClass());
                // initialize report container class
                // TODO add condition whether reports must be switched on or off
                testReportContainer = new ReportDataContainer(getTestClass().getJavaClass());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Instrument the class's field that are marked with {@link Intercept} annotation
         * 
         * @param testClass the class under test
         * @throws IllegalArgumentException if an exception occurred
         * @throws IllegalAccessException if an exception occurred
         * @throws InstantiationException if an exception occurred
         */
        protected void instrumentClass(Class<?> testClass) throws IllegalArgumentException, IllegalAccessException,
            InstantiationException {
            Field[] fields = testClass.getDeclaredFields();
            for (Field field : fields) {
                Intercept interceptor = field.getAnnotation(Intercept.class);
                if (interceptor != null) {
                    Class<? extends MethodInterceptor> interceptorClass = interceptor.interceptor();
                    // This is the field we want to enhance
                    Class<?> fieldType = field.getType();
                    Object proxiedObject = null;
                    if (fieldType.isInterface()) {
                        PARAM_LOG
                            .debug("Proxying Interfaces is currently not handled in EasyTest Core module. The field of type :"
                                + fieldType + " will not be proxied");
                    } else {
                        Enhancer enhancer = new Enhancer();
                        enhancer.setSuperclass(fieldType);
                        enhancer.setCallback(interceptorClass.newInstance());
                        proxiedObject = enhancer.create();
                    }

                    try {
                        if (proxiedObject != null) {
                            field.set(testInstance, proxiedObject);
                        }

                    } catch (Exception e) {
                        Assert
                            .fail("Failed while trying to instrument the class for Intercept annotation with exception : "
                                + e.getStackTrace());
                    }
                }
            }
        }

        protected void handleTestConfigProvider(Class<?> testClass) {
            Field[] fields = testClass.getDeclaredFields();
            for (Field field : fields) {
                Provided providedAnnotation = field.getAnnotation(Provided.class);
                if (providedAnnotation != null) {
                    String providerBeanName = providedAnnotation.value();
                    Object beanInstance = null;
                    if (!providerBeanName.isEmpty()) {
                        // Load the bean by name
                        beanInstance = ConfigContext.getBeanByName(providerBeanName);
                    } else {
                        // provider bean name is NULL.
                        // load bean by type
                        Class beanClass = field.getType();
                        beanInstance = ConfigContext.getBeanByType(beanClass);
                        if (beanInstance == null) {
                            beanInstance = ConfigContext.getBeanByName(field.getName());
                        }

                    }
                    try {
                        field.set(testInstance, beanInstance);
                    } catch (Exception e) {
                        Assert.fail("Failed while trying to handle Provider annotation for Field : "
                            + field.getDeclaringClass() + e.getStackTrace());
                    }

                }
            }
        }

        /**
         * Try to collect any initialization errors, if any.
         * 
         * @param errors
         */

        protected void collectInitializationErrors(List<Throwable> errors) {
            super.collectInitializationErrors(errors);
        }

        /**
         * Override the name of the test. In case of EasyTest, it will be the name of the test method concatenated with
         * the input test data that the method will run with.
         * 
         * @param method the {@link FrameworkMethod}
         * @return an overridden test method Name
         */

        protected String testName(final FrameworkMethod method) {
            return String.format("%s", method.getName());
        }

        /**
         * Overridden the compute test method to make it save the method list as class instance, so that the method does
         * not run multiple times. Also, this method now is responsible for creating multiple {@link FrameworkMethod}
         * instances for a given method with multiple test data. So, if a given test method needs to run three times
         * with three set of input test data, then this method will actually create three instances of
         * {@link FrameworkMethod}. In order to allow the user to override the default name, {@link FrameworkMethod} is
         * extended with {@link EasyFrameworkMethod} and {@link EasyFrameworkMethod#setName(String)} method introduced.
         * 
         * @return list of {@link FrameworkMethod}
         */

        protected List<FrameworkMethod> computeTestMethods() {
            if (frameworkMethods != null && !frameworkMethods.isEmpty()) {
                return frameworkMethods;
            }
            List<FrameworkMethod> finalList = new ArrayList<FrameworkMethod>();
            // Iterator<FrameworkMethod> testMethodsItr = super.computeTestMethods().iterator();
            Class<?> testClass = getTestClass().getJavaClass();
            for (FrameworkMethod methodWithData : methodsWithData) {
                String superMethodName = DataConverter.getFullyQualifiedTestName(methodWithData.getName(), testClass);
                for (FrameworkMethod method : super.computeTestMethods()) {

                    if (superMethodName.equals(DataConverter.getFullyQualifiedTestName(method.getName(), testClass))) {
                        // Load the data,if any, at the method level
                        loadData(null, method, getTestClass().getJavaClass());
                        registerConverter(method.getAnnotation(org.easetech.easytest.annotation.Converters.class));
                        List<Map<String, Object>> methodData = null;
                        if (DataContext.getData() != null) {
                            methodData = DataContext.getData().get(superMethodName);
                        }

                        if (methodData == null) {
                            Assert.fail("Method with name : " + superMethodName
                                + " expects some input test data. But there doesnt seem to be any test "
                                + "data for the given method. Please check the Test Data file for the method data. "
                                + "Possible cause could be that the data did not get loaded at all from the file "
                                + "or a spelling mismatch in the method name. Check logs for more details.");
                        }
                        for (Map<String, Object> testData : methodData) {
                            // Create a new FrameworkMethod for each set of test data
                            EasyFrameworkMethod easyMethod = new EasyFrameworkMethod(method.getMethod());
                            easyMethod.setName(method.getName().concat(testData.toString()));
                            finalList.add(easyMethod);
                        }
                        // Since the runner only ever handles a single method, we break out of the loop as soon as we
                        // have
                        // found our method.
                        break;
                    }
                }
            }
            finalList.addAll(methodsWithNoData);
            if (finalList.isEmpty()) {
                Assert.fail("No method exists for the Test Runner");
            }
            frameworkMethods = finalList;
            return finalList;
        }

        /**
         * Validate that there could ever be only one constructor.
         * 
         * @param errors list of any errors while validating the Constructor
         */

        protected void validateConstructor(List<Throwable> errors) {
            validateOnlyOneConstructor(errors);
        }

        /**
         * Validate the test methods.
         * 
         * @param errors list of any errors while validating test method
         */

        protected void validateTestMethods(List<Throwable> errors) {
            // Do Nothing as we now support public non void arg test methods
        }

        /**
         * Override the methodBlock to return custom {@link ParamAnchor}
         * 
         * @param method the Framework Method
         * @return a compiled {@link Statement} object to be evaluated
         */

        public Statement methodBlock(final FrameworkMethod method) {
            return new ParamAnchor(method, getTestClass());
        }

        /**
         * Returns a {@link Statement}: run all non-overridden {@code @AfterClass} methods on this class and
         * superclasses before executing {@code statement}; all AfterClass methods are always executed: exceptions
         * thrown by previous steps are combined, if necessary, with exceptions from AfterClass methods into a
         * {@link MultipleFailureException}.
         * 
         * This method is also responsible for writing the data to the output file in case the user is returning test
         * data from the test method. This method will make sure that the data is written to the output file once after
         * the Runner has completed and not for every instance of the test method.
         */

        protected Statement withAfterClasses(Statement statement) {
            List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(AfterClass.class);
            List<FrameworkMethod> testMethods = getTestClass().getAnnotatedMethods(Test.class);
            List<TestInfo> testInfoList = new ArrayList<TestInfo>();

            // populateTestInfo(testInfo);
            // THere would always be atleast one method associated with the Runner, else validation would fail.
            for (FrameworkMethod method : testMethods) {
                TestInfo testInfo = null;

                // Only if the return type of the Method is not VOID, we try to determine the right loader and data
                // files.
                DataLoader loaderAnnotation = method.getAnnotation(DataLoader.class);
                if (loaderAnnotation != null) {
                    testInfo = determineLoader(loaderAnnotation, getTestClass());

                } else {
                    loaderAnnotation = getTestClass().getJavaClass().getAnnotation(DataLoader.class);
                    if (loaderAnnotation != null) {
                        testInfo = determineLoader(loaderAnnotation, getTestClass());
                    }
                }
                if (testInfo != null) {
                    testInfo.setMethodName(method.getName());
                    testInfoList.add(testInfo);
                }

            }
            RunAftersWithOutputData runAftersWithOutputData = new RunAftersWithOutputData(statement, afters, null,
                testInfoList, writableData, testReportContainer);
            return runAftersWithOutputData;
        }

        /**
         * 
         * Static inner class to support Statement evaluation.
         * 
         */
        public class ParamAnchor extends Statement {

            /**
             * An instance of logger associated with the test framework.
             */
            protected final Logger LOG = LoggerFactory.getLogger(EasyTestRunner.ParamAnchor.class);

            private int successes = 0;

            /**
             * an instance of {@link FrameworkMethod} identifying the method to be tested.
             */
            private FrameworkMethod fTestMethod;

            /**
             * An instance of {@link TestClass} identifying the class under test
             */
            private TestClass fTestClass;

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
             * 
             * Construct a new ParamAnchor. The constructor performs the following operations:<br>
             * <li>It sets the class variables method , testClass and initializes the instance of
             * {@link #listOfAssignments}</li> <li>
             * It searches for {@link DataLoader} annotation and if it finds one, it tries to get the right
             * {@link Loader} from the {@link LoaderFactory}. If the {@link Loader} is not found, the test fails. If the
             * Loader is found, it loads the data and makes it available to the entire test Thread using
             * {@link DataContext}
             * 
             * If the annotation {@link DataLoader} is not present, then the test assumes that the user wants to use
             * {@link ParametersSuppliedBy} annotation and does nothing.
             * 
             * @param method the method to run the test on
             * @param testClass an instance of {@link TestClass}
             */
            public ParamAnchor(FrameworkMethod method, TestClass testClass) {
                fTestMethod = method;
                fTestClass = testClass;
                listOfAssignments = new ArrayList<EasyAssignments>();
                DataContext.setMethodName(DataConverter.getFullyQualifiedTestName(method.getName(),
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
                            LOG.debug("Calling method {} with values {}" , method.getName(), values);
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
                                LOG.debug("Data returned by method {} is {} :", method.getName() , returnObj);
                                // checking and assigning the map method name.

                                LOG.debug("mapMethodName:" + mapMethodName + " ,rowNum:" + rowNum);
                                if (writableData.get(mapMethodName) != null) {
                                    LOG.debug("writableData.get({}) is {} ", mapMethodName, writableData.get(mapMethodName));

                                    Map<String, Object> writableRow = writableData.get(mapMethodName).get(rowNum);
                                    writableRow.put(Loader.ACTUAL_RESULT, returnObj);
                                    if (testContainsInputParams) {
                                        LOG.debug("writableData.get({}) is {} ", mapMethodName, writableData.get(mapMethodName));
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

    }

    /**
     * A List of {@link EasyTestRunner}s.
     */
    private final List<Runner> runners = new ArrayList<Runner>();

    /**
     * List of {@link FrameworkMethod} that does not have any external test data associated with them.
     */
    private List<FrameworkMethod> methodsWithNoData = new ArrayList<FrameworkMethod>();

    /**
     * List of {@link FrameworkMethod} that does have any external test data associated with them.
     */
    private List<FrameworkMethod> methodsWithData = new ArrayList<FrameworkMethod>();

    /**
     * Get the children Runners
     * 
     * @return a list of {@link DataDrivenTestRunner}
     */

    protected List<Runner> getChildren() {
        return runners;
    }

    /**
     * 
     * Construct a new DataDrivenTest. During construction, we will load the test data, and then we will create a list
     * of {@link EasyTestRunner}. The algorithm is as follows:<br>
     * <ul>
     * <li>STEP 1: Load the test data. This will also do the check whether there exists a {@link DataLoader} annotation
     * at the class level</li>
     * <li>Iterate over each method.<br>
     * For each method:
     * <ol>
     * <li>If method has {@link DataLoader} annotation, it means that there is test data associated with the test
     * method.<br>
     * In such a case add the method to the methodsWithData List.
     * <li>If method does not have a {@link DataLoader} annotation, then:
     * <ol>
     * <li>Check if there already exists data for the method. This is possible as the data could have been loaded at the
     * class level.<br>
     * <li>If the data for the given method exists, add the method to the methodsWithData List.
     * <li>If the data does not exists for the given test method, put it aside in a list of unused methods,
     * </ol>
     * </ol>
     * Iteration over each method ends.<br>
     * 
     * Finally create an instance of {@link EasyTestRunner} and make it use all the different types of methods we
     * identified.<br>
     * We need to identify methods with data and methods with no data primarily to group the test methods together as
     * well as to efficiently create new test methods for each method that has test data associated with it. This whole
     * process will happen for each of the test class that is part of the Suite.
     * 
     * @param klass the test class
     * @throws InitializationError if an initializationError occurs
     */
    @SuppressWarnings("unchecked")
    public DataDrivenTestRunner(Class<?> klass) throws InitializationError {
        super(klass, Collections.EMPTY_LIST);
        Class<?> testClass = getTestClass().getJavaClass();
        // Load TestBeanConfigurations if any
        loadTestBeanConfig(testClass);
        // Load the data at the class level, if any.
        loadData(klass, null, testClass);
        // Registering Converters based on @Converters annotation
        registerConverter(testClass.getAnnotation(org.easetech.easytest.annotation.Converters.class));
        List<FrameworkMethod> availableMethods = getTestClass().getAnnotatedMethods(Test.class);
        List<FrameworkMethod> methodsWithNoData = new ArrayList<FrameworkMethod>();
        List<FrameworkMethod> methodsWithData = new ArrayList<FrameworkMethod>();
        for (FrameworkMethod method : availableMethods) {
            // Try loading the data if any at the method level
            if (method.getAnnotation(DataLoader.class) != null) {
                methodsWithData.add(method);
            } else {
                // Method does not have its own dataloader annotation
                // Does method have data already loaded at the class level?
                boolean methodDataLoaded = isMethodDataLoaded(DataConverter.getFullyQualifiedTestName(method.getName(),
                    testClass));
                if (methodDataLoaded) {
                    methodsWithData.add(method);
                } else {
                    methodsWithNoData.add(method);
                }
            }

        }
        // Finally create a runner for methods that do not have Data specified with them.
        // These are potentially the methods with no method parameters and with @Test annotation.
        if (!methodsWithNoData.isEmpty()) {
            this.methodsWithNoData = methodsWithNoData;

        }
        if (!methodsWithData.isEmpty()) {
            this.methodsWithData = methodsWithData;
        }
        runners.add(new EasyTestRunner(klass));
    }

    private void loadTestBeanConfig(Class testClass) {
        TestConfigProvider configProvider = (TestConfigProvider) testClass.getAnnotation(TestConfigProvider.class);
        if (configProvider != null) {
            try {
                loadConfigBeans(configProvider.value());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                Assert.fail();
            } catch (InstantiationException e) {
                Assert.fail();
            }
        }
    }

    private void loadConfigBeans(Class<?>... configClasses) throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException, InstantiationException {

        for (Class<?> configClass : configClasses) {
            Object classInstance = configClass.newInstance();
            Method[] methods = configClass.getDeclaredMethods();
            for (Method method : methods) {
                TestBean testBean = method.getAnnotation(TestBean.class);
                if (testBean != null) {
                    String beanName = testBean.value();
                    Class<?> beanType = method.getReturnType();
                    Object[] params = {};
                    Object object = method.invoke(classInstance, params);
                    if (!beanName.isEmpty()) {
                        ConfigContext.setTestBeanByName(beanName, object);
                    } else {
                        ConfigContext.setTestBeanByType(beanType, object);
                    }
                }
            }
        }
    }

    /**
     * Method responsible for registering the converters with the EasyTest framework
     * 
     * @param converter the annotation {@link Converters}
     */
    private void registerConverter(Converters converter) {
        if (converter != null) {
            Class<? extends Converter>[] convertersToRegister = converter.value();
            if (convertersToRegister != null && convertersToRegister.length != 0) {
                for (Class<? extends Converter> value : convertersToRegister) {
                    ConverterManager.registerConverter(value);
                }
            }
        }

    }

    /**
     * Check if the data for the given method is loaded or not.
     * 
     * @param methodName the name of the method whose data needs to be checked.
     * @return true if there exists data for the given method, else false.
     */
    protected boolean isMethodDataLoaded(String methodName) {

        boolean result = false;
        if (DataContext.getData() == null || DataContext.getData().keySet() == null
            || DataContext.getData().keySet().isEmpty()) {
            result = false;
        } else {
            Iterator<String> keyIterator = DataContext.getData().keySet().iterator();
            while (keyIterator.hasNext()) {
                result = methodName.equals(keyIterator.next()) ? true : false;
                if (result) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Load the Data for the given class or method. This method will try to find {@link DataLoader} on either the class
     * level or the method level. In case the annotation is found, this method will load the data using the specified
     * loader class and then save it in the DataContext for further use by the system. We also create another copy of
     * the input test data that we store in the {@link DataDrivenTestRunner#writableData} field. This is done in order
     * to facilitate the writing of the data that might be returned by the test method.
     * 
     * @param testClass the class object, if any.
     * @param method current executing method, if any.
     * @param currentTestClass the currently executing test class. this is used to append in front of the method name to
     *            get unique method names as there could be methods in different classes with the same name and thus we
     *            want to avoid conflicts.
     */

    protected void loadData(Class<?> testClass, FrameworkMethod method, Class<?> currentTestClass) {
        if (testClass == null && method == null) {
            Assert
                .fail("The framework should provide either the testClass parameter or the method parameter in order to load the test data.");
        }
        // We give priority to Class Loading and then to method loading
        DataLoader testData = null;
        if (testClass != null) {
            testData = testClass.getAnnotation(DataLoader.class);
        } else {
            testData = method.getAnnotation(DataLoader.class);
        }
        if (testData != null) {
            TestInfo testInfo = determineLoader(testData, getTestClass());
            Loader dataLoader = testInfo.getDataLoader();
            if (testInfo.getDataLoader() == null) {
                Assert.fail("The framework currently does not support the specified Loader type. "
                    + "You can provide the custom Loader by choosing LoaderType.CUSTOM in TestData "
                    + "annotation and providing your custom loader using DataLoader annotation.");
            } else {
                if (testInfo.getFilePaths() == null || testInfo.getFilePaths().length == 0) {
                    // implies that there exists a CUSTOM loader that loads the data using Java classes
                    Map<String, List<Map<String, Object>>> data = dataLoader.loadData(new EmptyResource());
                    // We also maintain the copy of the actual data for our write functionality.
                    writableData.putAll(data);
                    DataContext.setData(DataConverter.appendClassName(data, currentTestClass));
                    DataContext.setConvertedData(DataConverter.convert(data, currentTestClass));
                } else {
                    ResourceLoader resourceLoader = new ResourceLoaderStrategy(getTestClass().getJavaClass());
                    for (String filePath : testInfo.getFilePaths()) {
                        Resource resource = resourceLoader.getResource(filePath);
                        try {
                            if (resource.exists()) {
                                Map<String, List<Map<String, Object>>> data = dataLoader.loadData(resource);
                                // We also maintain the copy of the actual data for our write functionality.
                                writableData.putAll(data);
                                DataContext.setData(DataConverter.appendClassName(data, currentTestClass));
                                DataContext.setConvertedData(DataConverter.convert(data, currentTestClass));
                            } else {
                                PARAM_LOG.warn("Resource {} does not exists in the specified path. If it is a classpath resource, use 'classpath:' " +
                                		"before the path name, else check the path.", resource);
                            }
                        } catch (Exception e) {
                            PARAM_LOG.error("Exception occured while trying to load the data for resource {}", resource , e);
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a {@link Statement}: We override this method as it was being called twice for the same class. Looks like
     * a bug in JUnit.
     */

    protected Statement withBeforeClasses(Statement statement) {
        return statement;
    }

    /**
     * Returns a {@link Statement}: We override this method as it was being called twice for the same class. Looks like
     * a bug in JUnit.
     */

    protected Statement withAfterClasses(Statement statement) {
        return statement;
    }

    /**
     * Method that determines the right Loader and the right Data Files for the "write output data" functionality
     * supported by the EasyTest Framework.
     * 
     * @param testData an instance of {@link DataLoader} that helps in identifying the right {@link Loader} to write the
     *            data back to the file.
     * @param testClass the class that the {@link TestInfo} object will be associated with
     * 
     * @return {@link TestInfo} an instance of {@link TestInfo} containing information about the currently executing
     *         test.
     */
    private TestInfo determineLoader(DataLoader testData, TestClass testClass) {
        TestInfo result = new TestInfo(testClass);
        String[] dataFiles = testData.filePaths();
        LoaderType loaderType = testData.loaderType();
        // Loader
        Loader dataLoader = null;
        if (LoaderType.CUSTOM.equals(loaderType)) {
            PARAM_LOG.info("User specified to use custom Loader. Trying to get the custom loader.");
            if (testData.loader() == null) {
                Assert.fail("Specified the LoaderType as CUSTOM but did not specify loader"
                    + " attribute. A loaderType of CUSTOM requires the loader " + "attribute specifying "
                    + "the Custom Loader Class which implements Loader interface.");
            } else {
                try {
                    Class<? extends Loader> loaderClass = testData.loader();
                    dataLoader = loaderClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Exception occured while trying to instantiate a class of type :"
                        + testData.loader(), e);
                }
            }
        } else if (dataFiles.length == 0) {
            // No files specified, implies user wants to load data with
            // custom loader
            if (testData.loader() == null) {
                Assert.fail("Specified the LoaderType as CUSTOM but did not specify loader"
                    + " attribute. A loaderType of CUSTOM requires the loader " + "attribute specifying "
                    + "the Custom Loader Class which implements Loader interface.");
            } else {
                try {
                    Class<? extends Loader> loaderClass = testData.loader();
                    dataLoader = loaderClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Exception occured while trying to instantiate a class of type :"
                        + testData.loader(), e);
                }
            }
        } else {
            // user has specified data files and the data fileType is also
            // not custom.
            dataLoader = LoaderFactory.getLoader(loaderType);
        }
        result.setDataLoader(dataLoader);
        result.setFilePaths(dataFiles);
        return result;
    }

}
