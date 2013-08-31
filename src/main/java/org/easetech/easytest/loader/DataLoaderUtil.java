
package org.easetech.easytest.loader;

import org.easetech.easytest.internal.SystemProperties;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.io.EmptyResource;
import org.easetech.easytest.io.Resource;
import org.easetech.easytest.io.ResourceLoader;
import org.easetech.easytest.io.ResourceLoaderStrategy;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.easetech.easytest.util.DataContext;
import org.easetech.easytest.util.TestInfo;
import org.junit.Assert;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Utility class for loading test data. This class has methods to identify information about loading the data
 * 
 * @author Anuj Kumar
 * 
 */
public final class DataLoaderUtil {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(DataLoaderUtil.class);

    /** Pattern for runtime expression */
    private static final Pattern RUNTIME_EXPR_PATTERN = Pattern.compile("\\$\\{.*\\}");


    /**
     * Method that determines the right Loader and the right Data Files for the "write output data" functionality
     * supported by the EasyTest Framework.
     * 
     * @param testData an instance of {@link DataLoader} that helps in identifying the right {@link Loader} to write the
     *            data back to the file.
     * @param testClass the class that the {@link TestInfo} object will be associated with
     * 
     * @return {@link TestInfo} an instance of {@link TestInfo} containing information about the currently executing
     *         test.
     */
    public static TestInfo determineLoader(DataLoader testData, TestClass testClass) {
        TestInfo result = new TestInfo(testClass);
        // String[] dataFiles = testData.filePaths();
        String[] dataFiles = determineFilePaths(testData);
        LoaderType loaderType = determineLoaderType(testData, dataFiles);
        // Loader
        Loader dataLoader = null;
        if (LoaderType.CUSTOM.equals(loaderType) || dataFiles.length == 0) {
            dataLoader = getCustomLoaderInstance(testData);
        } else {
            // user has specified data files and the data fileType is also
            // not custom.
            if (loaderType != null) {
                dataLoader = LoaderFactory.getLoader(loaderType);
            }

        }
        result.setDataLoader(dataLoader);
        result.setFilePaths(dataFiles);
        return result;
    }

    /**
     * Determine the Path of the test data files
     * 
     * @param dataLoader the {@link DataLoader} annotation
     * @return an array of resolved file names
     */
    private static String[] determineFilePaths(DataLoader dataLoader) {
        String[] filePaths = dataLoader.filePaths();
        String[] result = new String[filePaths.length];
        result = handleSystemProperty();
        if (result == null) {
            if(filePaths == null || filePaths.length == 0) {
                LOG.warn("Neither System Property 'testDataFiles' nor 'filePaths' attribute is specified.");
            }
            result = new String[filePaths.length];
            for (int i = 0; i < filePaths.length; i++) {
                if (isVariablePath(filePaths[i])) {
                    result[i] = getTestDataFileExpression(filePaths[i]);
                } else {
                    result[i] = filePaths[i];
                }
            }
        }
        return result;

    }

    /**
     * Get the test data file expression from the variable path
     * 
     * @param pathExpression
     * @return
     */
    private static String getTestDataFileExpression(String pathExpression) {
        String variable = pathExpression.substring(2, pathExpression.lastIndexOf("}"));
        return System.getProperty(variable) == null ? variable : System.getProperty(variable);
    }

    private static String[] handleSystemProperty() {
        String[] result = null;
        String datafiles = System.getProperty(SystemProperties.TEST_DATA_FILES.getValue());
        if (datafiles == null || datafiles.length() == 0) {
            LOG.warn("Input Test data not provided. Assuming that user has a custom Data Loader.");
        } else {
            result = datafiles.split(",");
        }
        return result;
    }

