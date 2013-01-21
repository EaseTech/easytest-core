
package org.easetech.easytest.util;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains common utils
 * 
 * @author gpcmol
 * 
 */
public class GeneralUtil {

    private static final Logger LOG = LoggerFactory.getLogger(GeneralUtil.class);

    private static final String FILE_SEPARATOR = String.valueOf(File.separatorChar);

    private static final String NULL_STR = "null";

    private static final String EMPTY_STRING = "";

    private static final String COLON = ":";

    /**
     * Rounds a value with number of decimals
     * 
     * @param valueToRound
     * @param numberOfDecimalPlaces
     * @return rounded double
     */
    public static Double getRounded(double valueToRound, int numberOfDecimalPlaces) {
        BigDecimal bigDecimal = new BigDecimal(valueToRound).setScale(numberOfDecimalPlaces, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
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
        String absolutePath = file.getAbsolutePath();
        return absolutePath;
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
     * Format date with pattern ddMMyyyyHHmmss
     * 
     * @param date
     * @return string with formatted date
     */
    public static String getFormattedDate(Date date) {
        DateFormat instance = new SimpleDateFormat("ddMMyyyyHHmmss");
        return instance.format(date);
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
     * 
     * @param Object object
     * @return java.sql.Timestamp converted value.
     */

    public static Timestamp convertToSQLTimestamp(Object object) {
        Timestamp timestamp = null;
        if (object != null) {
            timestamp = new java.sql.Timestamp(convertToUtilDate(object).getTime());
            /*
             * if(object instanceof java.util.Date){ timestamp = new Timestamp(((java.util.Date) object).getTime()); }
             * else if(object instanceof Double){ timestamp = new Timestamp(((Double) object).longValue()); } else
             * if(object instanceof String){ timestamp = new Timestamp(Long.valueOf((String) object)); }
             */
        }

        return timestamp;
    }

    /**
     * Method to convert object to java.util.Date type It checks the instance of the object is of different datatype
     * then it gets the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return java.util.Date converted value.
     */

    public static Date convertToUtilDate(Object object) {
        Date date = null;
        if (object != null) {
            if (object instanceof java.util.Date) {
                date = (java.util.Date) object;
            } else if (object instanceof Double) {
                date = new Date(((Double) object).longValue());
            } else if (object instanceof String) {
                try {
                    date = DateUtils.parseDate((String) object, new String[] { "dd/MM/yy", "dd/MM/yyyy", "MM/dd/yy",
                        "MM/dd/yyyy", "dd-MM-yy", "dd-MM-YYYY", "MM-dd-yy", "MM-dd-yyyy", "dd/MM/yy HH:MM:SS",
                        "dd/MM/yyyy HH:MM:SS", "MM/dd/yy HH:MM:SS", "MM/dd/yyyy HH:MM:SS", "dd-MM-yy HH:MM:SS",
                        "dd-MM-YYYY HH:MM:SS", "MM-dd-yy HH:MM:SS", "MM-dd-yyyy HH:MM:SS", "HH:MM:SS" });
                } catch (ParseException e) {
                    date = new Date(Long.valueOf((String) object));
                }

            }
        }

        return date;
    }

    /**
     * Method to convert object to java.sql.Date type It checks the instance of the object is of different datatype then
     * it gets the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return java.sql.Date converted value.
     */

    public static java.sql.Date convertToSQLDate(Object object) {
        java.sql.Date sqlDate = null;
        if (object != null) {
            sqlDate = new java.sql.Date(convertToUtilDate(object).getTime());
            ;
        }

        return sqlDate;
    }

    /**
     * Method to convert object to java.sql.Tim type It checks the instance of the object is of different datatype then
     * it gets the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return java.sql.Tim converted value.
     */

    public static java.sql.Time convertToSQLTime(Object object) {
        java.sql.Time time = null;
        if (object != null) {

            if (object instanceof java.util.Date) {
                time = new java.sql.Time(((Date) object).getTime());
            } else if (object instanceof Double) {
                time = new java.sql.Time(((Double) object).longValue());
            } else if (object instanceof String) {
                Date date;
                try {
                    date = DateUtils.parseDate((String) object, new String[] { "HH:MM:SS" });
                } catch (ParseException e) {
                    date = new Date(Long.valueOf((String) object));
                }
                time = new java.sql.Time(date.getTime());
                ;
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
        if (object != null) {
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
        if (object != null) {
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
                longvalue = ((Long) object).longValue();
            } else if (object instanceof Integer) {
                longvalue = ((Integer) object).longValue();
            } else if (object instanceof Double) {
                longvalue = ((Double) object).longValue();
            } else if (object instanceof String) {
                String strLongValue = (String) object;
                if ("".equalsIgnoreCase(strLongValue)) {
                    longvalue = (long) 0;
                } else {
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
        if (object != null) {
            if (object instanceof Long) {
                doublevalue = ((Long) object).doubleValue();
            } else if (object instanceof Integer) {
                doublevalue = ((Integer) object).doubleValue();
            } else if (object instanceof Double) {
                doublevalue = ((Double) object).doubleValue();
            } else if (object instanceof String) {
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
        if (object != null) {
            if (object instanceof Long) {
                floatvalue = ((Long) object).floatValue();
            } else if (object instanceof Integer) {
                floatvalue = ((Integer) object).floatValue();
            } else if (object instanceof Double) {
                floatvalue = ((Double) object).floatValue();
            } else if (object instanceof String) {
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
        if (object != null) {
            if (object instanceof Long) {
                booleanValue = stringToBoolean(((Long) object).toString());
            } else if (object instanceof Integer) {
                booleanValue = stringToBoolean(((Integer) object).toString());
            } else if (object instanceof Double) {
                booleanValue = stringToBoolean(((Double) object).toString());
            } else if (object instanceof String) {
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
        if (object != null) {
            if (object instanceof Long) {

                byteValue = Byte.valueOf(((Long) object).toString());
            } else if (object instanceof Integer) {
                byteValue = Byte.valueOf(((Integer) object).toString());
            } else if (object instanceof Double) {
                byteValue = Byte.valueOf(((Double) object).toString());
            } else if (object instanceof String) {
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
        if (object != null) {
            if (object instanceof Long) {
                chValue = Character.valueOf((char) ((Long) object).longValue());
            } else if (object instanceof Integer) {
                chValue = Character.valueOf((char) ((Integer) object).intValue());
            } else if (object instanceof Double) {
                chValue = Character.valueOf((char) ((Double) object).doubleValue());
            } else if (object instanceof String) {
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
     * Method to convert object to Character type It checks the instance of the object is of different datatype then it
     * gets the value from the object and casts it to required data type.
     * 
     * @param Object object
     * @return Character converted value.
     */
    public static Enum convertToEnum(Class idClass, Object object) {
        Enum enumValue = null;
        if (object != null && idClass.isEnum()) {
            enumValue = Enum.valueOf(idClass, (String) convertToString(object));
        }
        return enumValue;
    }

    /**
     * Method to convert string to Boolean type It checks the instance of the object is of different datatype then it
     * gets the value from the object and casts it to required data type.
     * 
     * @param String
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
     * Convert the given Class object to an object instance if possible
     * 
     * @param idClass
     * @param object
     * @return
     */
    public static Object convertToTargetType(Class<?> idClass, Object object) {
        Object returnObj = null;
        if (object == null || NULL_STR.equals(object.toString())) {
            return null;
        }

        if (String.class.isAssignableFrom(idClass)) {
            returnObj = convertToString(object);
        } else if (Timestamp.class.isAssignableFrom(idClass)) {
            returnObj = convertToSQLTimestamp(object);
        } else if (Time.class.isAssignableFrom(idClass)) {
            returnObj = GeneralUtil.convertToSQLTime(object);
        } else if (java.sql.Date.class.isAssignableFrom(idClass)) {
            returnObj = convertToSQLDate(object);
        } else if (Date.class.isAssignableFrom(idClass)) {
            returnObj = convertToUtilDate(object);
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
            || short.class.isAssignableFrom(idClass) || Enum.class.isAssignableFrom(idClass)) {
            result = true;
        }
        return result;
    }

    /**
     * Method responsible for calling a constructor on an object to try to fill it with data.
     * 
     * @param idClass
     * @param convertFrom
     * @param finalData
     * @param paramName
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void fillDataUsingConstructor(Class<?> idClass, List<Map<String, Object>> convertFrom,
        List<PotentialAssignment> finalData, String paramName) throws IllegalArgumentException, InstantiationException,
        IllegalAccessException, InvocationTargetException {
        fillDataUsingConstructor(idClass, convertFrom, finalData, paramName, null);

    }

    public static void fillDataUsingConstructor(Class<?> idClass, List<Map<String, Object>> convertFrom,
        List<PotentialAssignment> finalData, String paramName, Collection collectionInstance)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        // See if the parameter class has a constructor that we can use.
        Constructor constructor = getConstructor(idClass, Long.class) == null ? getConstructor(idClass, long.class)
            : getConstructor(idClass, Long.class);
        if (constructor != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Long.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, String.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, String.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Integer.class) == null ? getConstructor(idClass, int.class)
            : getConstructor(idClass, Integer.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Integer.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Byte.class) == null ? getConstructor(idClass, byte.class)
            : getConstructor(idClass, Byte.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Byte.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Character.class) == null ? getConstructor(idClass, char.class)
            : getConstructor(idClass, Character.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Character.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Date.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Date.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, java.util.Date.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, java.util.Date.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Timestamp.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Timestamp.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Time.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Time.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Double.class) == null ? getConstructor(idClass, double.class)
            : getConstructor(idClass, Double.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Double.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Float.class) == null ? getConstructor(idClass, float.class)
            : getConstructor(idClass, Float.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Float.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Boolean.class) == null ? getConstructor(idClass,
            boolean.class) : getConstructor(idClass, Boolean.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Boolean.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Short.class) == null ? getConstructor(idClass, short.class)
            : getConstructor(idClass, Boolean.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Short.class, collectionInstance);
        } else if ((constructor = getConstructor(idClass, Enum.class)) != null) {
            fill(idClass, paramName, constructor, finalData, convertFrom, Enum.class, collectionInstance);
        }

    }

    @SuppressWarnings({ "unused", "unchecked" })
    private static <T> void fill(Class idClass, String paramName, Constructor constructor,
        List<PotentialAssignment> finalData, List<Map<String, Object>> convertFrom, Class<T> argType, Collection collectionInstance)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (GeneralUtil.isStandardObjectInstance(argType)) {
            for (Map<String, Object> object : convertFrom) {
                T target = null;
                Object result = null;
                Object inputData = object.get(paramName);
                if(collectionInstance != null){
                    fillCollectionData(idClass, object, paramName, constructor, finalData, argType, collectionInstance);
                }else{
                    fillData(idClass, object, paramName, constructor, finalData, argType);
                }
                
            }
        }
    }

    private static <T> void fillCollectionData(Class<?> idClass, Map<String, Object> object, String paramName,
        Constructor constructor, List<PotentialAssignment> finalData, Class<T> argType, Collection collectionInstance)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object result = null;
        T target = null;

        if (paramName != null && !EMPTY_STRING.equals(paramName)) {
            String[] strValues = ((String) object.get(paramName)).split(COLON);
            for (int i = 0; i < strValues.length; i++) {
                target = (T) GeneralUtil.convertToTargetType(argType, strValues[i]);
                result = constructor.newInstance(target);
                collectionInstance.add(result);
            }          
            finalData.add(PotentialAssignment.forValue(EMPTY_STRING, collectionInstance));
        } else {
            String[] strValues = ((String) object.get(idClass.getSimpleName())).split(COLON);           
            for (int i = 0; i < strValues.length; i++) {
                target = (T) GeneralUtil.convertToTargetType(argType, strValues[i]);
                result = constructor.newInstance(target);
                collectionInstance.add(result);
            }          
            finalData.add(PotentialAssignment.forValue(EMPTY_STRING, collectionInstance));
        }
    }

    private static <T> void fillData(Class<?> idClass, Map<String, Object> object, String paramName,
        Constructor constructor, List<PotentialAssignment> finalData, Class<T> argType)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Object result = null;
        T target = null;

        if (paramName != null && !EMPTY_STRING.equals(paramName)) {
            target = (T) GeneralUtil.convertToTargetType(argType, object.get(paramName));
            result = constructor.newInstance(target);
            finalData.add(PotentialAssignment.forValue(EMPTY_STRING, result));
        } else {
            result = constructor.newInstance((T) GeneralUtil.convertToTargetType(argType,
                object.get(idClass.getSimpleName())));
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
}
