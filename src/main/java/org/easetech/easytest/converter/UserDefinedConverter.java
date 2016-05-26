package org.easetech.easytest.converter;

import org.easetech.easytest.internal.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * An instance of {@link BaseConverter} that is responsible for converting the raw data in to a user defined type instance 
 * using any user defined converter
 * 
 * @author Anuj Kumar
 *
 */
public class UserDefinedConverter implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {

    /**
     * The type of parameter to convert the raw data to
     */
    private final Class<?> parameterType;

    /**
     * The name of the parameter that is being converted
     */
    private final String paramName;
    
    /**
     * Whether empty values should be converted to Null values or not
     */
    private final Boolean convertEmptyToNull;
    
    /**
     * The user specified date time format to use
     */
    private final DateTimeFormat dateTimeFormat;
    
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(UserDefinedConverter.class);
    
    /**
     * 
     * Construct a new UserDefinedConverter
     * @param parameterType The type of parameter to convert the raw data to
     * @param paramName The name of the parameter that is being converted
     * @param convertEmptyToNull 
     * @param dateTimeFormat 
     */
    public UserDefinedConverter(Class<?> parameterType, String paramName , Boolean convertEmptyToNull, DateTimeFormat dateTimeFormat) {
        this.parameterType = parameterType;
        this.paramName = paramName;
        this.convertEmptyToNull = convertEmptyToNull;
        this.dateTimeFormat = dateTimeFormat;
    }
    
    /**
     * Convert the raw data into a list of {@link PotentialAssignment} instance 
     * that EasyTest uses to provide the right set of test data to the test method
     * @param convertFrom the raw data to convert from 
     * @return the list of {@link PotentialAssignment} instances
     */
    @SuppressWarnings("unchecked")
    public List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom) {
        List<PotentialAssignment> potentialAssignments = null;
        LOG.debug("Trying to find a registerd converter for class {}", parameterType);
        // Try to find the Converter
        Converter<?> converter = ConverterManager.findConverter(parameterType);
        if (converter != null) {
            if(converter instanceof AbstractConverter) {
                ConverterSupport converterSupport = new ConverterSupport();
                converterSupport.setConvertEmptyToNull(convertEmptyToNull);
                converterSupport.setDateTimeFormat(dateTimeFormat);
                converterSupport.setParamName(paramName);
                ((AbstractConverter) converter).setConverterSupport(converterSupport);
            }
            
            potentialAssignments = new ArrayList<PotentialAssignment>();
            LOG.debug("Converter for class {} found.", parameterType);
            for (Map<String, Object> object : convertFrom) {
                Object value;
                //TODO: This logic should be removed in future versions of EasyTest
                if (converter instanceof ParamAwareConverter) {
                    value = ((ParamAwareConverter)converter).convert(object, paramName);
                } else {
                    value = converter.convert(object);
                }
                potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, value));
            }
        } 
        return potentialAssignments;
    }

}
