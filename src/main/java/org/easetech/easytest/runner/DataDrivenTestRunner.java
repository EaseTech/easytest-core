package org.easetech.easytest.runner;

import org.junit.runners.ParentRunner;

import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import net.sf.cglib.proxy.Enhancer;
import org.apache.poi.ss.formula.functions.Value;
import org.easetech.easytest.annotation.Converters;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Intercept;
import org.easetech.easytest.annotation.Parallel;
import org.easetech.easytest.annotation.Provided;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.annotation.TestProperties;
import org.easetech.easytest.converter.Converter;
import org.easetech.easytest.converter.ConverterManager;
import org.easetech.easytest.exceptions.ParamAssertionError;
import org.easetech.easytest.interceptor.InternalInterceptor;
import org.easetech.easytest.interceptor.InternalInvocationhandler;
import org.easetech.easytest.interceptor.MethodIntercepter;
import org.easetech.easytest.loader.DataConverter;
import org.easetech.easytest.loader.DataLoaderUtil;
import org.easetech.easytest.reports.data.ReportDataContainer;
import org.easetech.easytest.reports.data.TestResultBean;
import org.easetech.easytest.strategy.SchedulerStrategy;
import org.easetech.easytest.util.DataContext;
import org.easetech.easytest.util.RunAftersWithOutputData;
import org.easetech.easytest.util.TestInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Construct a new DataDrivenTestRunner. This runner is an extension of {@link BlockJUnit4ClassRunner}.
 * The algorithm of constructing and making the runner ready for execution is as
 * follows:<br>
 * <ul>
 * <li> Set the Scheduling Strategy. The {@link SchedulerStrategy} can be <b>PARALLEL</b> or <b>SERIAL</b> and is decided based on {@link Parallel} annotation</li>
 * <li>Load the Test Bean Configurations that are defined at the class level using the {@link TestConfigProvider}
 * annotation.<br>
 * (For details on how he test bean is loaded look at {@link TestConfigUtil#loadTestBeanConfig(Class)}) method.</li>
 * <li>Next, load the input test data, if any, at the class level. We mention if any because the input test data can be
 * loaded per method as well instead of loading it at the class level.<br>
 * (For details on how the input test data is loaded, look at {@link DataLoaderUtil#loadData}) method.</li>
 * <li>Next, we registers the converters, if any, declared at the class level using {@link Converters} annotation</li>
 * <li>We then move to inject the fields in the test class marked with {@link Provided} or {@link Inject} annotation
 * with the test beans that were loaded in step 1 above.</li>
 * <li>Next, we try to resolve any test properties that are specified on the test class using the {@link TestProperties}
 * annotation.<br>
 * Note that {@link TestProperties} annotation requires a filed of type {@link Properties} be defined at the class
 * level. Look at {@link TestConfigUtil#loadResourceProperties(Class, Object)} method for details.</li>
 * <li>We finally create a proxy for the class under test, if it is marked with {@link Intercept} annotation. This helps
 * in intercepting the method calls to the class under test and perform any initialization/destruction logic
 * before/after the method execution.</li>
 * 
 * @author Anuj Kumar
 */
public class DataDrivenTestRunner extends BlockJUnit4ClassRunner {

    /**
     * An instance of logger associated with the test framework.
     */
    private final Logger LOG = LoggerFactory.getLogger(DataDrivenTestRunner.class);

    /**
     * An instance of {@link Map} that contains the data to be written to the File
     */
    private Map<String, List<Map<String, Object>>> writableData = new HashMap<String, List<Map<String, Object>>>();

    /**
     * Convenient class member to get the list of {@link FrameworkMethod} that this runner will execute.
     */
    private final List<FrameworkMethod> frameworkMethods;

    /**
     * The report container which holds all the reporting data
     */
    private final ReportDataContainer testReportContainer;

    /**
     * Look at {@link DataDrivenTestRunner} for details.
     * 
     * @see DataDrivenTestRunner
     * @param klass
     * @throws InitializationError
     */
    public DataDrivenTestRunner(Class<?> klass) throws InitializationError {

        super(klass);
        Class<?> testClass = getTestClass().getJavaClass();
        // PENDING: This is not a nice way to define scheduler
        // but it is required at the moment because JUnit tests can run in parallel,
        // from either command line or from Maven plugins.
        // Have to look at a more robust solution.
        setSchedulingStrategy();
        loadBeanConfiguration();
        loadClassLevelData(klass);
        registerConverter(testClass.getAnnotation(org.easetech.easytest.annotation.Converters.class));
        try {
            // initialize report container class
            // TODO add condition whether reports must be switched on or off
            testReportContainer = new ReportDataContainer(getTestClass().getJavaClass());
            frameworkMethods = computeMethodsForTest();

        } catch (Exception e) {
            LOG.error("Exception occured while instantiating the EasyTestRunner. Exception is : ", e);
            throw new RuntimeException(e);
        }

    }
    
    /**
     * Get the instance of the class under test
     * @return the instance of class under test
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    protected Object getTestInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return getTestClass().getOnlyConstructor().newInstance();
    }
    
    /**
     * @see TestConfigUtil#loadTestConfigurations(Class, Object)
     */
    protected void loadTestConfigurations(Object testInstance) {
        TestConfigUtil.loadTestConfigurations(getTestClass().getJavaClass(), testInstance);
    }
    
    /**
     * @see TestConfigUtil#loadResourceProperties
     */
    protected void loadResourceProperties(Object testInstance) {
        TestConfigUtil.loadResourceProperties(getTestClass().getJavaClass(), testInstance);
    }
    
    /**
     * Set whether the tests should be run in parallel or serial.
     */
    protected void setSchedulingStrategy() {
        Class<?> testClass = getTestClass().getJavaClass();
        if (testClass.getAnnotation(Parallel.class) != null) {
            super.setScheduler(SchedulerStrategy.getScheduler(testClass));
        }
    }
    
    /**
     * @see TestConfigUtil#loadTestBeanConfig(Class)
     */
    protected void loadBeanConfiguration() {
        Class<?> testClass = getTestClass().getJavaClass();
        TestConfigUtil.loadTestBeanConfig(testClass);
    }
    
    /**
     * Load any class level test data
     * @see DataLoaderUtil#loadData(Class, FrameworkMethod, org.junit.runners.model.TestClass, Map)
     * @param klass
     */
    protected void loadClassLevelData(Class<?> klass) {
        DataLoaderUtil.loadData(klass, null, getTestClass(), writableData);
    }

    /**
     * Compute any test methods
     * @return a list of {@link FrameworkMethod}s
     */
    protected List<FrameworkMethod> computeTestMethods() {
        return frameworkMethods;
    }

    /**
     * Overridden the compute test method to make it save the method list as class instance, so that the method does not
     * run multiple times. Also, this method now is responsible for creating multiple {@link FrameworkMethod} instances
     * for a given method with multiple test data. So, if a given test method needs to run three times with three set of
     * input test data, then this method will actually create three instances of {@link FrameworkMethod}. In order to
     * allow the user to override the default name, {@link FrameworkMethod} is extended with {@link EasyFrameworkMethod}
     * and {@link EasyFrameworkMethod#setName(String)} method introduced.
     * 
     * @return list of {@link FrameworkMethod}
     */

    protected List<FrameworkMethod> computeMethodsForTest() {

        List<FrameworkMethod> finalList = new ArrayList<FrameworkMethod>();
        // Iterator<FrameworkMethod> testMethodsItr = super.computeTestMethods().iterator();
        Class<?> testClass = getTestClass().getJavaClass();

        List<FrameworkMethod> availableMethods = getTestClass().getAnnotatedMethods(Test.class);
        List<FrameworkMethod> methodsWithNoData = new ArrayList<FrameworkMethod>();
        List<FrameworkMethod> methodsWithData = new ArrayList<FrameworkMethod>();

        for (FrameworkMethod method : availableMethods) {
            // Try loading the data if any at the method level
            if (method.getAnnotation(DataLoader.class) != null) {
                DataLoaderUtil.loadData(null, method, getTestClass(), writableData);
                methodsWithData.add(method);
            } else {
                // Method does not have its own dataloader annotation
                // Does method need input data ??
                if (method.getMethod().getParameterTypes().length == 0) {
                    methodsWithNoData.add(method);
                } else {
                    // Does method have data already loaded?
                    boolean methodDataLoaded = DataLoaderUtil.isMethodDataLoaded(DataConverter
                        .getFullyQualifiedTestName(method.getName(), testClass));
                    if (methodDataLoaded) {
                        methodsWithData.add(method);
                    } else {
                        methodsWithNoData.add(method);
                    }
                }
            }
            // Next Try registering the converters, if any at the method level
            registerConverter(method.getAnnotation(Converters.class));

        }

        for (FrameworkMethod methodWithData : methodsWithData) {
            String superMethodName = DataConverter.getFullyQualifiedTestName(methodWithData.getName(), testClass);
            for (FrameworkMethod method : availableMethods) {
                if (superMethodName.equals(DataConverter.getFullyQualifiedTestName(method.getName(), testClass))) {
                    // Load the data,if any, at the method level
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
                        TestResultBean testResultBean = new TestResultBean(methodWithData.getMethod().getName(),
                            new Date());
                        testReportContainer.addTestResult(testResultBean);
                        // Create a new FrameworkMethod for each set of test data
                        EasyFrameworkMethod easyMethod = new EasyFrameworkMethod(method.getMethod(), testData,
                            testResultBean);
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
        for (FrameworkMethod fMethod : methodsWithNoData) {
            TestResultBean testResultBean = new TestResultBean(fMethod.getMethod().getName(), new Date());
            testReportContainer.addTestResult(testResultBean);
            EasyFrameworkMethod easyMethod = new EasyFrameworkMethod(fMethod.getMethod(), null, testResultBean);
            finalList.add(easyMethod);
        }
        if (finalList.isEmpty()) {
            Assert.fail("No method exists for the Test Runner");
        }
        return finalList;
    }
    
    /**
     * Override the filter method from {@link ParentRunner} so that individual 
     * tests can be run using EasyTest
     * @param filter
     * @throws NoTestsRemainException
     */
    public void filter(Filter filter) throws NoTestsRemainException {

        for (Iterator<FrameworkMethod> iter = frameworkMethods.iterator(); iter.hasNext(); ) {
            FrameworkMethod each = iter.next();
            if (shouldRun(filter, each))
                try {
                    filter.apply(each);
                } catch (NoTestsRemainException e) {
                    iter.remove();
                }
            else
                iter.remove();
        }
        if (frameworkMethods.isEmpty()) {
            throw new NoTestsRemainException();
        }
    }
    
    private boolean shouldRun(Filter filter, FrameworkMethod each) {
        return filter.shouldRun(describeFiltarableChild(each));
    }
    
    private Description describeFiltarableChild(FrameworkMethod each) {
        return Description.createTestDescription(getTestClass().getJavaClass(),
            each.getMethod().getName(), each.getAnnotations());
    }

    /**
     * Instrument the class's field that are marked with {@link Intercept} annotation
     * 
     * @param testClass the class under test
     * @throws IllegalArgumentException if an exception occurred
     * @throws IllegalAccessException if an exception occurred
     * @throws InstantiationException if an exception occurred
     */
    protected void instrumentClass(Class<?> testClass , Object testInstance) throws IllegalArgumentException, IllegalAccessException,
        InstantiationException {
        Field[] fields = testClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Intercept interceptor = field.getAnnotation(Intercept.class);
            if (interceptor != null) {
                Class<? extends MethodIntercepter> interceptorClass = interceptor.interceptor();
                // This is the field we want to enhance
                Class<?> fieldType = field.getType();

                Object fieldInstance = field.get(testInstance);
                Object proxiedObject = null;
                if (fieldType.isInterface()) {
                    LOG.debug("The field of type :" + fieldType + " will be proxied using JDK dynamic proxies.");

                    ClassLoader classLoader = determineClassLoader(fieldType, testClass);

                    Class<?>[] interfaces = { fieldType };
                    // use JDK dynamic proxy
                    InternalInvocationhandler handler = new InternalInvocationhandler();
                    handler.setUserIntercepter(interceptorClass.newInstance());
                    handler.setTargetInstance(fieldInstance);
                    proxiedObject = Proxy.newProxyInstance(classLoader, interfaces, handler);

                } else {
                    LOG.debug("The field of type :" + fieldType + " will be proxied using CGLIB proxies.");
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(fieldType);
                    InternalInterceptor cglibInterceptor = new InternalInterceptor();
                    cglibInterceptor.setTargetInstance(fieldInstance);
                    cglibInterceptor.setUserIntercepter(interceptorClass.newInstance());
                    enhancer.setCallback(cglibInterceptor);
                    proxiedObject = enhancer.create();
                }

                try {
                    if (proxiedObject != null) {
                        field.set(testInstance, proxiedObject);
                    }

                } catch (Exception e) {
                    LOG.error("Failed while trying to instrument the class for Intercept annotation with exception : ",
                        e);
                    Assert
                        .fail("Failed while trying to instrument the class for Intercept annotation with exception : "
                            + e);
                }
            }
        }
    }

    /**
     * Determine the right class loader to use to load the class
     * 
     * @param fieldType
     * @param testClass
     * @return the classLoader or null if none found
     */
    protected ClassLoader determineClassLoader(Class<?> fieldType, Class<?> testClass) {
        ClassLoader cl = testClass.getClassLoader();
        try {
            if (Class.forName(fieldType.getName(), false, cl) == fieldType) {
                return cl;
            } else {
                cl = Thread.currentThread().getContextClassLoader();
                if (Class.forName(fieldType.getName(), false, cl) == fieldType) {
                    return cl;
                }
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method responsible for registering the converters with the EasyTest framework
     * 
     * @param converter the annotation {@link Converters}
     */
    @SuppressWarnings("rawtypes")
    public void registerConverter(Converters converter) {
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
     * Override the name of the test. In case of EasyTest, it will be the name of the test method concatenated with the
     * input test data that the method will run with.
     * 
     * @param method the {@link FrameworkMethod}
     * @return an overridden test method Name
     */

    protected String testName(final FrameworkMethod method) {
        return String.format("%s", method.getName());
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
     * Adds to {@code errors} for each method annotated with {@code @Test}, {@code @Before}, or {@code @After} that is
     * not a public, void instance method with no arguments.
     * 
     * @deprecated unused API, will go away in future version
     */
    @Deprecated
    protected void validateInstanceMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);

        if (getTestClass().getAnnotatedMethods(Test.class).size() == 0)
            errors.add(new Exception("No runnable methods"));
    }

    /**
     * Validate the test methods.
     * 
     * @param errors list of any errors while validating test method
     */

    protected void validateTestMethods(List<Throwable> errors) {
        // Do Nothing as we now support public non void arg test methods
    }

    protected Statement methodBlock(FrameworkMethod method) {
        return withTestResult((EasyFrameworkMethod)method, super.methodBlock(method));
    }
    
    protected Statement withTestResult(final EasyFrameworkMethod method, final Statement statement) {
        return new Statement() {
            
            @Override
            public void evaluate() throws Throwable {
                TestResultBean testResult = method.getTestResult();
                try {
                    statement.evaluate();
                    testResult.setPassed(Boolean.TRUE);
                } catch (Throwable e) {

                    if (e instanceof AssertionError) { // Assertion error
                        testResult.setPassed(Boolean.FALSE);
                        testResult.setResult(e.getMessage()); 
                        throw new ParamAssertionError(e, method.getName());

                    } else { // Exception
                        testResult.setException(Boolean.TRUE);
                        testResult.setExceptionResult(e.toString());
                        throw e;

                    }
                   
                }
                
            }
        };
    }

    /**
     * Returns a new fixture for running a test. Default implementation executes
     * the test class's no-argument constructor (validation should have ensured
     * one exists).
     */
    protected Object createTest() throws Exception {
        Object testInstance =  getTestClass().getOnlyConstructor().newInstance();
        loadTestConfigurations(testInstance);
        loadResourceProperties(testInstance);
        instrumentClass(getTestClass().getJavaClass() , testInstance);
        return testInstance;
        
    }

    /**
     * Returns a {@link Statement} that invokes {@code method} on {@code test}
     */
    protected Statement methodInvoker(FrameworkMethod method, Object testInstance) {
        return new InternalParameterizedStatement((EasyFrameworkMethod) method, getTestClass(), testInstance);
    }

    /**
     * Returns a {@link Statement}: run all non-overridden {@code @AfterClass} methods on this class and superclasses
     * before executing {@code statement}; all AfterClass methods are always executed: exceptions thrown by previous
     * steps are combined, if necessary, with exceptions from AfterClass methods into a {@link MultipleFailureException}
     * .
     * 
     * This method is also responsible for writing the data to the output file in case the user is returning test data
     * from the test method. This method will make sure that the data is written to the output file once after the
     * Runner has completed and not for every instance of the test method.
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
                testInfo = DataLoaderUtil.determineLoader(loaderAnnotation, getTestClass());

            } else {
                loaderAnnotation = getTestClass().getJavaClass().getAnnotation(DataLoader.class);
                if (loaderAnnotation != null) {
                    testInfo = DataLoaderUtil.determineLoader(loaderAnnotation, getTestClass());
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
    
    

}
