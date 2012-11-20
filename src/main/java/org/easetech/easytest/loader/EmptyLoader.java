
package org.easetech.easytest.loader;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

/**
 * 
 * An empty loader implementation
 * 
 */
public class EmptyLoader implements Loader {

    /**
     * Return an empty map
     * 
     * @param filePaths
     * @return the data to be consumed by the framework
     */
    @Override
    public Map<String, List<Map<String, Object>>> loadData(String[] filePaths) {
        return new HashMap<String, List<Map<String, Object>>>();
    }

    @Override
    public void writeData(String[] filePaths, String methodName, Map<String, List<Map<String, Object>>> actualData) {
       //do nothing

    }
}
