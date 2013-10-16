package org.easetech.easytest.example;

import java.util.Map;
import org.easetech.easytest.converter.AbstractConverter;
import org.easetech.easytest.example.EnumObject.Workingday;

public class EnumConverter extends AbstractConverter<Workingday> {

    /**
     * @param convertFrom
     * @return
     */
    public Workingday convert(Map<String, Object> convertFrom) {
        
        return Workingday.valueOf((String)convertFrom.get("items"));

    }

}
