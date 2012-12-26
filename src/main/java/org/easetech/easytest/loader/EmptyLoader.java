
package org.easetech.easytest.loader;

import java.util.List;
import java.util.Map;
import org.easetech.easytest.io.Resource;

/**
 * 
 * An empty loader implementation
 * 
 */
public class EmptyLoader implements Loader {

    public Map<String, List<Map<String, Object>>> loadData(Resource resource) {
        // TODO Auto-generated method stub
        return null;
    }

    public void writeData(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodNames) {
        // TODO Auto-generated method stub
        
    }
}
