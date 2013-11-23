
package org.easetech.easytest.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import org.easetech.easytest.util.GeneralUtil;

import java.util.List;
import java.util.Map;
import org.junit.experimental.theories.PotentialAssignment;

/**
 * 
 * An instance of {@link BaseConverter} that is responsible for getting the already converted data and 
 * creating a list of {@link PotentialAssignment}s that is understood by EasyTest Framework.
 * 
 * @author Anuj Kumar
 *
 */
public class DataAlreadyConvertedConverter implements
    BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {
    
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(DataAlreadyConvertedConverter.class);

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
     * 
     * Construct a new DataAlreadyConvertedConverter
     * @param parameterType The type of parameter to convert the raw data to
     * @param paramName The name of the parameter that is being converted
     * @param convertEmptyToNull Whether empty values should be converted to Null values or not
     */
    public DataAlreadyConvertedConverter(Class<?> parameterType, String paramName, Boolean convertEmptyToNull) {
        super();
        this.parameterType = parameterType;
        this.paramName = paramName;
        this.convertEmptyToNull = convertEmptyToNull;
    }

    /**
     * Handle the data that has already been converted by the user.
     * @param convertFrom the data to handle
     * @return the list of PotentialAssignemnts
     */
    public List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom) {
        LOG.debug("Trying to verify whether the data has already been converted by the user or not");
        List<PotentialAssignment> potentialAssignments = null;
        if (GeneralUtil.dataAlreadyConverted(parameterType, convertFrom, paramName)) {
            LOG.debug("Data is converted by the user. Filling it up");
            potentialAssignments = new ArrayList<PotentialAssignment>();
            Object value = null;
            for (Map<String, Object> object : convertFrom) {
                if (String.class.isAssignableFrom(parameterType)) {
                    if (convertEmptyToNull) {
                        if (object.get(paramName) != null && "".equals(object.get(paramName).toString())) {
                            potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, value));
                        } else {
                            potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, object.get(paramName)));
                        }
                    } else {
                        potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, object.get(paramName)));
                    }
                } else {
                    potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, object.get(paramName)));
                }

            }
        }
        return potentialAssignments;
    }

}
