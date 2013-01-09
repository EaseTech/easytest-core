package org.easetech.easytest.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.easetech.easytest.annotation.TestBean;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.util.ConfigContext;
import org.junit.Assert;

/**
 * A utility class for Test Configuration based processes
 * 
 * @author Anuj Kumar
 * 
 */
public final class TestConfigUtil {
    
    /**
     * Load the test configurations using the provided {@link TestConfigProvider} annotation values
     * @param testClass
     */
    @SuppressWarnings("cast")
    public static void loadTestBeanConfig(Class<?> testClass) {
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

    /**
     * Load the config beans from the given config classes
     * @param configClasses
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static void loadConfigBeans(Class<?>... configClasses) throws IllegalArgumentException, IllegalAccessException,
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
                    if (!(beanName.length() <= 0)) {
                        ConfigContext.setTestBeanByName(beanName, object);
                    } else {
                        ConfigContext.setTestBeanByType(beanType, object);
                    }
                }
            }
        }
    }

}
