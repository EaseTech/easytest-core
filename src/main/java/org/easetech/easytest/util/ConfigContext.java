
package org.easetech.easytest.util;

import org.easetech.easytest.annotation.TestConfigProvider;

import java.util.HashMap;
import java.util.Map;
import org.easetech.easytest.annotation.TestBean;

/**
 * A context class for holding the Configuration beans declared by the user. This class provides a utility to identify a
 * test bean declared using {@link TestBean} annotations inside a class that is decalred in the test class using {@link TestConfigProvider}
 * 
 */
public final class ConfigContext {

    private ConfigContext() {
        // do nothing
    }

    /**
     * The threadlocal variable that is responsible for containing the bean name to object instance mapping
     */
    public static final ThreadLocal<Map<String, Object>> beansByName = new ThreadLocal<Map<String, Object>>();

    /**
     * The threadlocal variable that is responsible for containing the bean type to object instance mapping
     */
    public static final ThreadLocal<Map<Class, Object>> beansByType = new ThreadLocal<Map<Class, Object>>();

    /**
     * Set the test bean in the threadlocal variable by name
     * @param beanName the name of the bean
     * @param beanInstance the instance to set
     */
    public static void setTestBeanByName(String beanName, Object beanInstance) {
        Map<String, Object> beanNamesMap = beansByName.get();
        if (beanNamesMap == null) {
            beanNamesMap = new HashMap<String, Object>();
        }
        beanNamesMap.put(beanName, beanInstance);
        beansByName.set(beanNamesMap);

    }
    
    /**
     * Set the test bean in the threadlocal variable by type
     * @param beanType the type of the bean
     * @param beanInstance the instance to set
     */
    public static void setTestBeanByType(Class beanType , Object beanInstance){
        Map<Class, Object> beanTypesMap = beansByType.get();
        if (beanTypesMap == null) {
            beanTypesMap = new HashMap<Class, Object>();
        }
        beanTypesMap.put(beanType, beanInstance);
        beansByType.set(beanTypesMap);
    }
    
    /**
     * Get the bean by name
     * @param beanName the name of the bean to get value for
     * @return the bean instance or null if not found
     */
    public static Object getBeanByName(String beanName){
        Map<String, Object> beanNamesMap = beansByName.get();
        if(beanNamesMap == null){
            return null;
        }
        return beanNamesMap.get(beanName);
    }
    
    /**
     * Get the bean by type
     * @param beanType the type of bean to get 
     * @return the instance of the bean
     */
    public static Object getBeanByType(Class beanType){
        Map<Class, Object> beanTypesMap = beansByType.get();
        if(beanTypesMap == null){
            return null;
        }
        return beanTypesMap.get(beanType);
    }

}
