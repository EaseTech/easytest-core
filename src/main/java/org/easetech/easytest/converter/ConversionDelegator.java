
package org.easetech.easytest.converter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.internal.DateTimeFormat;
import org.easetech.easytest.internal.EasyParamSignature;
import org.junit.experimental.theories.PotentialAssignment;

/**
 * A Delegator class that is responsible for converting the raw data into user specified type.
 * It implements the {@link BaseConverter#convert(Object)} method.  
 * It works as follows :
 * <li> If the data is already converted by the user(lets say while loading from the Data Loader), it returns the data as it is.</li>
 * <li> If the data is not converted, it calls the registered converters one by one to convert the raw data into user specified format.</li>
 * <li> If still the data is not converted , it throws assertion failure exception </li>
 * 
 * @author Anuj Kumar
 *
 */
public class ConversionDelegator implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {

    /**
     * An instance of {@link EasyParamSignature} that contains information about the aignature of the test method.
     */
    private final EasyParamSignature signature;

    /**
     * The name of the parameter for which the data is being converted
     */
    private final String paramName;

    /**
     * An internal Map of converters that are used to convert raw data into user specified format.
     */
    private final Map<String , BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>>> converters = new LinkedHashMap<String , BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>>>();
    
    /**
     * Constant key for specifying collections converter identified by {@link DataAlreadyConvertedConverter}
     */
    private static final String DATA_ALREADY_CONVERTED_CONVERTER = "dataAlreadyConvertedConverter";
    
    /**
     * Constant key for specifying Map Object converter identified by {@link MapConverter}
     */
    private static final String MAP_OBJECT_CONVERTER = "mapObjectConverter";
    
    /**
     * Constant key for specifying Standard Object converter identified by {@link StandardObjectConverter}
     */
    private static final String STANDARD_OBJECT_CONVERTER = "standardObjectConverter";
    
    /**
     * Constant key for specifying Property Editor converter identified by {@link PropertyEditorConverter}
     */
    private static final String PROPERTY_EDITOR_CONVERTER = "propertyEditorConverter";
    
    /**
     * Constant key for specifying User Defined converter identified by {@link UserDefinedConverter}
     */
    private static final String USER_DEFINED_CONVERTER = "userDefinedConverter";
    
    /**
     * Constant key for specifying JSON data converter identified by {@link JSONDataConverter}
     */
    private static final String JSON_DATA_CONVERTER = "jsonDataConverter";
    
    /**
     * Constant key for specifying param constructor converter identified by {@link ParamConstructorConverter}
     */
    private static final String PARAM_CONSTRUCTOR_CONVERTER = "paramConstructorConverter";
    
    /**
     * Constant key for specifying collections converter identified by {@link CollectionConverter}
     */
    private static final String COLLECTIONS_CONVERTER = "collectionsConverter";

    /**
     * 
     * Construct a new ConversionDelegator
     * 
     * @param signature the {@link EasyParamSignature} instance that contains all the information regarding the parameter whose data is currently being converted.
     * @param paramName the optional name of the parameter with which to search for the data.
     * @param convertEmptyToNull whether an empty string be converted to Null or not
     * @param dateTimeFormat User specified date time format
     */
    public ConversionDelegator(EasyParamSignature signature, String paramName , Boolean convertEmptyToNull , DateTimeFormat dateTimeFormat) {
        this.signature = signature;
        this.paramName = paramName;
        converters.put(DATA_ALREADY_CONVERTED_CONVERTER, new DataAlreadyConvertedConverter(signature.getParameterType(), paramName, convertEmptyToNull));
        converters.put(MAP_OBJECT_CONVERTER, new MapConverter(signature.getParameterType()));
        converters.put(COLLECTIONS_CONVERTER, new CollectionConverter(signature, paramName , convertEmptyToNull , dateTimeFormat));
        converters.put(STANDARD_OBJECT_CONVERTER, new StandardObjectConverter(signature.getParameterType(), paramName , convertEmptyToNull , dateTimeFormat));
        converters.put(PROPERTY_EDITOR_CONVERTER, new PropertyEditorConverter(signature.getParameterType(), paramName));
        converters.put(USER_DEFINED_CONVERTER, new UserDefinedConverter(signature.getParameterType(), paramName , convertEmptyToNull , dateTimeFormat));
        converters.put(JSON_DATA_CONVERTER, new JSONDataConverter(signature.getParameterType(), paramName));
        converters.put(PARAM_CONSTRUCTOR_CONVERTER, new ParamConstructorConverter(signature.getParameterType(), paramName , null , convertEmptyToNull , dateTimeFormat));
        
    }
    
    /**
     * 
     * Construct a new ConversionDelegator
     * 
    * @param signature the {@link EasyParamSignature} instance that contains all the information regarding the parameter whose data is currently being converted.
     * @param paramName the optional name of the parameter with which to search for the data.
     * @param converters The list of converters that a user can specify. 
     */
    public ConversionDelegator(EasyParamSignature signature, String paramName, LinkedHashMap<String , BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>>> converters) {
        this.signature = signature;
        this.paramName = paramName;
        this.converters.putAll(converters);
        
        
    }

    /**
     * Convert the raw data into a list of {@link PotentialAssignment} instance 
     * that EasyTest uses to provide the right set of test data to the test method
     * @param convertFrom the raw data to convert from 
     * @return the list of {@link PotentialAssignment} instances
     */
    public List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom) {
        List<PotentialAssignment> potentialAssignments = null;
        for(String key : converters.keySet()) {
            potentialAssignments = converters.get(key).convert(convertFrom);
            if(potentialAssignments != null) {
                break;
            }
        }
        return potentialAssignments;
    }

}
