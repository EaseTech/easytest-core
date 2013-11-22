package org.easetech.easytest.runner;

import java.util.ArrayList;

import org.easetech.easytest.internal.SystemProperties;

import org.easetech.easytest.reports.data.ReportDataContainer;

import java.util.Date;
import org.easetech.easytest.annotation.Repeat;
import org.easetech.easytest.reports.data.TestResultBean;
import org.easetech.easytest.util.DataContext;
import org.junit.Assert;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.loader.DataConverter;
import org.junit.Test;

import org.junit.runners.model.TestClass;

import java.util.List;

import java.util.Map;
import org.easetech.easytest.loader.DataLoaderUtil;
import org.junit.runners.model.FrameworkMethod;

import org.easetech.easytest.annotation.TestPolicy;
import org.easetech.easytest.strategy.SchedulerStrategy;

import org.junit.runners.model.RunnerScheduler;

/**
 * A utility class that doctrines JUnit Runners to EasyTest Runners
 *
 */
public class RunnerUtil {

    public static RunnerScheduler getScheduler(Class<?> testClass) {
        RunnerScheduler scheduler = null;
        TestPolicy testPolicy = testClass.getAnnotation(TestPolicy.class);
        if(testPolicy != null) {
            Class<?> policyClass = testPolicy.value();
            scheduler = SchedulerStrategy.getScheduler(policyClass , false);
            RunnerScheduler testClassScheduler = SchedulerStrategy.getScheduler(testClass , true);
            if(testClassScheduler != null) {
                scheduler = testClassScheduler;
            }
            
        } else {
            scheduler = SchedulerStrategy.getScheduler(testClass , false);
        }
        return scheduler;
    }
    
    /**
     * @param testClass 
     * @see TestConfigUtil#loadTestBeanConfig(Class)
     */
    public static void loadBeanConfiguration(Class<?> testClass) {
        TestPolicy testPolicy = testClass.getAnnotation(TestPolicy.class);
        if(testPolicy != null) {
            TestConfigUtil.loadTestBeanConfig(testPolicy.value());
        }
        TestConfigUtil.loadTestBeanConfig(testClass);
    }
    
    /**
     * Load any class level test data
     * 
     * @see DataLoaderUtil#loadData(Class, FrameworkMethod, org.junit.runners.model.TestClass, Map)
     * @param klass
     * @param testClass 
     * @param writableData 
     */
    public static void loadClassLevelData(Class<?> klass , TestClass testClass , Map<String, List<Map<String, Object>>> writableData) {
        TestPolicy testPolicy = testClass.getJavaClass().getAnnotation(TestPolicy.class);
        if(testPolicy != null) {
            DataLoaderUtil.loadData(testPolicy.value(), null, testClass, writableData);
        }
        DataLoaderUtil.loadData(klass, null, testClass, writableData);
    }
    
