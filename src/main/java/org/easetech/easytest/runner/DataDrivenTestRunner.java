
package org.easetech.easytest.runner;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.cglib.proxy.Enhancer;
import org.easetech.easytest.annotation.Converters;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Intercept;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.Provided;
import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.annotation.TestBean;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.config.ConfigLoader;
import org.easetech.easytest.converter.Converter;
import org.easetech.easytest.converter.ConverterManager;
import org.easetech.easytest.interceptor.InternalInterceptor;
import org.easetech.easytest.interceptor.InternalInvocationhandler;
import org.easetech.easytest.interceptor.MethodIntercepter;
import org.easetech.easytest.loader.DataConverter;
import org.easetech.easytest.loader.DataLoaderUtil;
import org.easetech.easytest.reports.data.ReportDataContainer;
import org.easetech.easytest.reports.data.TestResultBean;
import org.easetech.easytest.util.DataContext;
import org.easetech.easytest.util.RunAftersWithOutputData;
import org.easetech.easytest.util.TestInfo;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
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
public class DataDrivenTestRunner extends BaseSuite {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger PARAM_LOG = LoggerFactory.getLogger(DataDrivenTestRunner.class);
    


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
    public DataDrivenTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        runners.add(new EasyTestRunner(klass));
    }

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
     *  Finally, EasyTest also supports {@link Intercept} annotation. This annotation can be used to intercept
     *         calls to the test subject that is currently being tested. For eg. if you want to capture how much time a
     *         particular method of the actual service class is taking, then you can mark the field representing the
     *         testSubject with {@link Intercept} annotation. The framework also provides convenient way to write your
     *         own custom method interceptors.
     *         
     *         @author Anuj Kumar
     */
    public class EasyTestRunner extends BlockJUnit4ClassRunner {

        /**
         * Convenient class member to get the list of {@link FrameworkMethod} that this runner will execute.
         */
        List<FrameworkMethod> frameworkMethods;

        /**
         * The actual instance of the test class. This is extremely handy in cases where we want to reflectively set
         * instance fields on a test class.
         */
        Object testInstance;
       
        /**
         * The report container which holds all the reporting data
         */
        private ReportDataContainer testReportContainer = null;

        /**
         * Instance of {@link TestResultBean} containing result for a single execution of test method
         */
        TestResultBean testResult;

        /**
         * 
         * Construct a new DataDrivenTestRunner.
         * We first inject the TestBeans, if any is specified by the user using {@link Provided} annotation.
         * Note that {@link Provided} annotation works, iff tha user has provided {@link TestConfigProvider} annotation 
         * at the class level with a valid TestConfig class that has public methods marked with {@link TestBean} annotation.  
         * Next, we try instrument the fields that are marked with {@link Intercept} annotation.
         * Finally, we instantiate the container for report generation logic, if the user has provided {@link Report} annotation at the class level.
         * @param klass the test class whose test methods needs to be executed
         * @throws InitializationError if any error occurs
         */
        public EasyTestRunner(Class<?> klass) throws InitializationError {
            super(klass);
            try {
                testInstance = getTestClass().getOnlyConstructor().newInstance();
                ConfigLoader.loadTestConfigurations(getTestClass().getJavaClass(), testInstance);
                instrumentClass(getTestClass().getJavaClass());
                // initialize report container class
                // TODO add condition whether reports must be switched on or off
                testReportContainer = new ReportDataContainer(getTestClass().getJavaClass());

            } catch (Exception e) {
                PARAM_LOG.error("Exception occured while instantiating the EasyTestRunner. Exception is : ", e);
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
                field.setAccessible(true);
                Intercept interceptor = field.getAnnotation(Intercept.class);
                if (interceptor != null) {
                    Class<? extends MethodIntercepter> interceptorClass = interceptor.interceptor();
                    // This is the field we want to enhance
                    Class<?> fieldType = field.getType();
                    
                    Object fieldInstance = field.get(testInstance);
                    Object proxiedObject = null;
                    if (fieldType.isInterface()) {
                        PARAM_LOG
                        .debug("The field of type :"
                            + fieldType + " will be proxied using JDK dynamic proxies.");
                        
                        ClassLoader classLoader = determineClassLoader(fieldType , testClass);
                        
                        Class<?>[] interfaces = {fieldType};
                        //use JDK dynamic proxy
                        InternalInvocationhandler handler = new InternalInvocationhandler();
                        handler.setUserIntercepter(interceptorClass.newInstance());
                        handler.setTargetInstance(fieldInstance);
                        proxiedObject = Proxy.newProxyInstance(classLoader, interfaces, handler);
                        
                    } else {
                        PARAM_LOG
                        .debug("The field of type :"
                            + fieldType + " will be proxied using CGLIB proxies.");
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
                        PARAM_LOG.error("Failed while trying to instrument the class for Intercept annotation with exception : ", e);
                        Assert
                            .fail("Failed while trying to instrument the class for Intercept annotation with exception : "
                                + e);
                    }
                }
            }
        }
        
        /**
         * Determine the right class loader to use to load the class
         * @param fieldType
         * @param testClass
         * @return the classLoader or null if none found
         */
        protected ClassLoader determineClassLoader(Class<?> fieldType , Class<?> testClass){
            ClassLoader cl = testClass.getClassLoader();
            try {
                if(Class.forName(fieldType.getName(), false, cl) == fieldType){
                    return cl;
                }else{
                    cl = Thread.currentThread().getContextClassLoader();
                    if(Class.forName(fieldType.getName(), false, cl) == fieldType){
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
                        DataLoaderUtil.loadData(null, method, getTestClass(), writableData);
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
            return new InternalParameterizedStatement(method, testResult, testReportContainer, writableData, getTestClass(), testInstance);
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

}
