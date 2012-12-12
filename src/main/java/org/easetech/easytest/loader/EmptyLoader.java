
package org.easetech.easytest.loader;

import org.easetech.easytest.io.Resource;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

/**
 * 
 * An empty loader implementation
 * 
 */
public class EmptyLoader implements Loader {

    @Override
    public Map<String, List<Map<String, Object>>> loadData(Resource resource) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void writeData(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodNames) {
        // TODO Auto-generated method stub
        
    }
}
