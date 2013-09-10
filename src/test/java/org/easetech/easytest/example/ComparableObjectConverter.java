package org.easetech.easytest.example;

import java.util.Map;

import org.easetech.easytest.converter.AbstractConverter;

public class ComparableObjectConverter extends AbstractConverter<ComparableObject> {

    /**
     * @param convertFrom
     * @return
     */
    public ComparableObject convert(Map<String, Object> convertFrom, String paramName) {
        String items = (String)convertFrom.get("items");
        return new ComparableObject(Long.valueOf(items));
        
    }

}
