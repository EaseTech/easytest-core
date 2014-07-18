
package org.easetech.easytest.loader;

import junit.framework.Assert;

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
        Assert.fail("There is no loader configured to handle loading of the resource :" + resource.getResourceName()+ 
            "Please provide either file with extensions : " +
            "xsl, csv, xml or provide your own custom data loader implementation for the resource.");
        return null;
    }

    public void writeData(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodNames) {
        Assert.fail("There is no loader configured to handle writing of the resource :" + resource.getResourceName()+ 
            "Please provide either file with extensions : " +
            "xsl, csv, xml or provide your own custom data loader implementation for the resource.");
        
    }
}
