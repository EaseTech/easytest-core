package org.easetech.easytest.converter;

import org.easetech.easytest.internal.DateTimeFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.util.GeneralUtil;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * An instance of {@link BaseConverter} that is responsible for converting the raw data in to a user defined type instance 
 * using the type's constructor
 * 
 * @author Anuj Kumar
 *
 */
public class ParamConstructorConverter implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {

    /**
     * The type of parameter to convert to
     */
    private final Class<?> parameterType; 

    /**
     * The name of the parameter that is being converted
     */
    private final String paramName;
    
    /**
     * The optional collection instance in case of Collection type parameter
     */
    private final Collection collection;
    
    /**
     * Whether empty values should be converted to null or not
     */
    private final Boolean convertEmptyToNull;
    
    /**
     * the user specified date time format to use
     */
    private final DateTimeFormat dateTimeFormat;
    
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(JSONDataConverter.class);
    
    /**
     * 
     * Construct a new ParamConstructorConverter
     * @param parameterType The type of parameter to convert to
     * @param paramName The name of the parameter that is being converted
     * @param collection The optional collection instance in case of Collection type parameter
     * @param convertEmptyToNull whether empty string be converted to null or not
     * @param dateTimeFormat the user specified date time format to use
     */
    public ParamConstructorConverter(Class<?> parameterType, String paramName, Collection collection, Boolean convertEmptyToNull , DateTimeFormat dateTimeFormat) {
        this.parameterType = parameterType;
        this.paramName = paramName;
        this.collection = collection;
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
        LOG.debug("Trying to call the constructor, if any of the class {} to populate the data" , parameterType);
        List<PotentialAssignment> potentialAssignments = new ArrayList<PotentialAssignment>();
        Boolean populated = false;
        try {
            populated = GeneralUtil.fillDataUsingConstructor(parameterType, convertFrom, potentialAssignments, paramName, collection , convertEmptyToNull , dateTimeFormat);
        } catch (Exception e) {
            LOG.debug("Exception occured while trying to populate the data by instantiating the parameter object" , e);
            potentialAssignments = null;
        }
        if(!populated) {
            potentialAssignments = null;
        }
        return potentialAssignments;
        
    }

}
