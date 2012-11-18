package org.easetech.easytest.example;

import org.easetech.easytest.example.EnumObject.Workingday;

import java.util.Map;

import org.easetech.easytest.converter.AbstractConverter;

public class EnumConverter extends AbstractConverter<Workingday> {

    /**
     * @param convertFrom
     * @return
     */
    @Override
    public Workingday convert(Map<String, Object> convertFrom) {
        
        return Workingday.valueOf((String)convertFrom.get("items"));

    }

}
