package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.easetech.easytest.loader.EmptyLoader;
import org.easetech.easytest.loader.Loader;
import org.easetech.easytest.loader.LoaderType;

/**
 * 
 * A method or class level annotation providing users with the ability to specify a data {@link Loader} strategy for their test class.
 * EasyTest supports CSV, EXCEL and XML based data loading. But it may not be sufficient in all the cases.
 * Also EasyTest's Data Loading Strategy may not suit every user. In such a case, a user can write his own Custom Loader and pass it to the Data Loader annotation
 * to supply its own custom Loader.<br>
 * 
 * For eg. this is how you can use it :<br>
 * <code>
 *   {@literal @}Test
 *   {@literal @}DataLoader(loader=MyCustomDataLoader.class)<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 *OR
 *<br><br>
 *<code>
 *   {@literal @}Test
 *   {@literal @}DataLoader(filePaths={testData.json} , loader=MyCustomDataLoader.class)<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 *
 *OR
 *
 *<br>
 *<code>
 *   {@literal @}Test
 *   {@literal @}DataLoader(filePaths={testData.csv})<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 *
 *OR
 *
 *<br>
 *<code>
 *   {@literal @}Test
 *   {@literal @}DataLoader(filePaths={testDataExcel.xls})<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 * Note that the custom Loader must implement the {@link Loader} interface and should have a no arg constructor.
 * <br>
 * The data loader annotation also supports the functionality for the user to specify whether the data should be written back to the test file or not
 * using the attribute {@link DataLoader#writeData()}. Its default value is {@link Boolean#TRUE}.
 * <BR>
 * A user can also specify whether the test data should be overrideen or should be appended in case the test data is present in more than one file. 
 * The user can do that using the {@link #appendData()} attribute. If the value is True, then the data from different files is preserved and the test is run 
 * for each row of test data specified in each test data file. if the value is False(also the default value), then the test data in file 1 is overridden with test data in file 2.
 * 
 * <BR>
 * A new System Property "testDataFiles" to provide a comma separated list of input test data files at runtime. 
 * In order to use this option simply specify @DataLoader annotation at the top of your class without any input data. 
 * Thus in such a case DataLoader annotation acts as a marker annotation telling the EasyTest system that it has to fetch the value 
 * of filePaths attribute from the system property "testDataFiles".
 * 
 * <BR>
 * <B>NOTE</B> If a user has specified both "testDataFiles" System Property AND a value for "dataFiles" attribute, 
 * then the System Property files(specified using testDataFiles System Property) will override the files specified 
 * using the "dataFiles" attribute of DataLoader annotation.
 * 
 * @author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD , ElementType.TYPE})
@Inherited
public @interface DataLoader {
    
    /** 
     * OPTIONAL
     * The list of file paths representing the input test data for the given test method. 
     * In scenarios where the custom {@link DataLoader} represented by {@link #loader()} attribute
     * knows about the test data, this field can be left blank. 
     * <br>
     * This attribute also takes variable path as input. for eg. a User can specify the file path to be something like :
     * <pre>
     * <code>
     *  @DataLoader(filePaths={"${my.data.file}" , "${my.second.data.file}"})
     * </code>
     * </pre> 
     * Using the above way, a user can specify properties of the above variables 
     * <B>"my.data.file"</B> and <B>"my.second.data.file"</B> as System Property using -D option of Java System Properties.
     */
    String[] filePaths() default {};

    /**
     * OPTIONAL 
     * The type of file that contains the data. Defaults to "none".
     * This attribute has become optional mostly because EasyTest can figure out the type of loader,
     * based on the extension of the test-data file. It is mostly used to give hint to EasyTest.
     * It is also used to specify that the loader is Custom and not a standard loader.
     */
    LoaderType loaderType() default LoaderType.NONE;

    
    /**
     * OPTIONAL 
     * The custom Loader class that will be used by EasyTest to load the test data.
     * In case of standard DataLoaders for Excel, CSV and XML, you dont have to provide any Loader.
     * This attribute is used only when the {@link LoaderType} is Custom.*/
    Class<? extends Loader> loader() default EmptyLoader.class;
    
    /**
     * OPTIONAL
     * Boolean identifying whether the data should be written back to the file or not. 
     * Default behavior is that the data will be written back to the file. Data from a test method is written back to the file
     * only when the test method returns something. In case the test method returns void, 
     * then even though this attribute is set to true, nothing will be written back to the file.
     */
    boolean writeData() default true;
    
    /**
     * OPTIONAL
     * Boolean identifying whether data specified in two different files for the same method
     * should be appended or replaced. Default behavior is to replace the data present in one file from the other.
     * @return whether data should be appended or not
     */
    boolean appendData() default false;

}
