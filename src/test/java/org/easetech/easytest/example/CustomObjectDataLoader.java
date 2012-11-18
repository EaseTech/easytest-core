
package org.easetech.easytest.example;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.easetech.easytest.loader.Loader;

/**
 * 
 * A Custom Data Loader that simply provides data from the class itself without reading any external files.
 * 
 */
public class CustomObjectDataLoader implements Loader {

    @Override
    public Map<String, List<Map<String, Object>>> loadData(String[] filePaths) {
        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("LibraryId", new LibraryId(1L));
        list.add(map);
        result.put("testGetItemsWithCustomLoader", list);
        return result;
    }

    @Override
    public void writeData(String[] filePaths, String methodName, Map<String, List<Map<String, Object>>> actualData) {
        // TODO Auto-generated method stub

    }

}
