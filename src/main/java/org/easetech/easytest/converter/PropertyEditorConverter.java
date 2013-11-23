package org.easetech.easytest.converter;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
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
 * using any registered Property Editors
 * 
 * @author Anuj Kumar
 *
 */
public class PropertyEditorConverter implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {
    
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(PropertyEditorConverter.class);
    
    /**
     * The type of parameter to convert the raw data to
     */
    private final Class<?> parameterType;

    /**
     * The name of the parameter that is being converted
     */
    private final String paramName;

    /**
     * 
     * Construct a new PropertyEditorConverter
     * @param parameterType The type of parameter to convert the raw data to
     * @param paramName The name of the parameter that is being converted
     */
    public PropertyEditorConverter(Class<?> parameterType, String paramName) {
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
        LOG.debug("Trying to convert the data using any registered Property Editors. The class responsible for conversion is {}" , PropertyEditorConverter.class.getSimpleName());
        List<PotentialAssignment> potentialAssignments = null;
        PropertyEditor editor = PropertyEditorManager.findEditor(parameterType);
        if (editor != null) {
            potentialAssignments = new ArrayList<PotentialAssignment>();
            LOG.debug("Editor for class {} found", parameterType);
            for (Map<String, Object> object : convertFrom) {
                if (paramName != null && !EMPTY_STRING.equals(paramName)) {
                    if (GeneralUtil.getStringValue(paramName, object) != null) {
                        editor.setAsText(GeneralUtil.getStringValue(paramName, object));
                    }

                } else {
                    if(GeneralUtil.getStringValue(parameterType.getSimpleName(), object) != null){
                        editor.setAsText(GeneralUtil.getStringValue(parameterType.getSimpleName(), object));
                    }
                }
                // add data to PotentialAssignment even if it is null
                potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, editor.getValue()));

            }

        }
        return potentialAssignments;
    }


}
