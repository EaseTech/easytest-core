
package org.easetech.easytest.runner;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.easetech.easytest.annotation.Converters;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.converter.Converter;
import org.easetech.easytest.converter.ConverterManager;
import org.easetech.easytest.loader.DataConverter;
import org.easetech.easytest.loader.DataLoaderUtil;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Base {@link Suite} class for EasyTest based Runners. This class abstracts a lot of boiler plate logic so as to give
 * EasyTest Runners convenient and single place for reference.
 * 
 * @author Anuj Kumar
 * 
 */
public class BaseSuite extends Suite {

    /**
     * An instance of {@link Map} that contains the data to be written to the File
     */
    protected static Map<String, List<Map<String, Object>>> writableData = new HashMap<String, List<Map<String, Object>>>();

    /**
     * A List of {@link DataDrivenTestRunner.EasyTestRunner}s.
     */
    protected final List<Runner> runners = new ArrayList<Runner>();

    /**
     * List of {@link FrameworkMethod} that does not have any external test data associated with them.
     */
    protected List<FrameworkMethod> methodsWithNoData = new ArrayList<FrameworkMethod>();

    /**
     * List of {@link FrameworkMethod} that does have any external test data associated with them.
     */
    protected List<FrameworkMethod> methodsWithData = new ArrayList<FrameworkMethod>();

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
     * Construct a new BaseSuite
     * @param klass the class under test
     * @throws InitializationError if any error occurs
     */
    protected BaseSuite(Class<?> klass) throws InitializationError {
        super(klass, Collections.<Runner> emptyList());
        Class<?> testClass = getTestClass().getJavaClass();
        // Load TestBeanConfigurations if any
        TestConfigUtil.loadTestBeanConfig(testClass);
        // Load the data at the class level, if any.
        DataLoaderUtil.loadData(klass, null, getTestClass(), writableData);
        // Registering Converters based on @Converters annotation
        registerConverter(testClass.getAnnotation(org.easetech.easytest.annotation.Converters.class));
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
                // Does method have data already loaded?
                boolean methodDataLoaded = DataLoaderUtil.isMethodDataLoaded(DataConverter.getFullyQualifiedTestName(
                    method.getName(), testClass));
                if (methodDataLoaded) {
                    methodsWithData.add(method);
                } else {
                    methodsWithNoData.add(method);
                }
            }
            //Next Try registering the converters, if any at the method level
            registerConverter(method.getAnnotation(Converters.class));

        }
        // Finally create a runner for methods that do not have Data specified with them.
        // These are potentially the methods with no method parameters and with @Test annotation.
        if (!methodsWithNoData.isEmpty()) {
            this.methodsWithNoData = methodsWithNoData;

        }
        if (!methodsWithData.isEmpty()) {
            this.methodsWithData = methodsWithData;
        }

    }

    /**
     * Method responsible for registering the converters with the EasyTest framework
     * 
     * @param converter the annotation {@link Converters}
     */
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

}