    public static void categorizeTestMethods(List<FrameworkMethod> methodsWithNoData, List<FrameworkMethod> methodsWithData , TestClass testClazz , Map<String, List<Map<String, Object>>> writableData) {
        List<FrameworkMethod> availableMethods = testClazz.getAnnotatedMethods(Test.class);

        Class<?> testClass = testClazz.getJavaClass();
        for (FrameworkMethod method : availableMethods) {
            // Try loading the data if any at the method level
            if (method.getAnnotation(DataLoader.class) != null) {
                DataLoaderUtil.loadData(null, method, testClazz, writableData);
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
        }
    }
    
    public static void handleMethodsWithData(List<FrameworkMethod> methodsWithData, List<FrameworkMethod> finalList , TestClass testClazz , ReportDataContainer testReportContainer) {
        Class<?> testClass = testClazz.getJavaClass();
        List<FrameworkMethod> availableMethods = testClazz.getAnnotatedMethods(Test.class);
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
                        Repeat repeatTests = method.getAnnotation(Repeat.class);
                        if (repeatTests != null || getRepeatCount() != null) {
                            int repeatCount = getRepeatCount() != null ? getRepeatCount() : repeatTests.times();
                            for (int count = 0; count < repeatCount; count++) {
                                TestResultBean testResultBean = new TestResultBean(
                                    methodWithData.getMethod().getName(), new Date());
                                testReportContainer.addTestResult(testResultBean);
                                // Create a new FrameworkMethod for each set of test data
                                EasyFrameworkMethod easyMethod = new EasyFrameworkMethod(method.getMethod(), testData,
                                    testResultBean, method.getName().concat(testData.toString()));
                                easyMethod.setName(method.getName().concat("_").concat(String.valueOf(count))
                                    .concat(testData.toString()));
                                finalList.add(easyMethod);
                            }
                        } else {
                            TestResultBean testResultBean = new TestResultBean(methodWithData.getMethod().getName(),
                                new Date());
                            testReportContainer.addTestResult(testResultBean);
                            // Create a new FrameworkMethod for each set of test data
                            EasyFrameworkMethod easyMethod = new EasyFrameworkMethod(method.getMethod(), testData,
                                testResultBean, method.getName().concat(testData.toString()));
                            easyMethod.setName(method.getName().concat(testData.toString()));
                            finalList.add(easyMethod);
                        }

                    }

                    // Since the runner only ever handles a single method, we break out of the loop as soon as we
                    // have
                    // found our method.
                    break;
                }
            }
        }
    }
    
    public static Integer getRepeatCount() {
        Integer count = null;
        String repeatCount = System.getProperty(SystemProperties.REPEAT_COUNT.getValue());
        if(repeatCount != null) {
            count = Integer.valueOf(repeatCount);
        }
        return count;
        
    }
    
    public static void handleMethodsWithNoData(List<FrameworkMethod> methodsWithNoData, List<FrameworkMethod> finalList , ReportDataContainer testReportContainer) {
        for (FrameworkMethod fMethod : methodsWithNoData) {

            Repeat repeatTests = fMethod.getAnnotation(Repeat.class);
            if (repeatTests != null || getRepeatCount() != null) {
                int repeatCount = getRepeatCount() != null ? getRepeatCount() : repeatTests.times();
                for (int count = 0; count < repeatCount; count++) {
                    TestResultBean testResultBean = new TestResultBean(fMethod.getMethod().getName(), new Date());
                    testReportContainer.addTestResult(testResultBean);
                    // Create a new FrameworkMethod for each set of test data
                    EasyFrameworkMethod easyMethod = new EasyFrameworkMethod(fMethod.getMethod(), null, testResultBean,
                        fMethod.getName());
                    easyMethod.setName(fMethod.getName().concat("_").concat(String.valueOf(count)));
                    finalList.add(easyMethod);
                }
            } else {
                TestResultBean testResultBean = new TestResultBean(fMethod.getMethod().getName(), new Date());
                testReportContainer.addTestResult(testResultBean);
                EasyFrameworkMethod easyMethod = new EasyFrameworkMethod(fMethod.getMethod(), null, testResultBean,
                    fMethod.getName());
                finalList.add(easyMethod);
            }

        }
    }
    
    public static List<FrameworkMethod> testMethods(TestClass testClazz , ReportDataContainer testReportContainer , Map<String, List<Map<String, Object>>> writableData) {
        List<FrameworkMethod> finalList = new ArrayList<FrameworkMethod>();
        List<FrameworkMethod> methodsWithNoData = new ArrayList<FrameworkMethod>();
        List<FrameworkMethod> methodsWithData = new ArrayList<FrameworkMethod>();
        categorizeTestMethods(methodsWithNoData, methodsWithData, testClazz, writableData);
        handleMethodsWithData(methodsWithData, finalList, testClazz, testReportContainer);
        handleMethodsWithNoData(methodsWithNoData, finalList , testReportContainer);
        return finalList;
    }
    
    /**
     * Determine the right class loader to use to load the class
     * 
     * @param fieldType
     * @param testClass
     * @return the classLoader or null if none found
     */
    public static ClassLoader determineClassLoader(Class<?> fieldType, Class<?> testClass) {
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
}
