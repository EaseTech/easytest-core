package org.easetech.easytest.converter;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.internal.EasyParamSignature;
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
public class PropertyEditorCollectionConverter implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {
    
    /**
     * an instance of {@link EasyParamSignature}
     */
    private final EasyParamSignature signature;
    
    /**
     * The name of the parameter that is being converted
     */
    private final String paramName;
    
    /**
     * The collection instance in case of Collection type parameter
     */
    private final Collection collection;
    
    
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(PropertyEditorCollectionConverter.class);
    
    /**
     * 
     * Construct a new PropertyEditorCollectionConverter
     * @param signature an instance of {@link EasyParamSignature}
     * @param paramName  The name of the parameter that is being converted
     * @param collection The collection instance in case of Collection type parameter
     */
    public PropertyEditorCollectionConverter(EasyParamSignature signature, String paramName, Collection collection) {
        super();
        this.signature = signature;
        this.paramName = paramName;
        this.collection = collection;
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
        Class<?> genericType = signature.getIsGenericParameter() ? signature.getGenericParameterArgType()
            : Object.class;
        PropertyEditor editor = PropertyEditorManager.findEditor(genericType);
        if (editor != null) {
            potentialAssignments = new ArrayList<PotentialAssignment>();
            LOG.debug("Editor for class {} found", genericType);
            for (Map<String, Object> object : convertFrom) {
                String strValue;
                if (paramName != null && !EMPTY_STRING.equals(paramName)) {
                    strValue = GeneralUtil.getStringValue(paramName, object);
                } else {
                    strValue = GeneralUtil.getStringValue(genericType.getSimpleName(), object);
                }
                if (strValue != null) {
                    String[] values = strValue.split(COLON);
                    for (int i = 0; i < values.length; i++) {
                        editor.setAsText(values[i]);
                        if (editor.getValue() != null) {
                            collection.add(editor.getValue());
                        }
                    }
                }
                potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, collection));
            }

        }
        return potentialAssignments;
    }
    
    

}
