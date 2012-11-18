package org.easetech.easytest.example;

import java.util.Map;

import org.easetech.easytest.converter.AbstractConverter;

public class DelayedObjectConverter extends AbstractConverter<DelayedObject>{

    /**
     * @param convertFrom
     * @return
     */
    @Override
    public DelayedObject convert(Map<String, Object> convertFrom) {
        
        return new DelayedObject(Long.valueOf((String)convertFrom.get("items")));
    }

}
