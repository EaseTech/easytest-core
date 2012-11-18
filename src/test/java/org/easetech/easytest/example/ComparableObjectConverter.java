package org.easetech.easytest.example;

import java.util.Map;

import org.easetech.easytest.converter.AbstractConverter;

public class ComparableObjectConverter extends AbstractConverter<ComparableObject> {

    /**
     * @param convertFrom
     * @return
     */
    @Override
    public ComparableObject convert(Map<String, Object> convertFrom) {
        String items = (String)convertFrom.get("items");
        return new ComparableObject(Long.valueOf(items));
        
    }

}
