
package org.easetech.easytest.converter;

import org.easetech.easytest.internal.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.util.GeneralUtil;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * An instance of {@link BaseConverter} that is responsible for converting the raw data in to a user defined type instance 
 * using any java standard object
 * 
 * @author Anuj Kumar
 *
 */
public class StandardObjectConverter implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {

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
    private static final Logger LOG = LoggerFactory.getLogger(StandardObjectConverter.class);
    
    /**
     * 
     * Construct a new StandardObjectConverter
     * @param parameterType The type of parameter to convert the raw data to
     * @param paramName The name of the parameter that is being converted
     * @param convertEmptyToNull Whether empty values should be converted to Null values or not
     * @param dateTimeFormat The user specified date time format to use
     */
    public StandardObjectConverter(Class<?> parameterType, String paramName , Boolean convertEmptyToNull , DateTimeFormat dateTimeFormat) {
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
    public List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom) {
        LOG.debug("Trying to convert the data to a standard Object instance using {}" , StandardObjectConverter.class.getSimpleName());
        List<PotentialAssignment> potentialAssignments = null;
        if (GeneralUtil.isStandardObjectInstance(parameterType)) {
            potentialAssignments = new ArrayList<PotentialAssignment>();
            for (Map<String, Object> object : convertFrom) {
                potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING,
                    GeneralUtil.convertToTargetType(parameterType, object.get(paramName) , convertEmptyToNull, dateTimeFormat)));
            }
        }
        
        return potentialAssignments;
    }

}
