
package org.easetech.easytest.util;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

import org.easetech.easytest.internal.DateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains common utils
 * 
 * @author gpcmol
 * 
 */
//PENDING: Refactor this class to make it easier to use
public class GeneralUtil {

    private static final Logger LOG = LoggerFactory.getLogger(GeneralUtil.class);

    private static final String FILE_SEPARATOR = String.valueOf(File.separatorChar);

    private static final String NULL_STR = "null";

    private static final String EMPTY_STRING = "";

    private static final String COLON = ":";

    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{.*\\}");

    private static final Pattern ARRAY_PATTERN = Pattern.compile("\\[.*\\]");

    /**
     * Rounds a value with number of decimals
     * 
     * @param valueToRound
     * @param numberOfDecimalPlaces
     * @return rounded double
     */
    public static Double getRounded(double valueToRound, int numberOfDecimalPlaces) {
        BigDecimal bigDecimal = BigDecimal.valueOf(valueToRound).setScale(numberOfDecimalPlaces, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
    
    /**
     * Util method to get the String value
     * 
     * @param paramName the name of the parameter to get the String value for
     * @param data the data that contains the include Holdings value
     * @return String value or null if it is not set in the data.
     */
    public static String getStringValue(String paramName, Map<String, Object> data) {
        return data.get(paramName) != null && !data.get(paramName).equals("null") ? data.get(paramName).toString()
            : null;

    }

    /**
     * Create directory
     * 
     * @param destinationFolder
     * @return
     */
    public static String createDefaultOutputFolder(String destinationFolder) {
        if (destinationFolder == null || destinationFolder.equals("")) {
            destinationFolder = System.getProperty("user.dir") + File.separatorChar + "target" + File.separatorChar
                + "reports";
        }
        return createFolder(destinationFolder);
    }

    public static String getCurrentFolder() {
        File file = new File("");
        return file.getAbsolutePath();
    }

    /**
     * Creates directory. If absolute location is empty, pick current folder
     * 
     * @param absoluteLocation
     * @return directory
     */
    public static String createFolder(String absoluteLocation) {
        if (absoluteLocation == null) {
            return null;
        }
        File file = new File(absoluteLocation);
        if (!file.isDirectory()) {
            try {
                FileUtils.forceMkdir(file);
            } catch (IOException e) {
                LOG.error("Error creating directory " + absoluteLocation + " (" + e.getMessage() + ")");
            }
        }
        return absoluteLocation;
    }


    /**
     * Returns absulute path of either the classpath of file location
     * 
     * @param location
     * @return absolute location
     */
    public static String getAbsoluteLocation(String location) {
        String path = null;

        if (location.equals("")) {
            return GeneralUtil.getCurrentFolder();
        }

        if (location.startsWith("file:")) {
            path = location.substring(location.indexOf(":") + 1, location.length());
        } else if (location.startsWith("classpath:")) {
            path = location.substring(location.indexOf(":") + 1, location.length());
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResource(".");
            try {
                File file = new File(url.toURI());
                String tempPath = file.toString();
                if (!tempPath.endsWith(FILE_SEPARATOR)) {
                    tempPath += FILE_SEPARATOR;
                }
                path = path.replace("/", FILE_SEPARATOR);
                path = tempPath + path;
            } catch (URISyntaxException e) {
                LOG.error("URI exception ", e);
            }
        } else {
            LOG.error("Report output location " + location + " not found");
        }
        return path;
    }

    /**
     * Method to convert object to java.sql.Timestamp type It checks the instance of the object is of different datatype
     * then it gets the value from the object and casts it to required data type.
     * @param object The object to convert to SQL Timestamp
     * @param dateTimeFormat the date time format to use
     * @return converted timestamp value
     */

    public static Timestamp convertToSQLTimestamp(Object object , DateTimeFormat dateTimeFormat) {
        Timestamp timestamp = null;
        if (object != null && !object.toString().isEmpty()) {
            timestamp = new java.sql.Timestamp(convertToUtilDate(object , dateTimeFormat).getTime());
        }

        return timestamp;
    }

    /**
     * Method to convert object to java.util.Date type It checks the instance of the object is of different datatype
     * then it gets the value from the object and casts it to required data type.
     * @param object the object to convert to date
     * @param dateTimeFormat the date and time format to use to convert 
     * @return java.util.Date converted value.
     */

    public static Date convertToUtilDate(Object object , DateTimeFormat dateTimeFormat) {
        Date date = null;
        if (object != null && !object.toString().isEmpty()) {
            if (object instanceof java.util.Date) {
                date = (java.util.Date) object;
            } else if (object instanceof Double) {
                date = new Date(((Double) object).longValue());
            } else if (object instanceof String) {
                try {
                    String[] formats = (String[]) ArrayUtils.addAll(dateTimeFormat.getDateTimeFormats(), dateTimeFormat.getDateFormats());
                    String[] availableFormats = (String[]) ArrayUtils.addAll(formats, dateTimeFormat.getTimeFormats());
                    
                    date = DateUtils.parseDate((String) object, availableFormats);
                    
                } catch (ParseException e) {
                    LOG.error("Parse exception occured while trying to convert {} to java util date using formats {}", object , dateTimeFormat);
                    throw new RuntimeException(e);
                }

            }
        }

        return date;
    }

    /**
     * Method to convert object to java.sql.Date type It checks the instance of the object is of different datatype then
     * it gets the value from the object and casts it to required data type.
     * @param object the object to convert to SQL Date 
     * @param dateTimeFormat the user specified date time format
     * @return java.sql.Date converted value.
     */

    public static java.sql.Date convertToSQLDate(Object object , DateTimeFormat dateTimeFormat) {
        java.sql.Date sqlDate = null;
        if (object != null && !object.toString().isEmpty()) {
            sqlDate = new java.sql.Date(convertToUtilDate(object , dateTimeFormat).getTime());
            
        }

        return sqlDate;
    }

    /**
     * Method to convert object to java.sql.Tim type It checks the instance of the object is of different datatype then
     * it gets the value from the object and casts it to required data type.
     * @param object the object to convert to SQL Time 
     * @param dateTimeFormat the date time format to use
     * @return java.sql.Tim converted value.
     */

    public static java.sql.Time convertToSQLTime(Object object , DateTimeFormat dateTimeFormat) {
        java.sql.Time time = null;
        if (object != null && !object.toString().isEmpty()) {

            if (object instanceof java.util.Date) {
                time = new java.sql.Time(((Date) object).getTime());
            } else if (object instanceof Double) {
                time = new java.sql.Time(((Double) object).longValue());
            } else if (object instanceof String) {
                Date date;
                try {
                    date = DateUtils.parseDate((String) object, dateTimeFormat.getTimeFormats());
                } catch (ParseException e) {
                    LOG.debug("Parse Exception occured while trying to convert to SQL TimeStamp. " +
                    		"The object to convert to : {} and the fomat used to convert to SQL Time : {}" ,
                    		object, dateTimeFormat.getTimeFormats());
                    date = new Date(Long.valueOf((String) object));
                }
                time = new java.sql.Time(date.getTime());
            }
        }

        return time;
    }

    /**
     * Method to convert object to Integer type It checks the instance of the object is of different datatype then it
     * gets the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return Integer converted value.
     */
    public static Integer convertToInteger(Object object) {
        Integer integer = null;
        if (object != null && !object.toString().isEmpty()) {
            if (object instanceof Long) {
                integer = ((Long) object).intValue();
            } else if (object instanceof Integer) {
                integer = (Integer) object;
            } else if (object instanceof Double) {
                integer = ((Double) object).intValue();
            } else if (object instanceof String) {
                integer = Integer.valueOf((String) object);
            }
        }
        return integer;
    }

    /**
     * Method to convert object to Short type It checks the instance of the object is of different datatype then it gets
     * the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return Short converted value.
     */
    public static Short convertToShort(Object object) {
        Short shortValue = null;
        if (object != null && !object.toString().isEmpty()) {
            if (object instanceof Long) {
                shortValue = ((Long) object).shortValue();
            } else if (object instanceof Integer) {
                shortValue = ((Integer) object).shortValue();
            } else if (object instanceof Double) {
                shortValue = ((Double) object).shortValue();
            } else if (object instanceof String) {
                shortValue = Short.valueOf((String) object);
            }
        }
        return shortValue;
    }

    /**
     * Method to convert object to Long type It checks the instance of the object is of different datatype then it gets
     * the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return Long converted value.
     */
    public static Long convertToLong(Object object) {
        Long longvalue = null;
        if (object != null) {
            if (object instanceof Long) {
                longvalue = (Long) object;
            } else if (object instanceof Integer) {
                longvalue = ((Integer) object).longValue();
            } else if (object instanceof Double) {
                longvalue = ((Double) object).longValue();
            } else if (object instanceof String) {
                String strLongValue = (String) object;
                if (!"".equalsIgnoreCase(strLongValue)) {
                    longvalue = Long.valueOf((String) object);
                } 
            }
        }
        return longvalue;
    }

    /**
     * Method to convert object to Double type It checks the instance of the object is of different datatype then it
     * gets the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return Double converted value.
     */
    public static Double convertToDouble(Object object) {
        Double doublevalue = null;
        if (object != null && !object.toString().isEmpty()) {
            if (object instanceof Long) {
                doublevalue = ((Long) object).doubleValue();
            } else if (object instanceof Integer) {
                doublevalue = ((Integer) object).doubleValue();
            } else if (object instanceof Double) {
                doublevalue = (Double) object;
            } else if (object instanceof String && !"".equals((String)object)) {
                doublevalue = Double.valueOf((String) object);
            }
        }
        return doublevalue;
    }

    /**
     * Method to convert object to Float type It checks the instance of the object is of different datatype then it gets
     * the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return Float converted value.
     */
    public static Float convertToFloat(Object object) {
        Float floatvalue = null;
        if (object != null && !object.toString().isEmpty()) {
            if (object instanceof Long) {
                floatvalue = ((Long) object).floatValue();
            } else if (object instanceof Integer) {
                floatvalue = ((Integer) object).floatValue();
            } else if (object instanceof Double) {
                floatvalue = ((Double) object).floatValue();
            } else if (object instanceof String && !"".equals((String)object)) {
                floatvalue = Float.valueOf((String) object);
            }
        }
        return floatvalue;
    }

    /**
     * Method to convert object to Boolean type It checks the instance of the object is of different datatype then it
     * gets the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return Boolean converted value.
     */
    public static Boolean convertToBoolean(Object object) {
        Boolean booleanValue = null;
        if (object != null && !object.toString().isEmpty()) {
            if (object instanceof Long) {
                booleanValue = stringToBoolean(((Long) object).toString());
            } else if (object instanceof Integer) {
                booleanValue = stringToBoolean(((Integer) object).toString());
            } else if (object instanceof Double) {
                booleanValue = stringToBoolean(((Double) object).toString());
            } else if (object instanceof String && !"".equals((String)object)) {
                booleanValue = stringToBoolean((String) object);
            }
        }
        return booleanValue;
    }

    /**
     * Method to convert object to Byte type It checks the instance of the object is of different datatype then it gets
     * the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return Byte converted value.
     */
    public static Byte convertToByte(Object object) {
        Byte byteValue = null;
        if (object != null && !object.toString().isEmpty()) {
            if (object instanceof Long) {

                byteValue = Byte.valueOf(((Long) object).toString());
            } else if (object instanceof Integer) {
                byteValue = Byte.valueOf(((Integer) object).toString());
            } else if (object instanceof Double) {
                byteValue = Byte.valueOf(((Double) object).toString());
            } else if (object instanceof String && !"".equals((String)object)) {
                byteValue = Byte.valueOf((String) object);
            }
        }
        return byteValue;
    }

    /**
     * Method to convert object to Character type It checks the instance of the object is of different datatype then it
     * gets the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return Character converted value.
     */
    public static Character convertToCharacter(Object object) {
        java.lang.Character chValue = null;
        if (object != null && !object.toString().isEmpty()) {
            if (object instanceof Long) {
                chValue = Character.valueOf((char) ((Long) object).longValue());
            } else if (object instanceof Integer) {
                chValue = Character.valueOf((char) ((Integer) object).intValue());
            } else if (object instanceof Double) {
                chValue = Character.valueOf((char) ((Double) object).doubleValue());
            } else if (object instanceof String && !"".equals((String)object)) {
                chValue = Character.valueOf(((String) object).charAt(0));
            } else if (object instanceof Character) {
                chValue = (Character) object;
            }
        }
        return chValue;
    }

    /**
     * Method to convert object to String type It returns toString value of object.
     * 
     * @param Object object
     * @return Character converted value.
     */
    public static String convertToString(Object object) {
        return (object == null) ? null : object.toString();
    }
    
    /**
     * Method to convert object to String type It returns toString value of object.
     * 
     * @param Object object
     * @return Character converted value.
     */
    public static StringBuilder convertToStringBuffer(Object object) {
        
        return (object == null) ? null : new StringBuilder(object.toString());
    }
    
    /**
     * Method to convert object to String type It returns toString value of object.
     * 
     * @param Object object
     * @return Character converted value.
     */
    public static StringBuilder convertToStringBuilder(Object object) {
    	
        return (object == null) ? null : new StringBuilder(object.toString());
    }
    
    

    /**
     * Method to convert object to Character type It checks the instance of the object is of different datatype then it
     * gets the value from the object and casts it to required data type.
     * @param idClass 
     * @param object 
     * @return Character converted value.
     */
    @SuppressWarnings("unchecked")
    public static Enum convertToEnum(Class idClass, Object object) {
        Enum enumValue = null;
        if (object != null && idClass.isEnum()) {
            enumValue = Enum.valueOf(idClass, convertToString(object));
        }
        return enumValue;
    }

    /**
     * Method to convert string to Boolean type It checks the instance of the object is of different datatype then it
     * gets the value from the object and casts it to required data type.
     * 
     * @param str the string to convert to boolean
     * @return Boolean converted value.
     */

    private static Boolean stringToBoolean(String str) {
        Boolean value = null;
        if (str != null) {
            if (str.equalsIgnoreCase("True")) {
                value = true;
            } else {
                value = false;
            }
        }
        return value;
    }
    
    /**
     * Is the data already converted by the user while loading the data.
     * This can happen when a user has its own custom loader and converts the data
     * while loading it. This may not be advisable as it brings in extra coupling
     * but is still possible as sometimes it may be very simple to convert the data while loading it.
     * @param parameterType the type of the input parameter
     * @param convertFrom the data to convert from
     * @param paramName the name of the parameter
     * @return whether the data is already converted by the user while loading the data
     */
    @SuppressWarnings({ "unchecked"})
    public static Boolean dataAlreadyConverted(Class parameterType , List<Map<String, Object>> convertFrom , String paramName) {
        Boolean result = false;
        for(Map<String , Object> data : convertFrom) {
            Object value = data.get(paramName);
            if(value != null && parameterType.isAssignableFrom(value.getClass())) {
                result = true;
                break;
            } else {
                //We just want to check the first instance 
                break;
            }
        }
        return result;
    }

    /**
     * Convert the given Class object to an object instance if possible
     * 
     * @param idClass
     * @param object
     * @param convertEmptyToNull 
     * @return a converted object
     */
    public static Object convertToTargetType(Class<?> idClass, Object object , Boolean convertEmptyToNull , DateTimeFormat dateTimeFormat) {
        Object returnObj = null;
        if (object == null || NULL_STR.equals(object.toString())) {
            return null;
        }
        if(convertEmptyToNull && "".equals(object.toString())) {
            return null;
        }
        if (String.class.isAssignableFrom(idClass)) {
            returnObj = convertToString(object);
        } else if (Timestamp.class.isAssignableFrom(idClass)) {
            returnObj = convertToSQLTimestamp(object , dateTimeFormat);
        } else if (Time.class.isAssignableFrom(idClass)) {
            returnObj = GeneralUtil.convertToSQLTime(object , dateTimeFormat);
        } else if (java.sql.Date.class.isAssignableFrom(idClass)) {
            returnObj = convertToSQLDate(object , dateTimeFormat);
        } else if (Date.class.isAssignableFrom(idClass)) {
            returnObj = convertToUtilDate(object , dateTimeFormat);
        } else if (Double.class.isAssignableFrom(idClass) || double.class.isAssignableFrom(idClass)) {
            returnObj = convertToDouble(object);
        } else if (Float.class.isAssignableFrom(idClass) || float.class.isAssignableFrom(idClass)) {
            returnObj = convertToFloat(object);
        } else if (Long.class.isAssignableFrom(idClass) || long.class.isAssignableFrom(idClass)) {
            returnObj = convertToLong(object);
        } else if (Integer.class.isAssignableFrom(idClass) || int.class.isAssignableFrom(idClass)) {
            returnObj = convertToInteger(object);
        } else if (Boolean.class.isAssignableFrom(idClass) || boolean.class.isAssignableFrom(idClass)) {
            returnObj = convertToBoolean(object);
        } else if (Byte.class.isAssignableFrom(idClass) || byte.class.isAssignableFrom(idClass)) {
            returnObj = convertToByte(object);
        } else if (Character.class.isAssignableFrom(idClass) || char.class.isAssignableFrom(idClass)) {
            returnObj = convertToCharacter(object);
        } else if (Short.class.isAssignableFrom(idClass) || short.class.isAssignableFrom(idClass)) {
            returnObj = convertToShort(object);
        } else if (Enum.class.isAssignableFrom(idClass)) {
            returnObj = convertToEnum(idClass, object);
        } else if (StringBuffer.class.isAssignableFrom(idClass)) {
            returnObj = convertToStringBuffer(object);
        } else if (StringBuilder.class.isAssignableFrom(idClass)) {
            returnObj = convertToStringBuilder(object);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Could not find either Editor or Converter instance for class :" + idClass);
            }
            Assert.fail("Could not find either Editor or Converter instance for class :" + idClass);
        }
        return returnObj;
    }

    public static boolean isStandardObjectInstance(Class<?> idClass) {
        boolean result = false;
        if (String.class.isAssignableFrom(idClass) || Timestamp.class.isAssignableFrom(idClass)
            || Time.class.isAssignableFrom(idClass) || java.sql.Date.class.isAssignableFrom(idClass)
            || Date.class.isAssignableFrom(idClass) || Double.class.isAssignableFrom(idClass)
            || double.class.isAssignableFrom(idClass) || Float.class.isAssignableFrom(idClass)
            || float.class.isAssignableFrom(idClass) || Long.class.isAssignableFrom(idClass)
            || long.class.isAssignableFrom(idClass) || Integer.class.isAssignableFrom(idClass)
            || int.class.isAssignableFrom(idClass) || Boolean.class.isAssignableFrom(idClass)
            || boolean.class.isAssignableFrom(idClass) || Byte.class.isAssignableFrom(idClass)
            || byte.class.isAssignableFrom(idClass) || Character.class.isAssignableFrom(idClass)
            || char.class.isAssignableFrom(idClass) || Short.class.isAssignableFrom(idClass)
            || short.class.isAssignableFrom(idClass) || Enum.class.isAssignableFrom(idClass)
            || StringBuffer.class.isAssignableFrom(idClass) || StringBuilder.class.isAssignableFrom(idClass)) {
            result = true;
        }
        return result;
    }

    public static Boolean populateJSONData(Class<?> idClass, List<Map<String, Object>> convertFrom,
        List<PotentialAssignment> potentialAssignments, String paramName){
        Boolean result = false;
        Object value = null;
        for (Map<String, Object> object : convertFrom) {
            if (paramName != null && !EMPTY_STRING.equals(paramName)){
                value = object.get(paramName);
                if(value != null && isJSON(value.toString())){
                    handleJSONData(value.toString(),idClass , potentialAssignments);
                    result = true;
                }
            }else{
                value = object.get(idClass.getSimpleName());
                if(value != null && isJSON(value.toString())){
                    handleJSONData(value.toString(),idClass , potentialAssignments);
                    result = true;
                }
            }
        }
        return result;
        
    }

    public static <T> void handleJSONData(String expr, Class<T> idClass, List<PotentialAssignment> potentialAssignments) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            T value = mapper.readValue(expr, idClass);
            potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, value));
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static Boolean isJSON(String expression) {
        if (expression == null || "".equals(expression)) {
            return false;
        }
        Matcher objectMatcher = OBJECT_PATTERN.matcher(expression);
        Matcher arrayMatcher = ARRAY_PATTERN.matcher(expression);
        if (objectMatcher.matches() || arrayMatcher.matches()) {
            return true;
        }
        return false;
    }

    public static Boolean fillDataUsingConstructor(Class<?> idClass, List<Map<String, Object>> convertFrom,
        List<PotentialAssignment> finalData, String paramName, Collection collectionInstance , Boolean convertEmptyToNull , DateTimeFormat dateTimeFormat)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        // See if the parameter class has a constructor that we can use.
        Constructor constructor = getConstructor(idClass, Long.class) == null ? getConstructor(idClass, long.class)
            : getConstructor(idClass, Long.class);
        if (constructor != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Long.class, collectionInstance , convertEmptyToNull , dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, String.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, String.class, collectionInstance , convertEmptyToNull , dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Integer.class) == null ? getConstructor(idClass, int.class)
            : getConstructor(idClass, Integer.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Integer.class, collectionInstance, convertEmptyToNull , dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Byte.class) == null ? getConstructor(idClass, byte.class)
            : getConstructor(idClass, Byte.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Byte.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Character.class) == null ? getConstructor(idClass, char.class)
            : getConstructor(idClass, Character.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Character.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Date.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Date.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, java.util.Date.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, java.util.Date.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Timestamp.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Timestamp.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Time.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Time.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Double.class) == null ? getConstructor(idClass, double.class)
            : getConstructor(idClass, Double.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Double.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Float.class) == null ? getConstructor(idClass, float.class)
            : getConstructor(idClass, Float.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Float.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Boolean.class) == null ? getConstructor(idClass,
            boolean.class) : getConstructor(idClass, Boolean.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Boolean.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Short.class) == null ? getConstructor(idClass, short.class)
            : getConstructor(idClass, Boolean.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Short.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else if ((constructor = getConstructor(idClass, Enum.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Enum.class, collectionInstance, convertEmptyToNull, dateTimeFormat);
        } else {
            return false;
        }
        return true;

    }

    @SuppressWarnings({ "unused" })
    private static <T> Boolean fill(Class idClass, String paramName, Constructor constructor,
        List<PotentialAssignment> finalData, List<Map<String, Object>> convertFrom, Class<T> argType,
        Collection collectionInstance , Boolean convertEmptyToNull , DateTimeFormat dateTimeFormat) throws IllegalArgumentException, InstantiationException, IllegalAccessException,
        InvocationTargetException {
        if (GeneralUtil.isStandardObjectInstance(argType)) {
            for (Map<String, Object> object : convertFrom) {
                if (collectionInstance != null) {
                    fillCollectionData(idClass, object, paramName, constructor, finalData, argType, collectionInstance, convertEmptyToNull , dateTimeFormat);
                } else {
                    fillData(idClass, object, paramName, constructor, finalData, argType, convertEmptyToNull , dateTimeFormat);
                }

            }
            return true;
        } else {
            return false;
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> void fillCollectionData(Class<?> idClass, Map<String, Object> object, String paramName,
        Constructor constructor, List<PotentialAssignment> finalData, Class<T> argType, Collection collectionInstance, Boolean convertEmptyToNull , DateTimeFormat dateTimeFormat)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object result = null;
        T target = null;

        if (paramName != null && !EMPTY_STRING.equals(paramName)) {
            String[] strValues = ((String) object.get(paramName)).split(COLON);
            for (int i = 0; i < strValues.length; i++) {
                target = (T) GeneralUtil.convertToTargetType(argType, strValues[i], convertEmptyToNull , dateTimeFormat);
                result = constructor.newInstance(target);
                collectionInstance.add(result);
            }
            finalData.add(PotentialAssignment.forValue(EMPTY_STRING, collectionInstance));
        } else {
            String[] strValues = ((String) object.get(idClass.getSimpleName())).split(COLON);
            for (int i = 0; i < strValues.length; i++) {
                target = (T) GeneralUtil.convertToTargetType(argType, strValues[i], convertEmptyToNull, dateTimeFormat);
                result = constructor.newInstance(target);
                collectionInstance.add(result);
            }
            finalData.add(PotentialAssignment.forValue(EMPTY_STRING, collectionInstance));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void fillData(Class<?> idClass, Map<String, Object> object, String paramName,
        Constructor constructor, List<PotentialAssignment> finalData, Class<T> argType, Boolean convertEmptyToNull , DateTimeFormat dateTimeFormat)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object result = null;
        T target = null;

        if (paramName != null && !EMPTY_STRING.equals(paramName)) {
            target = (T) GeneralUtil.convertToTargetType(argType, object.get(paramName), convertEmptyToNull, dateTimeFormat);
            result = constructor.newInstance(target);
            finalData.add(PotentialAssignment.forValue(EMPTY_STRING, result));
        } else {
            result = constructor.newInstance((T) GeneralUtil.convertToTargetType(argType,
                object.get(idClass.getSimpleName()), convertEmptyToNull, dateTimeFormat));
            finalData.add(PotentialAssignment.forValue(EMPTY_STRING, result));
        }
    }

    private static Constructor getConstructor(Class<?> idClass, Class<?> paramType) {
        Constructor<?> constructor = null;
        try {
            constructor = idClass.getConstructor(paramType);
        } catch (SecurityException e) {
            // do nothing
        } catch (NoSuchMethodException e) {
            // do nothing
        }
        return constructor;
    }

    public static Constructor getConstructor(Class<?> idClass) {
        Constructor constructor = getConstructor(idClass, Long.class) == null ? getConstructor(idClass, long.class)
            : getConstructor(idClass, Long.class);
        if (constructor != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, String.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Integer.class) == null ? getConstructor(idClass, int.class)
            : getConstructor(idClass, Integer.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Byte.class) == null ? getConstructor(idClass, byte.class)
            : getConstructor(idClass, Byte.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Character.class) == null ? getConstructor(idClass, char.class)
            : getConstructor(idClass, Character.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Date.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, java.util.Date.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Timestamp.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Time.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Double.class) == null ? getConstructor(idClass, double.class)
            : getConstructor(idClass, Double.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Float.class) == null ? getConstructor(idClass, float.class)
            : getConstructor(idClass, Float.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Boolean.class) == null ? getConstructor(idClass,
            boolean.class) : getConstructor(idClass, Boolean.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Short.class) == null ? getConstructor(idClass, short.class)
            : getConstructor(idClass, Boolean.class)) != null) {
            return constructor;
        } else if ((constructor = getConstructor(idClass, Enum.class)) != null) {
            return constructor;
        }
        return null;
    }
}
