
package org.easetech.easytest.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.io.Resource;
import org.easetech.easytest.loader.Loader;

/**
 * 
 * A Custom Data Loader that simply provides data from the class itself without reading any external files.
 * 
 */
public class CustomObjectDataLoader implements Loader {


    public Map<String, List<Map<String, Object>>> loadData(Resource resource) {
        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map1 = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<String, Object>();
        Item item1 = new Item (1, "Item1");
        Item item2= new Item (1, "Item2");
        map1.put("Item", item1);
        map2.put("Item", item2);
        list.add(map1);
        list.add(map2);
        result.put("testGetItemsWithCustomLoader", list);
        return result;
    }

    public void writeData(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodName) {
        // TODO Auto-generated method stub
        
    }

}
