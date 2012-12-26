
package org.easetech.easytest.config;

import java.lang.reflect.Field;
import org.easetech.easytest.annotation.Provided;
import org.easetech.easytest.util.ConfigContext;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class to load the Configuration test beans and associating the {@link Provided} annotated fields with the test beans.
 * 
 */
public class ConfigLoader {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

    /**
     * Load the test configurations for the test class and associated 
     * the bean instance with the fields annotated with {@link Provided} annotation
     * @param testClass the test class on which the fields needs to be set
     * @param testInstance a test instance of the test class
     */
    public static void loadTestConfigurations(Class<?> testClass, Object testInstance) {
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
                    LOG.debug("Field {} is being set with the instance {}", field.getName(), beanInstance);
                    field.setAccessible(true);
                    field.set(testInstance, beanInstance);
                } catch (Exception e) {
                    Assert.fail("Failed while trying to handle Provider annotation for Field : "
                        + field.getDeclaringClass() + e.getStackTrace());
                }

            }
        }
    }

}
