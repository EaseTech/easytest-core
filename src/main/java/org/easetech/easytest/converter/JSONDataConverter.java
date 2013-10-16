package org.easetech.easytest.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.util.GeneralUtil;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * An instance of {@link BaseConverter} that is responsible for converting the raw JSON data in to auser defined type instance
 * 
 * @author Anuj Kumar
 *
 */
public class JSONDataConverter implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {
    
    /**
     * The type of parameter to convert to
     */
    private final Class<?> parameterType;

    /**
     * The name of the parameter that is being converted
     */
    private final String paramName;
    
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(JSONDataConverter.class);
    
    /**
     * 
     * Construct a new JSONDataConverter
     * @param parameterType The type of parameter to convert to
     * @param paramName The name of the parameter that is being converted
     */
    public JSONDataConverter(Class<?> parameterType, String paramName) {
        this.parameterType = parameterType;
        this.paramName = paramName;
    }

    /**
     * Convert the raw data into a list of {@link PotentialAssignment} instance 
     * that EasyTest uses to provide the right set of test data to the test method
     * @param convertFrom the raw data to convert from 
     * @return the list of {@link PotentialAssignment} instances
     */
    public List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom) {
        LOG.debug("Trying to see if the provided data is JSON Data. ");
        List<PotentialAssignment> potentialAssignments = new ArrayList<PotentialAssignment>();
        Boolean populated = GeneralUtil.populateJSONData(parameterType , convertFrom, potentialAssignments, paramName);
        if(!populated) {
            potentialAssignments = null;
        }
        return potentialAssignments;
    }

}
