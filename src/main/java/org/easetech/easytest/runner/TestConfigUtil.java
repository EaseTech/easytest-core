
package org.easetech.easytest.runner;

import javax.inject.Named;

import javax.inject.Inject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import org.easetech.easytest.annotation.Provided;
import org.easetech.easytest.annotation.TestBean;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.annotation.TestProperties;
import org.easetech.easytest.io.Resource;
import org.easetech.easytest.io.ResourceLoader;
import org.easetech.easytest.io.ResourceLoaderStrategy;
import org.easetech.easytest.util.ConfigContext;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for Test Configuration based processes
 * 
 * @author Anuj Kumar
 * 
 */
public final class TestConfigUtil {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(TestConfigUtil.class);

    /**
     * Load the test configurations using the provided {@link TestConfigProvider} annotation values
     * 
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
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Load the config beans from the given config classes
     * 
     * @param configClasses
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static void loadConfigBeans(Class<?>... configClasses) throws IllegalArgumentException,
        IllegalAccessException, InvocationTargetException, InstantiationException {

        for (Class<?> configClass : configClasses) {
            Object classInstance = configClass.newInstance();
            loadResourceProperties(configClass, classInstance);

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

    /**
     * Load the resource properties specified by {@link TestProperties} annotation
     * 
     * @param configClass
     * @param classInstance
     */
    public static void loadResourceProperties(Class<?> configClass, Object classInstance) {
        TestProperties resource = configClass.getAnnotation(TestProperties.class);
        if (resource != null) {
            Properties properties = getProperties(resource, configClass);
            setPropertiesFields(configClass, classInstance, properties);
        } else {
            setPropertiesFields(configClass, classInstance, null);
        }

    }

    private static void setPropertiesFields(Class<?> configClass, Object classInstance, Properties properties) {
        Field[] fields = configClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(Properties.class)) {
                field.setAccessible(true);
                try {
                    Properties fieldProperties = null;
                    // If the annotation TestProperties is present at the field level then it gets the priority
                    TestProperties fieldResource = field.getAnnotation(TestProperties.class);
                    if (fieldResource != null) {
                        fieldProperties = getProperties(fieldResource, configClass);
                    }
                    field.set(classInstance, fieldProperties != null ? fieldProperties : properties);

                } catch (Exception e) {
                    LOG.error(
                        "An exception occured while trying to set the Properties instance on the class {}. Exception is : {}",
                        configClass, e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static Properties getProperties(TestProperties resource, Class<?> configClass) {
        java.util.Properties properties = new java.util.Properties();
        ResourceLoader resourceLoader = new ResourceLoaderStrategy(configClass);
        for (String resourcePath : resource.value()) {
            Resource fileResource = resourceLoader.getResource(resourcePath);
            if (fileResource.exists()) {

                try {
                    properties.load(fileResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("IOException occured while trying to load the properties from file : "
                        + resourcePath, e);
                }
            } else {
                Assert.fail("Properties file with path " + resourcePath + " does not exist.");
            }

        }
        return properties;
    }

    /**
     * Load the test configurations for the test class and associate the bean instance with the fields annotated with
     * {@link Provided} or {@link Inject} and/or {@link Named} annotation
     * 
     * @param testClass the test class on which the fields needs to be set
     * @param testInstance a test instance of the test class
     */
    public static void loadTestConfigurations(Class<?> testClass, Object testInstance) {
        Field[] fields = testClass.getDeclaredFields();
        for (Field field : fields) {
            Provided providedAnnotation = field.getAnnotation(Provided.class);
            if (providedAnnotation != null) {
                String providerBeanName = providedAnnotation.value();
                injectTestBean(providerBeanName, field, testInstance);
            } else {
                Inject injectAnnotation = field.getAnnotation(Inject.class);
                if (injectAnnotation != null) {
                    // Check if it is Named wiring
                    Named namedInjection = field.getAnnotation(Named.class);
                    String providerBeanName = null;
                    if (namedInjection != null) {
                        providerBeanName = namedInjection.value();
                    }
                    injectTestBean(providerBeanName, field, testInstance);

                }
            }
        }
    }

    private static void injectTestBean(String providerBeanName, Field field, Object testInstance) {
        Object beanInstance = null;
        if (!(providerBeanName == null) && !(providerBeanName.length() <= 0)) {
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
            LOG.debug("Field {} is being set with the instance {}", field.getName(), beanInstance);
            field.setAccessible(true);
            field.set(testInstance, beanInstance);
        } catch (Exception e) {
            Assert.fail("Failed while trying to handle Provider annotation for Field : " + field.getDeclaringClass()
                + e.getStackTrace());
        }
    }

}