    private static Boolean isVariablePath(String path) {
        if (path == null || "".equals(path)) {
            return false;
        }
        Matcher exprMatcher = RUNTIME_EXPR_PATTERN.matcher(path);
        if (exprMatcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * Determine the Loader Type to load the test data
     * 
     * @param testData the {@link DataLoader} annotation instance
     * @param dataFiles the list of test data Files
     * @return {@link LoaderType} to use
     */
    private static final LoaderType determineLoaderType(DataLoader testData, String[] dataFiles) {
        LoaderType loaderType = testData.loaderType();
        if (LoaderType.NONE.equals(loaderType)) {
            // Identify the file extension
            if (dataFiles.length == 0) {
                // assume it is custom loader and return
                return LoaderType.CUSTOM;
            }
            // Since we currently support only a single file type in the FilePaths attribute,
            // we can safely pick one of the file, determine its extension and assume that other files have the same
            // extension
            return resolveFileExtension(dataFiles[0]);

        }
        return loaderType;

    }

    /**
     * Return the laoder type based on file extension
     * 
     * @param filePath the file path
     * @return {@link LoaderType} based on file extension
     */
    private static LoaderType resolveFileExtension(String filePath) {
        if (filePath == null || filePath == "") {
            return LoaderType.CUSTOM;
        }
        if (filePath.endsWith("csv")) {
            return LoaderType.CSV;
        } else if (filePath.endsWith("xls")) {
            return LoaderType.EXCEL;
        } else if (filePath.endsWith("xml")) {
            return LoaderType.XML;
        }
        return LoaderType.CUSTOM;

    }

    private static Loader getCustomLoaderInstance(DataLoader testData) {
        Loader dataLoader = null;
        LOG.info("User specified to use custom Loader. Trying to get the custom loader.");
        if (testData.loader() == null) {
            Assert.fail("Specified the LoaderType as CUSTOM but did not specify loader"
                + " attribute. A loaderType of CUSTOM requires the loader " + "attribute specifying "
                + "the Custom Loader Class which implements Loader interface.");
        } else {
            try {
                Class<? extends Loader> loaderClass = testData.loader();
                dataLoader = loaderClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Exception occured while trying to instantiate a class of type :"
                    + testData.loader(), e);
            }
        }
        return dataLoader;

    }

    /**
     * Load the Data for the given class or method. This method will try to find {@link DataLoader} on either the class
     * level or the method level. In case the annotation is found, this method will load the data using the specified
     * loader class and then save it in the DataContext for further use by the system. We also create another copy of
     * the input test data that we store in the {@link DataDrivenTestRunner#writableData} field. This is done in order
     * to facilitate the writing of the data that might be returned by the test method.
     * 
     * @param testClass the class object, if any.
     * @param method current executing method, if any.
     * @param currentTestClass the currently executing test class. this is used to append in front of the method name to
     *            get unique method names as there could be methods in different classes with the same name and thus we
     *            want to avoid conflicts.
     * @param writableData The writable data that is used internally for reporting purposes
     */

    public static void loadData(Class<?> testClass, FrameworkMethod method, TestClass currentTestClass,
        Map<String, List<Map<String, Object>>> writableData) {
        if (testClass == null && method == null) {
            Assert
                .fail("The framework should provide either the testClass parameter or the method parameter in order to load the test data.");
        }
        // We give priority to Class Loading and then to method loading
        DataLoader testData = null;
        if (testClass != null) {
            testData = testClass.getAnnotation(DataLoader.class);
        } else {
            testData = method.getAnnotation(DataLoader.class);
        }
        if (testData != null) {
            TestInfo testInfo = DataLoaderUtil.determineLoader(testData, currentTestClass);
            Loader dataLoader = testInfo.getDataLoader();
            if (testInfo.getDataLoader() == null) {
                Assert.fail("The framework currently does not support the specified Loader type. "
                    + "You can provide the custom Loader by choosing LoaderType.CUSTOM in TestData "
                    + "annotation and providing your custom loader using DataLoader annotation.");
            } else {
                if (testInfo.getFilePaths() == null || testInfo.getFilePaths().length == 0) {
                    // implies that there exists a CUSTOM loader that loads the data using Java classes
                    Map<String, List<Map<String, Object>>> data = dataLoader.loadData(new EmptyResource());
                    // We also maintain the copy of the actual data for our write functionality.
                    writableData.putAll(data);
                    DataContext.setData(DataConverter.appendClassName(data, currentTestClass.getJavaClass()));
                    DataContext.setConvertedData(DataConverter.convert(data, currentTestClass.getJavaClass()));
                } else {
                    ResourceLoader resourceLoader = new ResourceLoaderStrategy(currentTestClass.getJavaClass());
                    for (String filePath : testInfo.getFilePaths()) {
                        Resource resource = resourceLoader.getResource(filePath);
                        try {
                            if (resource.exists()) {
                                Map<String, List<Map<String, Object>>> data = dataLoader.loadData(resource);
                                // We also maintain the copy of the actual data for our write functionality.
                                writableData.putAll(data);
                                DataContext
                                    .setData(DataConverter.appendClassName(data, currentTestClass.getJavaClass()));
                                DataContext.setConvertedData(DataConverter.convert(data,
                                    currentTestClass.getJavaClass()));
                            } else {
                                LOG.warn(
                                    "Resource {} does not exists in the specified path. If it is a classpath resource, use 'classpath:' "
                                        + "before the path name, else check the path.", resource);
                            }
                        } catch (Exception e) {
                            LOG.error("Exception occured while trying to load the data for resource {}", resource, e);
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if the data for the given method is loaded or not.
     * 
     * @param methodName the name of the method whose data needs to be checked.
     * @return true if there exists data for the given method, else false.
     */
    public static boolean isMethodDataLoaded(String methodName) {

        boolean result = false;
        if (DataContext.getData() == null || DataContext.getData().keySet() == null
            || DataContext.getData().keySet().isEmpty()) {
            result = false;
        } else {
            Iterator<String> keyIterator = DataContext.getData().keySet().iterator();
            while (keyIterator.hasNext()) {
                result = methodName.equals(keyIterator.next()) ? true : false;
                if (result) {
                    break;
                }
            }
        }

        return result;
    }

}
